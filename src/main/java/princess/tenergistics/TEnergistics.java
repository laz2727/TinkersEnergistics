package princess.tenergistics;

import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import princess.tenergistics.data.TagProvider;
import princess.tenergistics.data.ToolsRecipeProvider;
import princess.tenergistics.items.EnergisticsBookItem;
import princess.tenergistics.items.EnergisticsBookItem.EnergisticsBookType;
import princess.tenergistics.modifiers.CapacityModifier;
import princess.tenergistics.modifiers.EnergyCoilModifier;
import princess.tenergistics.modifiers.ExchangerModifier;
import princess.tenergistics.modifiers.FireboxModifier;
import princess.tenergistics.modifiers.RTGModifier;
import princess.tenergistics.recipes.RefuelFireboxRecipe;
import princess.tenergistics.tools.BuzzsawTool;
import princess.tenergistics.tools.JackhammerTool;
import princess.tenergistics.tools.ToolDefinitions;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.common.registration.ItemDeferredRegisterExtension;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.item.ToolPartItem;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.shared.CommonsClientEvents;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

@Mod(TEnergistics.modID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TEnergistics
	{
	public static final String															modID									= "tenergistics";
	
	public static TEnergistics															instance;
	
	public static final Logger															log										= LogManager
			.getLogger(modID);
	
	protected static final ItemDeferredRegisterExtension								ITEMS									= new ItemDeferredRegisterExtension(TEnergistics.modID);
	protected static final DeferredRegister<Modifier>									MODIFIERS								= DeferredRegister
			.create(Modifier.class, TEnergistics.modID);
	protected static final DeferredRegister<Attribute>									ATTRIBUTES								= DeferredRegister
			.create(ForgeRegistries.ATTRIBUTES, TEnergistics.modID);
	protected static final DeferredRegister<IRecipeSerializer<?>>						RECIPE_SERIALIZERS						= DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, TEnergistics.modID);
	
	private static final Supplier<Item.Properties>										TOOL									= () -> new Item.Properties()
			.group(TinkerTools.TAB_TOOLS);
	private static final Item.Properties												GENERAL_PROPS							= new Item.Properties()
			.group(TinkerModule.TAB_GENERAL);
	private static final Item.Properties												PARTS_PROPS								= new Item.Properties()
			.group(TinkerToolParts.TAB_TOOL_PARTS);
	private static final Item.Properties												SMELTERY_PROPS							= new Item.Properties()
			.group(TinkerSmeltery.TAB_SMELTERY);
	private static final Item.Properties												BOOK									= new Item.Properties()
			.group(TinkerModule.TAB_GENERAL)
			.maxStackSize(1);
	
	public static final ItemObject<EnergisticsBookItem>									miraculousMachinery						= ITEMS
			.register("miraculous_machinery", () -> new EnergisticsBookItem(BOOK, EnergisticsBookType.MIRACULOUS_MACHINERY));
	
	public static final ItemObject<ToolPartItem>										toolCasing								= ITEMS
			.register("tool_casing", () -> new ToolPartItem(PARTS_PROPS, ExtraMaterialStats.ID));
	public static final ItemObject<ToolPartItem>										gearbox									= ITEMS
			.register("gearbox", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
	
	public static final ItemObject<Item>												firebox									= ITEMS
			.register("firebox", GENERAL_PROPS);
	public static final ItemObject<Item>												exchanger								= ITEMS
			.register("exchanger", GENERAL_PROPS);
	public static final ItemObject<Item>												energyCoil								= ITEMS
			.register("energy_coil", GENERAL_PROPS);
	
	public static final ItemObject<ToolPartItem>										jackhammerRod							= ITEMS
			.register("jackhammer_rod", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
	public static final ItemObject<ToolPartItem>										buzzsawDisc								= ITEMS
			.register("buzzsaw_disc", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
	
	public static final CastItemObject													toolCasingCast							= ITEMS
			.registerCast("tool_casing", SMELTERY_PROPS);
	public static final CastItemObject													gearboxCast								= ITEMS
			.registerCast("gearbox", SMELTERY_PROPS);
	public static final CastItemObject													jackhammerRodCast						= ITEMS
			.registerCast("jackhammer_rod", SMELTERY_PROPS);
	public static final CastItemObject													buzzsawDiscCast							= ITEMS
			.registerCast("buzzsaw_disc", SMELTERY_PROPS);
	
	public static final ItemObject<JackhammerTool>										jackhammer								= ITEMS
			.register("jackhammer", () -> new JackhammerTool(TOOL.get()
					.addToolType(ToolType.PICKAXE, 0), ToolDefinitions.JACKHAMMER));
	
	public static final ItemObject<BuzzsawTool>											buzzsaw									= ITEMS
			.register("buzzsaw", () -> new BuzzsawTool(TOOL.get()
					.addToolType(ToolType.AXE, 0), ToolDefinitions.BUZZSAW));
	
	public static final RegistryObject<FireboxModifier>									fireboxModifier							= MODIFIERS
			.register("firebox", FireboxModifier::new);
	public static final RegistryObject<ExchangerModifier>								exchangerModifier						= MODIFIERS
			.register("exchanger", ExchangerModifier::new);
	public static final RegistryObject<EnergyCoilModifier>								energyCoilModifier						= MODIFIERS
			.register("energy_coil", EnergyCoilModifier::new);
	
	public static final RegistryObject<CapacityModifier>								capacityModifier						= MODIFIERS
			.register("capacity", CapacityModifier::new);
	public static final RegistryObject<RTGModifier>										rtgModifier								= MODIFIERS
			.register("rtg", RTGModifier::new);
	
	public static final RegistryObject<Attribute>										FAKE_HARVEST_SPEED						= ATTRIBUTES
			.register("generic.fake_harvest_speed", () -> new RangedAttribute(modID + ".attribute.name.generic.fake_harvest_speed", 1.0D, 0.0D, 2048.0D));
	
	public static final RegistryObject<SpecialRecipeSerializer<RefuelFireboxRecipe>>	tinkerStationFireboxRefuelSerializer	= RECIPE_SERIALIZERS
			.register("tinker_station_firebox_refuel", () -> new SpecialRecipeSerializer<>(RefuelFireboxRecipe::new));
	
	public TEnergistics()
		{
		instance = this;
		
		initRegisters();
		
		MinecraftForge.EVENT_BUS.register(this);
		
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> EnergisticsClient::onConstruct);
		}
		
	public static void initRegisters()
		{
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(bus);
		MODIFIERS.register(bus);
		ATTRIBUTES.register(bus);
		RECIPE_SERIALIZERS.register(bus);
		}
		
	@SubscribeEvent
	static void gatherData(final GatherDataEvent event)
		{
		if (event.includeServer())
			{
			DataGenerator datagenerator = event.getGenerator();
			ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
			
			TagProvider blockTagProvider = new TagProvider(datagenerator, modID, existingFileHelper);
			datagenerator.addProvider(blockTagProvider);
			datagenerator
					.addProvider(new TagProvider.ItemTag(datagenerator, blockTagProvider, modID, existingFileHelper));
//			datagenerator.addProvider(new TagProvider.FluidTag(datagenerator, modID, existingFileHelper));
			datagenerator.addProvider(new ToolsRecipeProvider(datagenerator));
			}
		}
		
	@EventBusSubscriber(modid = modID, value = Dist.CLIENT, bus = Bus.MOD)
	public static class EnergisticsClient
		{
		@SubscribeEvent
		static void clientSetup(final FMLClientSetupEvent event)
			{
			FontRenderer unicode = CommonsClientEvents.unicodeFontRender();
			EnergisticsBookItem.EnergisticsBook.MIRACULOUS_MACHINERY.fontRenderer = unicode;
			}
			
		public static void onConstruct()
			{
			EnergisticsBookItem.EnergisticsBook.initBook();
			}
			
		@SubscribeEvent
		static void itemColors(ColorHandlerEvent.Item event)
			{
			
			final ItemColors colors = event.getItemColors();
			
			// tint tool textures for fallback
			registerToolItemColors(colors, jackhammer);
			registerToolItemColors(colors, buzzsaw);
			
			registerMaterialItemColors(colors, toolCasing);
			registerMaterialItemColors(colors, gearbox);
			registerMaterialItemColors(colors, jackhammerRod);
			registerMaterialItemColors(colors, buzzsawDisc);
			}
			
		private static void registerMaterialItemColors(ItemColors colors, Supplier<? extends MaterialItem> item)
			{
			colors.register(materialColorHandler, item.get());
			}
			
		private static void registerToolItemColors(ItemColors colors, Supplier<? extends ToolCore> item)
			{
			colors.register(toolColorHandler, item.get());
			}
			
		private static final IItemColor	materialColorHandler	= (stack, index) -> {
																return Optional
																		.of(IMaterialItem.getMaterialFromStack(stack))
																		.filter((material) -> IMaterial.UNKNOWN != material)
																		.map(IMaterial::getIdentifier)
																		.flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
																		.map(MaterialRenderInfo::getVertexColor)
																		.orElse(-1);
																};
		private static final IItemColor	toolColorHandler		= (stack, index) -> {
																MaterialId material = MaterialIdNBT.from(stack)
																		.getMaterial(index);
																if (!IMaterial.UNKNOWN_ID.equals(material))
																	{
																	return MaterialRenderInfoLoader.INSTANCE
																			.getRenderInfo(material)
																			.map(MaterialRenderInfo::getVertexColor)
																			.orElse(-1);
																	}
																return -1;
																
																};
		}
	}
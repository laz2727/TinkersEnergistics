package princess.tinkersenergistics;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.eventbus.Subscribe;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import princess.tinkersenergistics.common.ConfigHandler;
import princess.tinkersenergistics.library.MachinePart;
import princess.tinkersenergistics.machines.MachineFurnace;
import princess.tinkersenergistics.proxy.CommonProxy;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.common.ModelRegisterUtil;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.TinkerRegistryClient;
import slimeknights.tconstruct.library.client.ToolBuildGuiInfo;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolPart;

@Pulse(id = ModInfo.MODID, description = ModInfo.DESCRIPTION, forced = ModInfo.TRUE, modsRequired = ModInfo.DEPEND)
public class TinkersEnergistics extends AbstractTinkerPulse
	{
	@SidedProxy(clientSide = ModInfo.CLIENTPROXY, serverSide = ModInfo.COMMONPROXY)
	public static CommonProxy		proxy;
	
	public static final Category	TIE_MACHINE	= new Category("tie_machine");
	
	public static ToolCore			furnace;
	/*
	public static ToolCore				crusher;
	
	public static ToolCore				converter;
	*/
	
	public static ToolPart			machineCasing;
	public static ToolPart			machineGearbox;
	public static ToolPart			machineHeater;
	/*
	public static ToolPart				machineMill;
	
	public static ToolPart				machineFirebox;
	public static ToolPart				machineExchanger;
	public static ToolPart				machineCoil;
	
	public static Item					carbonBall;
	*/
	
	private void registerMachines()
		{
		furnace = registerTool(new MachineFurnace(), "machine_furnace");
		}
		
	private void registerMachineParts()
		{
		machineCasing = registerToolPart(new MachinePart(Material.VALUE_Ingot * 8), "machine_casing");
		machineGearbox = registerToolPart(new MachinePart(Material.VALUE_Ingot * 4), "machine_gearbox");
		
		machineHeater = registerToolPart(new MachinePart(Material.VALUE_Ingot * 2), "machine_heater");
		
		/*
		machineMill = registerToolPart(new MachinePart(Material.VALUE_Ingot * 2), "machine_mill");
		
		machineFirebox = registerToolPart(new MachineModPart(Material.VALUE_Ingot * 1), "machine_firebox");
		machineExchanger = registerToolPart(new MachineModPart(Material.VALUE_Ingot * 1), "machine_exchanger");
		machineCoil = registerToolPart(new MachineModPart(Material.VALUE_Ingot * 1), "machine_coil");
		*/
		
		}
		
	private void registerToolBuilding()
		{
		TinkerRegistry.registerToolForgeCrafting(furnace);
		}
		
	@Subscribe
	public void preInit(FMLPreInitializationEvent event)
		{
		ConfigHandler.init(event.getSuggestedConfigurationFile());
		
		registerMachineParts();
		registerMachines();
		
		for (Pair<Item, ToolPart> toolPartPattern : toolPartPatterns)
			{
			registerStencil(toolPartPattern.getLeft(), toolPartPattern.getRight());
			}
			
		// NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
		
		proxy.preInit(event);
		}
		
	@Subscribe
	public void init(FMLInitializationEvent event)
		{
		registerToolBuilding();
		
		proxy.init(event);
		}
		
	@Subscribe
	public void postInit(FMLPostInitializationEvent event)
		{
		proxy.postInit(event);
		}
		
	public static class ClientProxy extends CommonProxy
		{
		
		@Override
		public void preInit(FMLPreInitializationEvent event)
			{
			super.preInit(event);
			
			toolParts.forEach(ModelRegisterUtil::registerPartModel);
			tools.forEach(ModelRegisterUtil::registerToolModel);
			/*
			List<IModifier> mods = new ArrayList<>();
			
			mods.add(TinkerModifiers.modSharpness);
			
			for (IModifier modifier : mods)
				ModelRegisterUtil.registerModifierModel(modifier, new ResourceLocation(ModInfo.MODID, "models/item/modifiers/" + modifier.getIdentifier()));
			*/
			}
			
		@Override
		public void postInit(FMLPostInitializationEvent event)
			{
			super.postInit(event);
			ToolBuildGuiInfo info = new ToolBuildGuiInfo(furnace);
			info.addSlotPosition(30, 64);
			info.addSlotPosition(7, 64);
			info.addSlotPosition(30, 44);
			TinkerRegistryClient.addToolBuilding(info);
			}
			
		@Override
		public boolean isServer()
			{
			return false;
			}
		}
	}
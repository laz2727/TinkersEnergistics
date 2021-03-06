package princess.tenergistics.data;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.IForgeRegistryEntry;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.recipes.ModifiedToolRecipeSerializer;
import slimeknights.mantle.recipe.SizedIngredient;
import slimeknights.mantle.recipe.ingredient.IngredientWithout;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.CompositeCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuelBuilder;
import slimeknights.tconstruct.library.recipe.ingredient.MaterialIngredient;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IncrementalModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierMatch;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.data.MaterialIds;

public class EnergisticsRecipeProvider extends RecipeProvider implements IConditionBuilder
	{
	//I swear to fuck, this has so much copypasta from TiC. I *wish* i could reuse their code, but i can't.
	public EnergisticsRecipeProvider(DataGenerator generator)
		{
		super(generator);
		}
		
	@Override
	public String getName()
		{
		return "Tinkers' Energistics Recipes";
		}
		
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
		{
		addMaterialRecipes(consumer);
		addPartRecipes(consumer);
		addTinkerStationRecipes(consumer);
		addForceFieldRecipes(consumer);
		addSmelteryRecipes(consumer);
		addMiscRecipes(consumer);
		}
		
	private void addMaterialRecipes(Consumer<IFinishedRecipe> consumer)
		{
		String compositeFolder = "tools/parts/composite/";
		registerMaterial(consumer, EnergisticsMaterialProvider.malachite, Ingredient
				.fromTag(ItemTags.makeWrapperTag("forge:ores/copper")), 2, 1, "malachite");
		registerMaterial(consumer, EnergisticsMaterialProvider.glaucodot, Ingredient
				.fromTag(ItemTags.makeWrapperTag("forge:ores/cobalt")), 2, 1, "glaucodot");
		
		CompositeCastingRecipeBuilder.table(MaterialIds.copper, EnergisticsMaterialProvider.malachite)
				.setFluid(new FluidStack(Fluids.WATER, FluidAttributes.BUCKET_VOLUME))
				.setTemperature(100)
				.build(consumer, location(compositeFolder + "malachite"));
		CompositeCastingRecipeBuilder.table(MaterialIds.stone, EnergisticsMaterialProvider.glaucodot)
				.setFluid(new FluidStack(TinkerFluids.moltenCobalt.get(), MaterialValues.INGOT))
				.build(consumer, location(compositeFolder + "glaucodot"));
		}
		
	private void addPartRecipes(Consumer<IFinishedRecipe> consumer)
		{
		addPartRecipe(consumer, TEnergistics.toolCasing, 8, TEnergistics.toolCasingCast);
		addPartRecipe(consumer, TEnergistics.gearbox, 4, TEnergistics.gearboxCast);
		
		addPartRecipe(consumer, TEnergistics.jackhammerRod, 4, TEnergistics.jackhammerRodCast);
		addPartRecipe(consumer, TEnergistics.bucketwheelWheel, 4, TEnergistics.bucketwheelWheelCast);
		addPartRecipe(consumer, TEnergistics.buzzsawDisc, 4, TEnergistics.buzzsawDiscCast);
		}
		
	private void addTinkerStationRecipes(Consumer<IFinishedRecipe> consumer)
		{
		String modifierFolder = "tools/modifiers/";
		String upgradeFolder = modifierFolder + "upgrade/";
		String powerGroup = "tenergistics:power_modifiers";
		
		addMachineBuildingRecipe(consumer, TEnergistics.jackhammer);
		addMachineBuildingRecipe(consumer, TEnergistics.bucketwheel);
		addMachineBuildingRecipe(consumer, TEnergistics.buzzsaw);
		
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.firebox)
				.key('-', TinkerSmeltery.searedBrick)
				.key('I', Items.IRON_INGOT)
				.key('n', Items.IRON_NUGGET)
				.patternLine("-I-")
				.patternLine("InI")
				.patternLine("-I-")
				.setGroup(powerGroup)
				.addCriterion("has_center", hasItem(Tags.Items.INGOTS_IRON))
				.build(consumer, prefix(TEnergistics.firebox, modifierFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.fireboxModifier.get())
				.addInput(TEnergistics.firebox)
				.setMaxLevel(1)
				.setTools(TagProvider.POWERED)
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.firebox, upgradeFolder));
		
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.exchanger)
				.key('-', TinkerSmeltery.searedBrick)
				.key('I', Items.IRON_INGOT)
				.key('n', Items.IRON_NUGGET)
				.patternLine("I--")
				.patternLine("InI")
				.patternLine("--I")
				.setGroup(powerGroup)
				.addCriterion("has_center", hasItem(Tags.Items.INGOTS_IRON))
				.build(consumer, prefix(TEnergistics.exchanger, modifierFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.exchangerModifier.get())
				.addInput(TEnergistics.exchanger)
				.setMaxLevel(1)
				.setTools(TagProvider.POWERED)
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.exchanger, upgradeFolder));
		
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.energyCoil)
				.key('-', TinkerSmeltery.searedBrick)
				.key('I', Items.IRON_INGOT)
				.key('n', Items.IRON_NUGGET)
				.patternLine("---")
				.patternLine("III")
				.patternLine("n-I")
				.setGroup(powerGroup)
				.addCriterion("has_center", hasItem(Tags.Items.INGOTS_IRON))
				.build(consumer, prefix(TEnergistics.energyCoil, modifierFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.energyCoilModifier.get())
				.addInput(TEnergistics.energyCoil)
				.setMaxLevel(1)
				.setTools(TagProvider.POWERED)
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.energyCoil, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.capacityModifier.get())
				.addInput(TinkerMaterials.copper.getIngotTag())
				.addInput(ItemTags.LOGS)
				.addInput(TinkerMaterials.copper.getIngotTag())
				.addInput(Tags.Items.INGOTS_IRON)
				.addInput(Tags.Items.INGOTS_IRON)
				.setUpgradeSlots(1)
				.setMaxLevel(3)
				.setTools(TinkerTags.Items.MODIFIABLE)
				.setRequirements(ModifierMatch.list(1, ModifierMatch.entry(TEnergistics.fireboxModifier
						.get()), ModifierMatch.entry(TEnergistics.exchangerModifier.get()), ModifierMatch
								.entry(TEnergistics.energyCoilModifier.get()), ModifierMatch.list(2, ModifierMatch
										.entry(TEnergistics.forceFieldModifier.get()), ModifierMatch
												.entry(TEnergistics.forceFireboxModifier.get())), ModifierMatch
														.list(2, ModifierMatch.entry(TEnergistics.forceFieldModifier
																.get()), ModifierMatch
																		.entry(TEnergistics.forceExchangerModifier
																				.get())), ModifierMatch
																						.list(2, ModifierMatch
																								.entry(TEnergistics.forceFieldModifier
																										.get()), ModifierMatch
																												.entry(TEnergistics.forceEnergyCoilModifier
																														.get()))))
				.setRequirementsError("recipe.tenergistics.modifier.capacity_requirements")
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.capacityModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.wideFunnelModifier.get())
				.addInput(Items.BUCKET)
				.addInput(Items.HOPPER)
				.addInput(Items.BUCKET)
				.setUpgradeSlots(1)
				.setMaxLevel(1)
				.setRequirements(ModifierMatch
						.list(1, ModifierMatch.entry(TEnergistics.exchangerModifier.get()), ModifierMatch
								.list(2, ModifierMatch.entry(TEnergistics.forceFieldModifier.get()), ModifierMatch
										.entry(TEnergistics.forceExchangerModifier.get()))))
				.setRequirementsError("recipe.tenergistics.modifier.wide_funnel_requirements")
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.wideFunnelModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.passthroughModifier.get())
				.addInput(SizedIngredient.fromItems(TinkerSmeltery.searedFaucet, TinkerSmeltery.scorchedFaucet))
				.addInput(Tags.Items.GLASS)
				.addInput(SizedIngredient.fromItems(TinkerSmeltery.searedFaucet, TinkerSmeltery.scorchedFaucet))
				.setUpgradeSlots(1)
				.setMaxLevel(1)
				.setRequirements(ModifierMatch
						.list(1, ModifierMatch.entry(TEnergistics.exchangerModifier.get()), ModifierMatch
								.list(2, ModifierMatch.entry(TEnergistics.forceFieldModifier.get()), ModifierMatch
										.entry(TEnergistics.forceExchangerModifier.get()))))
				.setRequirementsError("recipe.tenergistics.modifier.passthrough_requirements")
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.passthroughModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.rtgModifier.get())
				.addInput(TinkerFluids.blazingBlood.asItem())
				.addInput(TinkerMaterials.roseGold.getIngotTag())
				.addInput(TinkerFluids.blazingBlood.asItem())
				.addInput(Items.BLUE_ICE)
				.addInput(Items.BLUE_ICE)
				.setUpgradeSlots(1)
				.setMaxLevel(5)
				.setTools(TinkerTags.Items.MODIFIABLE)
				.setRequirements(ModifierMatch
						.list(1, ModifierMatch.entry(TEnergistics.energyCoilModifier.get()), ModifierMatch
								.list(2, ModifierMatch.entry(TEnergistics.forceFieldModifier.get()), ModifierMatch
										.entry(TEnergistics.forceEnergyCoilModifier.get()))))
				.setRequirementsError("recipe.tenergistics.modifier.coil_only")
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.rtgModifier, upgradeFolder));
		
		IncrementalModifierRecipeBuilder.modifier(TEnergistics.overclockModifier.get())
				.setInput(Items.REDSTONE_LAMP, 1, 16)
				.setUpgradeSlots(1)
				.setMaxLevel(5)
				.setTools(TagProvider.POWERED)
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.overclockModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.wideChiselModifier.get())
				.addInput(TinkerMaterials.pigIron.getIngotTag())
				.addInput(Items.PUFFERFISH)
				.addInput(TinkerMaterials.pigIron.getIngotTag())
				.addInput(Items.PISTON)
				.addInput(Items.PISTON)
				.setMaxLevel(1)
				.setTools(Ingredient.fromItems(TEnergistics.jackhammer.get()))
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.wideChiselModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.placeToolModifier.get())
				.addInput(Items.ARMOR_STAND)
				.setMaxLevel(1)
				.build(consumer, prefixR(TEnergistics.placeToolModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.blockingModifier.get())
				.addInput(Items.BEDROCK)
				.setMaxLevel(1)
				.build(consumer, prefixR(TEnergistics.blockingModifier, upgradeFolder));
		}
		
	private void addForceFieldRecipes(Consumer<IFinishedRecipe> consumer)
		{
		String upgradeFolder = "tools/modifiers/upgrade/force/";
		String powerGroup = "tenergistics:force_modifiers";
		// TODO: replace with durability tag when it arrives.
		ModifierRecipeBuilder.modifier(TEnergistics.forceFieldModifier.get())
				.addInput(Items.CHORUS_FLOWER)
				.addInput(Items.NETHER_STAR)
				.addInput(Items.CHORUS_FLOWER)
				.addInput(TinkerMaterials.manyullyn.getBlockItemTag())
				.addInput(TinkerMaterials.hepatizon.getBlockItemTag())
				.setMaxLevel(1)
				.setAbilitySlots(1)
				.setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.MODIFIABLE), Ingredient
						.fromTag(TagProvider.POWERED)))
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.forceFieldModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.forceFireboxModifier.get())
				.addInput(TEnergistics.firebox)
				.setMaxLevel(1)
				.setRequirements(ModifierMatch.entry(TEnergistics.forceFieldModifier.get()))
				.setRequirementsError("recipe.tenergistics.modifier.not_a_machine")
				.setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.MODIFIABLE), Ingredient
						.fromTag(TagProvider.POWERED)))
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.forceFireboxModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.forceExchangerModifier.get())
				.addInput(TEnergistics.exchanger)
				.setMaxLevel(1)
				.setRequirements(ModifierMatch.entry(TEnergistics.forceFieldModifier.get()))
				.setRequirementsError("recipe.tenergistics.modifier.not_a_machine")
				.setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.MODIFIABLE), Ingredient
						.fromTag(TagProvider.POWERED)))
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.forceExchangerModifier, upgradeFolder));
		
		ModifierRecipeBuilder.modifier(TEnergistics.forceEnergyCoilModifier.get())
				.addInput(TEnergistics.energyCoil)
				.setMaxLevel(1)
				.setRequirements(ModifierMatch.entry(TEnergistics.forceFieldModifier.get()))
				.setRequirementsError("recipe.tenergistics.modifier.not_a_machine")
				.setTools(new IngredientWithout(Ingredient.fromTag(TinkerTags.Items.MODIFIABLE), Ingredient
						.fromTag(TagProvider.POWERED)))
				.setGroup(powerGroup)
				.build(consumer, prefixR(TEnergistics.forceEnergyCoilModifier, upgradeFolder));
		
		}
		
	private void addSmelteryRecipes(Consumer<IFinishedRecipe> consumer)
		{
		String folder = "smeltery/melting/";
		/*
		String folder = "smeltery/casting/";
		ContainerFillingRecipeBuilder.tableRecipe(TEnergistics.jackhammer, FluidAttributes.BUCKET_VOLUME).build(consumer, location(folder + "filling/jackhammer"));*/
		MeltingFuelBuilder.fuel(new FluidStack(TEnergistics.moltenEnergy.get(), 400), 10)
				.build(consumer, location(folder + "fuel/energy"));
		}
		
	private void addMiscRecipes(Consumer<IFinishedRecipe> consumer)
		{
		String folder = "misc/";
		ShapelessRecipeBuilder.shapelessRecipe(TEnergistics.miraculousMachinery)
				.addIngredient(Items.BOOK)
				.addIngredient(Items.COAL_BLOCK)
				.addIngredient(Items.LAVA_BUCKET)
				.addIngredient(Items.REDSTONE_BLOCK)
				.addCriterion("has_center", hasItem(TinkerCommons.mightySmelting))
				.build(consumer, prefix(TEnergistics.miraculousMachinery, folder));
		
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.searedCoilBlock)
				.key('C', TinkerSmeltery.searedBrick)
				.key('i', TinkerMaterials.copper.getIngotTag())
				.patternLine("C C")
				.patternLine("iii")
				.patternLine("C C")
				.addCriterion("has_center", hasItem(TinkerSmeltery.searedBrick))
				.build(consumer, prefix(TEnergistics.searedCoilBlock, folder));;
		
		ShapedRecipeBuilder.shapedRecipe(TEnergistics.scorchedCoilBlock)
				.key('C', TinkerSmeltery.scorchedBrick)
				.key('i', TinkerMaterials.copper.getIngotTag())
				.patternLine("C C")
				.patternLine("iii")
				.patternLine("C C")
				.addCriterion("has_center", hasItem(TinkerSmeltery.scorchedBrick))
				.build(consumer, prefix(TEnergistics.scorchedCoilBlock, folder));
		
		CustomRecipeBuilder.customRecipe(TEnergistics.tinkerStationFireboxRefuelSerializer.get())
				.build(consumer, location(folder + "tinker_station_firebox_refuel").toString());
		}
		
	private void addPartRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends IMaterialItem> sup, int cost, CastItemObject cast)
		{
		String folder = "tools/parts/";
		// Base data
		IMaterialItem part = sup.get();
		String name = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();
		
		// Part Builder
		PartRecipeBuilder.partRecipe(part)
				.setPattern(location(name))
				.setCost(cost)
				.build(consumer, location(folder + "builder/" + name));
		
		// Material Casting
		String castingFolder = folder + "casting/";
		MaterialCastingRecipeBuilder.tableRecipe(part)
				.setItemCost(cost)
				.setCast(cast, false)
				.build(consumer, location(castingFolder + name + "_gold_cast"));
		MaterialCastingRecipeBuilder.tableRecipe(part)
				.setItemCost(cost)
				.setCast(cast.getSingleUseTag(), true)
				.build(consumer, location(castingFolder + name + "_sand_cast"));
		
		// Cast Casting
		MaterialIngredient ingredient = MaterialIngredient.fromItem(part);
		String partName = Objects.requireNonNull(part.asItem().getRegistryName()).getPath();
		ItemCastingRecipeBuilder.tableRecipe(cast)
				.setFluidAndTime(new FluidStack(TinkerFluids.moltenGold.get(), MaterialValues.INGOT))
				.setCast(ingredient, true)
				.setSwitchSlots()
				.build(consumer, location("smeltery/casting/casts/" + partName));
		
		// sand cast molding
		MoldingRecipeBuilder.moldingTable(cast.getSand())
				.setMaterial(TinkerSmeltery.blankCast.getSand())
				.setPattern(ingredient, false)
				.build(consumer, location("smeltery/casting/sand_casts/" + partName));
		MoldingRecipeBuilder.moldingTable(cast.getRedSand())
				.setMaterial(TinkerSmeltery.blankCast.getRedSand())
				.setPattern(ingredient, false)
				.build(consumer, location("smeltery/casting/red_sand_casts/" + partName));
		
		// Part melting
		MaterialMeltingRecipeBuilder.melting(part, cost).build(consumer, location(folder + "melting/" + part));
		}
		
	private void registerMaterial(Consumer<IFinishedRecipe> consumer, MaterialId material, Ingredient input, int value, int needed, String saveName)
		{
		MaterialRecipeBuilder.materialRecipe(material)
				.setIngredient(input)
				.setValue(value)
				.setNeeded(needed)
				.build(consumer, location("tools/materials/" + saveName));
		}
		
	private void addBuildingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends ToolCore> sup)
		{
		ToolCore toolCore = sup.get();
		String name = Objects.requireNonNull(toolCore.getRegistryName()).getPath();
		
		ToolBuildingRecipeBuilder.toolBuildingRecipe(toolCore).build(consumer, location("tools/building/" + name));
		}
		
	private void addMachineBuildingRecipe(Consumer<IFinishedRecipe> consumer, Supplier<? extends ToolCore> sup)
		{
		addBuildingRecipe(consumer, sup);

		ToolCore toolCore = sup.get();
		ModifiedToolRecipeSerializer.Builder.modifiedToolBuildingRecipe(toolCore)
				.setInputs(Ingredient.fromItems(TEnergistics.firebox))
				.setModifier(new ModifierEntry(TEnergistics.fireboxModifier.get(), 1))
				.build(consumer, prefix(toolCore, "tools/building/modified/firebox/"));
		ModifiedToolRecipeSerializer.Builder.modifiedToolBuildingRecipe(toolCore)
				.setInputs(Ingredient.fromItems(TEnergistics.exchanger))
				.setModifier(new ModifierEntry(TEnergistics.exchangerModifier.get(), 1))
				.build(consumer, prefix(toolCore, "tools/building/modified/exchanger/"));
		ModifiedToolRecipeSerializer.Builder.modifiedToolBuildingRecipe(toolCore)
				.setInputs(Ingredient.fromItems(TEnergistics.energyCoil))
				.setModifier(new ModifierEntry(TEnergistics.energyCoilModifier.get(), 1))
				.build(consumer, prefix(toolCore, "tools/building/modified/energy_coil/"));
		}
		
	protected static ResourceLocation location(String id)
		{
		return new ResourceLocation(TEnergistics.modID, id);
		}
		
	protected static ResourceLocation prefix(IItemProvider item, String prefix)
		{
		ResourceLocation loc = Objects.requireNonNull(item.asItem().getRegistryName());
		return location(prefix + loc.getPath());
		}
		
	protected static ResourceLocation prefixR(Supplier<? extends IForgeRegistryEntry<?>> entry, String prefix)
		{
		ResourceLocation loc = Objects.requireNonNull(entry.get().getRegistryName());
		return location(prefix + loc.getPath());
		}
	}

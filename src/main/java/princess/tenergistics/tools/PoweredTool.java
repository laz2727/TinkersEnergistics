package princess.tenergistics.tools;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import princess.tenergistics.TEnergistics;
import princess.tenergistics.capabilities.ToolFuelCapability;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.TooltipType;

/**More of a helper class than anything, really.*/
public class PoweredTool extends ToolCore
	{
	public static final String UNPOWERED = TEnergistics.modID + ".tool.unpowered";
	
	public PoweredTool(Properties properties, ToolDefinition toolDefinition)
		{
		super(properties, toolDefinition);
		}
		
	@EventBusSubscriber
	public static class PowerCapabilityHandler
		{
		public static final ResourceLocation POWER_CAPABILITY = new ResourceLocation(TEnergistics.modID, "power_capability");
		
		@SubscribeEvent
		public static void attachCapabilities(final AttachCapabilitiesEvent<ItemStack> event)
			{
			ItemStack stack = event.getObject();
			if (stack.getItem() instanceof ToolCore)
				{
				if (!stack.getCapability(CapabilityEnergy.ENERGY).isPresent())
					{
					event.addCapability(POWER_CAPABILITY, new ToolFuelCapability(stack));
					}
				}
			}
		}
		
	@Override
	public void getTooltip(ItemStack stack, List<ITextComponent> tooltips, TooltipType tooltipType, boolean flagIn)
		{
		super.getTooltip(stack, tooltips, tooltipType, flagIn);
		switch (tooltipType)
			{
			case NORMAL:
				{
				if (!ToolStack.from(stack).getVolatileData().getBoolean(POWERED))
					{
					tooltips.add(new TranslationTextComponent(UNPOWERED)
							.mergeStyle(TextFormatting.BOLD, TextFormatting.DARK_RED, TextFormatting.ITALIC));
					}
				}
			default:
				break;
			}
		}
		
	public static final ResourceLocation	POWERED			= new ResourceLocation(TEnergistics.modID, "powered");
	
	// Tiem Handling
	
	private static final String				TAG_ITEM		= "item";
	public static final ResourceLocation	ITEM_LOCATION	= new ResourceLocation(TEnergistics.modID, TAG_ITEM);
	
	public static final ResourceLocation	TICKER			= new ResourceLocation(TEnergistics.modID, TAG_ITEM + ".ticker");
	public static final ResourceLocation	TICKER_MAX		= new ResourceLocation(TEnergistics.modID, TAG_ITEM + ".ticker.max");
	public static final ResourceLocation	TICKER_LEFTOVER	= new ResourceLocation(TEnergistics.modID, TAG_ITEM + ".ticker.leftover");
	
	public static int getItemCapacity(ItemStack stack)
		{
		return getItemCapacity(ToolStack.from(stack));
		}
		
	public static int getItemCapacity(IModifierToolStack tool)
		{
		return tool.getVolatileData().getInt(PoweredTool.ITEM_LOCATION);
		}
		
	public static ItemStack getItemStack(ItemStack stack)
		{
		return getItemStack(ToolStack.from(stack));
		}
		
	public static ItemStack getItemStack(IModifierToolStack tool)
		{
		CompoundNBT tagCompound = tool.getPersistentData().getCompound(ITEM_LOCATION);
		if (tagCompound == null)
			{ return ItemStack.EMPTY; }
		return ItemStack.read(tagCompound);
		}
		
	public static void setItemStack(ItemStack stack, ItemStack fuel)
		{
		setItemStack(ToolStack.from(stack), fuel);
		}
		
	public static void setItemStack(IModifierToolStack stack, ItemStack fuel)
		{
		CompoundNBT fuelTag = new CompoundNBT();
		fuel.write(fuelTag);
		ModDataNBT data = stack.getPersistentData();
		data.put(ITEM_LOCATION, fuelTag);
		}
		
	// Fluid Handling
	
	private static final String				TAG_FLUID		= "fluid";
	public static final ResourceLocation	FLUID_LOCATION	= new ResourceLocation(TEnergistics.modID, TAG_FLUID);
	
	public static int getFluidCapacity(ItemStack stack)
		{
		return getFluidCapacity(ToolStack.from(stack));
		}
		
	public static int getFluidCapacity(IModifierToolStack tool)
		{
		return tool.getVolatileData().getInt(PoweredTool.FLUID_LOCATION);
		}
		
	public static FluidStack getFluidStack(ItemStack stack)
		{
		return getFluidStack(ToolStack.from(stack));
		}
		
	public static FluidStack getFluidStack(IModifierToolStack tool)
		{
		CompoundNBT tagCompound = tool.getPersistentData().getCompound(FLUID_LOCATION);
		if (tagCompound == null)
			{ return FluidStack.EMPTY; }
		return FluidStack.loadFluidStackFromNBT(tagCompound);
		}
		
	//FIXME PLS
	public static void setFluidStack(ItemStack stack, FluidStack fluid)
		{
		setFluidStack(ToolStack.from(stack), fluid);
		}
		
	public static void setFluidStack(IModifierToolStack stack, FluidStack fluid)
		{
		CompoundNBT fluidTag = new CompoundNBT();
		fluid.writeToNBT(fluidTag);
		ModDataNBT data = stack.getPersistentData();
		data.put(FLUID_LOCATION, fluidTag);
		}
		
	// Energy Handling
	
	private static final String				TAG_ENERGY		= "energy";
	public static final ResourceLocation	ENERGY_LOCATION	= new ResourceLocation(TEnergistics.modID, TAG_ENERGY);
	
	public static void setEnergy(ItemStack stack, int energy)
		{
		setEnergy(ToolStack.from(stack), energy);
		}
		
	public static void setEnergy(IModifierToolStack tool, int energy)
		{
		tool.getPersistentData().putInt(PoweredTool.ENERGY_LOCATION, energy);
		}
		
	public static int getEnergy(ItemStack stack)
		{
		return getEnergy(ToolStack.from(stack));
		}
		
	public static int getEnergy(IModifierToolStack tool)
		{
		return tool.getPersistentData().getInt(PoweredTool.ENERGY_LOCATION);
		}
		
	public static int getMaxEnergy(ItemStack stack)
		{
		return getMaxEnergy(ToolStack.from(stack));
		}
		
	public static int getMaxEnergy(IModifierToolStack tool)
		{
		return tool.getVolatileData().getInt(PoweredTool.ENERGY_LOCATION);
		}
	}
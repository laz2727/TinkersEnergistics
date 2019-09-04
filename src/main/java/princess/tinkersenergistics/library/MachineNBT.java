package princess.tinkersenergistics.library;

import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.tools.ToolCore;

public class MachineNBT
	{
	public int				cookTime;
	public float			speedMultiplier;
	public float			fuelMultiplier;
	
	public int				type;
	
	public int				inputSlots;
	public int				outputSlots;
	
	public int				tank;
	public int				energyStorage;
	
	public boolean			powered;
	
	public int				modifiers;
	
	private NBTTagCompound	parent;
	
	public MachineNBT()
		{
		cookTime = 1000;
		speedMultiplier = 1F;
		fuelMultiplier = 5F;
		
		type = 0;
		
		inputSlots = 1;
		outputSlots = 1;
		
		tank = 0;
		energyStorage = 0;
		
		powered = false;
		
		modifiers = ToolCore.DEFAULT_MODIFIERS;
		
		parent = new NBTTagCompound();
		}
		
	public MachineNBT(NBTTagCompound tag)
		{
		read(tag);
		parent = tag;
		}
		
	/** Initialize the type. Call this 0th! */
	public MachineNBT type(int type)
		{
		this.type = type;
		return this;
		}
	
	/** Initialize the stats with the heads. CALL THIS FIRST */
	public MachineNBT head(HeadMaterialStats... heads)
		{
		cookTime = 1000;
		
		for (HeadMaterialStats head : heads)
			{
			if (head != null)
				{
				cookTime = StatHelper.cookTime(cookTime, type, head);
				}
			}
			
		return this;
		}
		
	/** Calculate in extras. call this second! */
	public MachineNBT extra(ExtraMaterialStats... extras)
		{
		speedMultiplier = 1;
		fuelMultiplier = 5;
		for (ExtraMaterialStats extra : extras)
			{
			if (extra != null)
				{
				speedMultiplier = StatHelper.speedMultiplier(speedMultiplier, type, extra);
				fuelMultiplier = StatHelper.fuelMultiplier(fuelMultiplier, type, extra);
				}
			}
		return this;
		}
		
	/** Calculate in handles. call this last! */
	public MachineNBT handle(HandleMaterialStats... handles)
		{
		for (HandleMaterialStats handle : handles)
			{
			if (handle != null)
				{
				speedMultiplier = StatHelper.speedMultiplier(speedMultiplier, type, handle);
				fuelMultiplier = StatHelper.fuelMultiplier(fuelMultiplier, type, handle);
				}
			}
		return this;
		}
		
	public void read(NBTTagCompound tag)
		{
		cookTime = tag.getInteger(MachineTags.COOK_TIME);
		speedMultiplier = tag.getFloat(MachineTags.SPEED_MULTIPLIER);
		fuelMultiplier = tag.getFloat(MachineTags.FUEL_MULTIPLIER);
		
		type = tag.getInteger(MachineTags.TYPE);
		
		inputSlots = tag.getInteger(MachineTags.INPUT_SLOTS);
		outputSlots = tag.getInteger(MachineTags.OUTPUT_SLOTS);
		
		tank = tag.getInteger(MachineTags.TANK);
		energyStorage = tag.getInteger(MachineTags.ENERGY_STORAGE);
		
		powered = tag.getBoolean(MachineTags.POWERED);
		
		modifiers = tag.getInteger(MachineTags.FREE_MODIFIERS);
		}
		
	public void write(NBTTagCompound tag)
		{
		tag.setInteger(MachineTags.COOK_TIME, cookTime);
		tag.setFloat(MachineTags.SPEED_MULTIPLIER, speedMultiplier);
		tag.setFloat(MachineTags.FUEL_MULTIPLIER, fuelMultiplier);
		
		tag.setInteger(MachineTags.TYPE, type);
		
		tag.setInteger(MachineTags.INPUT_SLOTS, inputSlots);
		tag.setInteger(MachineTags.OUTPUT_SLOTS, outputSlots);
		
		tag.setInteger(MachineTags.TANK, tank);
		tag.setInteger(MachineTags.ENERGY_STORAGE, energyStorage);
		
		tag.setBoolean(MachineTags.POWERED, powered);
		
		tag.setInteger(MachineTags.FREE_MODIFIERS, modifiers);
		}
		
	public NBTTagCompound get()
		{
		NBTTagCompound tag = parent.copy();
		write(tag);
		
		return tag;
		}
		
	// "Autogenerated" equals and hashcode
	@Override
	public boolean equals(Object o)
		{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		MachineNBT NBT = (MachineNBT) o;
		
		if (cookTime != NBT.cookTime) return false;
		if (speedMultiplier != NBT.speedMultiplier) return false;
		if (fuelMultiplier != NBT.fuelMultiplier) return false;
		
		if (type != NBT.type) return false;
		
		if (inputSlots != NBT.inputSlots) return false;
		if (outputSlots != NBT.outputSlots) return false;
		
		if (tank != NBT.tank) return false;
		if (energyStorage != NBT.energyStorage) return false;
		
		if (powered != NBT.powered) return false;
		
		return modifiers == NBT.modifiers;
		}
		
	@Override
	public int hashCode()
		{
		int result = cookTime;
		result = (int) (31 * result + 31F * speedMultiplier);
		result = (int) (31 * result + 31F * fuelMultiplier);
		
		result = 31 * result + type;
		
		result = 31 * result + inputSlots;
		result = 31 * result + outputSlots;
		
		result = 31 * result + tank;
		result = 31 * result + energyStorage;
		
		result = 31 * result + (powered ? 1 : 0);
		
		result = 31 * result + modifiers;
		
		return result;
		}
	}
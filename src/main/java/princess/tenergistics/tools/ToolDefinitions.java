package princess.tenergistics.tools;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import princess.tenergistics.TEnergistics;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolBaseStatDefinition;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.tools.TinkerToolParts;

public class ToolDefinitions
	{
	
	public static final float					ATTACK_MULTIPLIER		= 2f;
	public static final float					SPEED_MULTIPLIER		= 4f;
	public static final float					BROAD_SPEED_MULTIPLIER	= 0.3f;
	public static final float					DURABILITY_MULTIPLIER	= 0.5f;
	
	public static final ToolBaseStatDefinition	JACKHAMMER_STATS		= new ToolBaseStatDefinition.Builder()
			.setDurabilityModifier(DURABILITY_MULTIPLIER)
			.setMiningSpeedModifier(1.5f / SPEED_MULTIPLIER)
			.setDamageBonus(1f)
			.setDamageModifier(1f / ATTACK_MULTIPLIER)
			.setAttackSpeed(2f / ATTACK_MULTIPLIER)
			.build();
	
	public static final ToolBaseStatDefinition	BUZZSAW_STATS			= new ToolBaseStatDefinition.Builder()
			.setDurabilityModifier(DURABILITY_MULTIPLIER)
			.setMiningSpeedModifier(1.5f / SPEED_MULTIPLIER * BROAD_SPEED_MULTIPLIER)
			.setDamageBonus(3f)
			.setDamageModifier(3f / ATTACK_MULTIPLIER)
			.setAttackSpeed(0.5f / ATTACK_MULTIPLIER)
			.build();
	
	public static final ToolDefinition			JACKHAMMER				= new ToolDefinition(JACKHAMMER_STATS, requirements(TEnergistics.jackhammerRod, TinkerToolParts.toolHandle, TEnergistics.toolCasing, TEnergistics.gearbox));
	public static final ToolDefinition			BUZZSAW					= new ToolDefinition(BUZZSAW_STATS, requirements(TEnergistics.buzzsawDisc, TinkerToolParts.toolHandle, TEnergistics.toolCasing, TEnergistics.gearbox));
	
	private static Supplier<List<IToolPart>> requirements(Stream<Supplier<? extends IToolPart>> parts)
		{
		return () -> parts.map(Supplier::get).collect(Collectors.toList());
		}
		
	private static Supplier<List<IToolPart>> requirements(Supplier<? extends IToolPart> part1, Supplier<? extends IToolPart> part2, Supplier<? extends IToolPart> part3, Supplier<? extends IToolPart> part4)
		{
		return requirements(Stream.of(part1, part2, part3, part4));
		}
	}
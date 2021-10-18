package com.obama69.worldz.command;

import static com.obama69.worldz.command.Util.dimension;
import static com.obama69.worldz.command.Util.success;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.obama69.worldz.Worldz;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class WorldsCommand {
	public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {		
		dispatcher.register(
				Commands.literal("worlds")
				.requires(s -> s.hasPermission(2))
				.then(
						Commands.literal("spawnpoint")
						.then(
								Commands.argument("dimension", DimensionArgument.dimension())
								.then(
										Commands.argument("spawnpoint", Vec3Argument.vec3())
										.executes(WorldsCommand::setSpawnpoint)
								)
						)
				)
				.then(
						Commands.literal("allow")
						.then(
								Commands.argument("dimension", DimensionArgument.dimension())
								.executes(WorldsCommand::allowDimension)
						)
				)
				.then(
						Commands.literal("disallow")
						.then(
								Commands.argument("dimension", DimensionArgument.dimension())
								.executes(WorldsCommand::disallowDimension)
						)
				)
		);
	}
	
	private static final int allowDimension(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final CommandSourceStack source = context.getSource();
		final ServerLevel dimension = DimensionArgument.getDimension(context, "dimension");
		
		Worldz.session.allowWorld(dimension);
		
		source.sendSuccess(success("Allowed dimension ").append(dimension(dimension)), false);
		
		return 1;
	}
	
	private static final int disallowDimension(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final CommandSourceStack source = context.getSource();
		final ServerLevel dimension = DimensionArgument.getDimension(context, "dimension");
		
		Worldz.session.disallowWorld(dimension, false);
		
		source.sendSuccess(success("Disallowed dimension ").append(dimension(dimension)), false);
		
		return 1;
	}
	
	private static final int setSpawnpoint(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final CommandSourceStack source = context.getSource();
		final ServerLevel dimension = DimensionArgument.getDimension(context, "dimension");
		final Vec3 spawnpoint = Vec3Argument.getVec3(context, "spawnpoint");
		
		Worldz.session.setSpawnpoint(dimension, spawnpoint);
		
		final MutableComponent p1 = new TextComponent("Spawnpoint for ").withStyle(ChatFormatting.GREEN);
		final MutableComponent p2 = new TextComponent(dimension.dimension().location().toString()).withStyle(ChatFormatting.YELLOW);
		final MutableComponent p3 = new TextComponent(" set to ").withStyle(ChatFormatting.GREEN);
		final MutableComponent p4 = new TextComponent(format(spawnpoint)).withStyle(ChatFormatting.BLUE);
		
		source.sendSuccess(p1.append(p2).append(p3).append(p4), false);
		
		return 1;
	}
	
	private static String format(final Vec3 vec) {
		return String.format("[%.1f %.1f %.1f]", vec.x, vec.y, vec.z);
	}
}

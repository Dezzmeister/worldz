package com.obama69.worldz.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.obama69.worldz.Worldz;
import com.obama69.worldz.exception.MissingSpawnPosException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class WorldTeleportCommand {
	

	public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {		
		final LiteralCommandNode<CommandSourceStack> node = dispatcher.register(
				Commands.literal("worldteleport")
				.requires(s -> s.hasPermission(0))
				.then(
						Commands.argument("dimension", DimensionArgument.dimension())
						.executes(WorldTeleportCommand::teleport)
				)
		);
		
		dispatcher.register(Commands.literal("wtp").requires(s -> s.hasPermission(0)).redirect(node));
	}
	
	private static int teleport(final CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		final ServerLevel dimension = DimensionArgument.getDimension(context, "dimension");
		final ServerPlayer player = context.getSource().getPlayerOrException();
		
		if (!Worldz.session.canTeleport(dimension)) {
			context.getSource().sendFailure(Util.error("Cannot teleport, ").append(Util.dimension(dimension)).append(Util.error(" is closed")));
			return 1;
		}
		
		if (Worldz.session.inSameWorld(player, dimension)) {
			context.getSource().sendFailure(Util.error("Already in ").append(Util.dimension(dimension)));
			return 1;
		}
		
		try {
			Worldz.session.teleport(player, dimension);
		} catch (MissingSpawnPosException e) {
			e.printStackTrace();
			context.getSource().sendFailure(Util.error("Spawn pos not set for ").append(Util.dimension(dimension)).append(Util.error(", notify an admin or do /worlds spawnpoint")));
		}
		
		return 1;
	}
}

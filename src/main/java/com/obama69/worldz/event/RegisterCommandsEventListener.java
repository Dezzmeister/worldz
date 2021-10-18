package com.obama69.worldz.event;

import com.mojang.brigadier.CommandDispatcher;
import com.obama69.worldz.command.WorldTeleportCommand;
import com.obama69.worldz.command.WorldsCommand;

import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RegisterCommandsEventListener {
	@SubscribeEvent
	public void receive(final RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		
		WorldsCommand.register(dispatcher);
		WorldTeleportCommand.register(dispatcher);
	}
}

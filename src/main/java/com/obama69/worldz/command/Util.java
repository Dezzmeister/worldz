package com.obama69.worldz.command;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;

class Util {
	static final DynamicCommandExceptionType UNKNOWN_ERROR = new DynamicCommandExceptionType(arg -> {
		return new TextComponent("Something went wrong");
	});
	
	static MutableComponent success(final String str) {
		return new TextComponent(str).withStyle(ChatFormatting.GREEN);
	}
	
	static MutableComponent dimension(final ServerLevel dimension) {
		return new TextComponent(dimension.dimension().location().toString()).withStyle(ChatFormatting.YELLOW);
	}
	
	static MutableComponent error(final String str) {
		return new TextComponent(str).withStyle(ChatFormatting.RED);
	}
}

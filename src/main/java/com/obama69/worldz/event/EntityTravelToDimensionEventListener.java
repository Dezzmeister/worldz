package com.obama69.worldz.event;

import com.obama69.worldz.Worldz;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EntityTravelToDimensionEventListener {

	@SubscribeEvent
	public void onEntityTravelToDimension(final EntityTravelToDimensionEvent event) {
		final Entity entity = event.getEntity();
		
		if (!(entity instanceof ServerPlayer)) {
			return;
		}
		
		final ServerPlayer player = (ServerPlayer) entity;
		final ServerLevel currentDimension = player.getLevel();
		Worldz.session.setLastPosition(player, currentDimension);
	}
}

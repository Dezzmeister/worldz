package com.obama69.worldz.session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.obama69.worldz.Worldz;
import com.obama69.worldz.exception.MissingSpawnPosException;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MultiWorldSession {
	private static final String WORLDZ_FILE = "worldz.json";
	
	/**
	 * Maps dimensions to their default spawnpoints. For now, default spawnpoints need to be set manually.
	 * TODO: Default spawnpoints should be determined automatically by the initial spawnpoint algorithm
	 */
	private final HashMap<String, Vec3> spawnpoints;
	
	/**
	 * Maps players to WorldPositions, which are maps from worlds to positions. Each player has a position in each
	 * world that they have visited.
	 */
	private final HashMap<String, WorldPositions<String>> userPositions;
	
	/**
	 * Players can still access worlds not in this list, but they cannot use /wtp to teleport
	 * to them. For example, the nether may not be accessible through /wtp, but you could build a nether
	 * portal to reach it instead.
	 */
	private final ArrayList<String> allowedWorlds;
	
	private MultiWorldSession(final HashMap<String, Vec3> _spawnpoints, final HashMap<String, WorldPositions<String>> _userPositions, final ArrayList<String> _allowedWorlds) {
		spawnpoints = _spawnpoints;
		userPositions = _userPositions;
		allowedWorlds = _allowedWorlds;
	}
	
	private MultiWorldSession() {
		this(new HashMap<>(), new HashMap<>(), defaultAllowedWorlds());
	}
	
	private static final ArrayList<String> defaultAllowedWorlds() {
		final ArrayList<String> out = new ArrayList<>();
		
		out.add(Level.OVERWORLD.location().toString());
		
		return out;
	}
	
	public boolean inSameWorld(final ServerPlayer player, final ServerLevel dimension) {
		final String target = getWorldKey(dimension);
		final String current = getWorldKey(player.getLevel());
		
		return target.equals(current);
	}
	
	public void setLastPosition(final ServerPlayer player, final ServerLevel dimension) {
		final String worldKey = getWorldKey(dimension);
		final String playerKey = getPlayerKey(player);
		final Vec3 pos = player.position();
		
		final WorldPositions<String> positions;
		
		if (userPositions.containsKey(playerKey)) {
			positions = userPositions.get(playerKey);
		} else {
			positions = new WorldPositions<>();
		}
		
		positions.positions.put(worldKey, pos);
		userPositions.put(playerKey, positions);
	}
	
	public boolean canTeleport(final ServerLevel targetDimension) {
		return allowedWorlds.contains(getWorldKey(targetDimension));
	}
	
	public boolean canTeleport(final ResourceLocation loc) {
		return allowedWorlds.contains(getWorldKey(loc));
	}
	
	public void teleport(final ServerPlayer player, final ServerLevel targetDimension) throws MissingSpawnPosException {
		final String worldKey = getWorldKey(targetDimension);
		final String playerKey = getPlayerKey(player);
		
		if (!canTeleport(targetDimension)) {
			return;
		}
		
		final Vec3 defaultSpawnPos = spawnpoints.get(worldKey);
		final WorldPositions<String> positions = userPositions.get(playerKey);
		
		final Vec3 spawnPos;
		
		if (positions == null) {			
			spawnPos = defaultSpawnPos;
		} else {
			final Vec3 prevPosition = positions.positions.get(worldKey);
			if (prevPosition == null) {
				spawnPos = defaultSpawnPos;
			} else {
				spawnPos = prevPosition;
			}
		}
		
		if (defaultSpawnPos == null && spawnPos == null) {
			throw new MissingSpawnPosException("Spawn pos is not set for " + worldKey);
		}
		
		// TODO: Test this and flesh it out		
		player.teleportTo(targetDimension, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);
		Worldz.LOGGER.info("SPAWNPOS: " + spawnPos.toString());
	}
	
	public void setSpawnpoint(final ServerLevel dimension, final Vec3 spawnpoint) {
		final String key = getWorldKey(dimension);
		
		spawnpoints.put(key, spawnpoint);
	}
	
	public void allowWorld(final ServerLevel dimension) {
		final String key = getWorldKey(dimension);
		
		allowedWorlds.add(key);
	}
	
	public void disallowWorld(final ServerLevel dimension, final boolean clearPlayers) {
		final String key = getWorldKey(dimension);
		
		allowedWorlds.remove(key);
		
		// TODO: Remove players from disallowed world if clearPlayers is true
	}
	
	private static String getWorldKey(final ServerLevel level) {
		return level.dimension().location().toString();
	}
	
	private static String getWorldKey(final ResourceLocation location) {
		return location.toString();
	}
	
	private static String getPlayerKey(final ServerPlayer player) {
		return player.getGameProfile().getName();
	}
	
	public static MultiWorldSession loadSession() throws FileNotFoundException {
		final File file = new File(WORLDZ_FILE);
		
		if (!file.exists()) {
			return new MultiWorldSession();
		}
		
		final Gson gson = new Gson();
		final JsonReader reader = new JsonReader(new FileReader(file));
		
		return gson.fromJson(reader, MultiWorldSession.class);
	}
	
	private void persist() throws IOException {
		final Gson gson = new Gson();
		final String json = gson.toJson(this);
		
		Files.writeString(Paths.get(WORLDZ_FILE), json);
	}
	
	@SubscribeEvent
	public void onWorldSave(final WorldEvent.Save event) {
		try {
			persist();
		} catch (IOException e) {
			e.printStackTrace();
			Worldz.LOGGER.error("Critical error: Failed to save Worldz data");
		}
	}
}

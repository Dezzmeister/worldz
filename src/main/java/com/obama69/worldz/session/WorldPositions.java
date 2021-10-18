package com.obama69.worldz.session;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.phys.Vec3;

public class WorldPositions<W> {
	public final Map<W, Vec3> positions;
	
	public WorldPositions() {
		positions = new HashMap<W, Vec3>();
	}
	
	public WorldPositions(final Map<W, Vec3> _positions) {
		positions = _positions;
	}
}

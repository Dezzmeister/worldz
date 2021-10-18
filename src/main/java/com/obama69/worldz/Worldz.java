package com.obama69.worldz;

import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.obama69.worldz.event.EntityTravelToDimensionEventListener;
import com.obama69.worldz.event.RegisterCommandsEventListener;
import com.obama69.worldz.session.MultiWorldSession;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("worldz")
public class Worldz {
	public static final String MODID = "worldz";
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static MultiWorldSession session;

    public Worldz() {        
        try {
			session = MultiWorldSession.loadSession();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			LOGGER.error("Critical error, Worldz mod cannot start");
			return;
		}
        
        MinecraftForge.EVENT_BUS.register(session);
        MinecraftForge.EVENT_BUS.register(new RegisterCommandsEventListener());
        MinecraftForge.EVENT_BUS.register(new EntityTravelToDimensionEventListener());
    }
}

package me.bannerbound.com.pms.civpm;

import me.bannerbound.com.api.managers.TickManagerListener;
import me.bannerbound.com.api.managers.TickManagerListenerID;
import me.bannerbound.com.api.managers.TickManagerListenerSide;
import me.bannerbound.com.pms.civpm.managers.CPMRegionsManager;
import me.bannerbound.com.pms.civpm.managers.CPMWandererManager;
import me.bannerbound.com.pms.civpm.managers.CPMWandererWalkManager;
import me.bannerbound.com.pms.civpm.utils.CPMListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Set;


public class CivPM {
    private static final CivPM instance = new CivPM();
    private static final CPMRegionsManager regionsManager = new CPMRegionsManager();
    private static final CPMWandererManager wandererManager = new CPMWandererManager();
    private static final CPMWandererWalkManager wandererWalkManager = new CPMWandererWalkManager();

    public static CivPM getInstance() {return instance;}
    public static CPMRegionsManager getRegionManager() {return regionsManager;}
    public static CPMWandererWalkManager getWandererWalkManager() {return wandererWalkManager;}
    public static CPMWandererManager getWandererManager() {return wandererManager;}
}
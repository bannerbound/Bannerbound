package me.bannerbound.com.pms.civpm;

import me.bannerbound.com.pms.civpm.managers.CPMRegionsManager;
import me.bannerbound.com.pms.civpm.managers.CPMWandererManager;
import me.bannerbound.com.pms.civpm.managers.CPMWandererWalkManager;
import me.bannerbound.com.pms.civpm.utils.CPMListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
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

    public final Set<CPMListener> listeners = new java.util.HashSet<>();

    public void addListener(CPMListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CPMListener listener) {
        listeners.remove(listener);
    }

    public void tick(ServerTickEvent.Post event) {
        ServerLevel overworld = event.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        for (CPMListener listener : listeners) {
            listener.tick(event, overworld);
        }
    }
}
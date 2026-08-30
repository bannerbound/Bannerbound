package me.bannerbound.com.civpm;

import me.bannerbound.com.civpm.managers.CPMRegionsManager;
import me.bannerbound.com.civpm.managers.CPMWandererWalkManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.ServerTickEvent;


public class CivPM {
    private static final CivPM instance = new CivPM();
    private static final CPMRegionsManager regions_manager = new CPMRegionsManager();
    private static final CPMWandererWalkManager wanderer_walk_manager = new CPMWandererWalkManager();

    public static CivPM getInstance() {return instance;}
    public static CPMRegionsManager getRegionManager() {return regions_manager;}
    public static CPMWandererWalkManager getWandererWalkManager() {return wanderer_walk_manager;}

    public void tick(ServerTickEvent.Post event) {
        ServerLevel overworld = event.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        wanderer_walk_manager.tick(event, overworld);
    }
}
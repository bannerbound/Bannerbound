package com.bannerbound.core.civpm;

import com.bannerbound.core.civpm.data.CPMRegion;
import com.bannerbound.core.civpm.managers.CPMRegionsManager;
import com.bannerbound.core.civpm.managers.CPMWandererWalkManager;
import com.bannerbound.core.civpm.utils.CPMMathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.UUID;


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
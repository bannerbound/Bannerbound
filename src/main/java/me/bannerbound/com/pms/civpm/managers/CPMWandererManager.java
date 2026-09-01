package me.bannerbound.com.pms.civpm.managers;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import me.bannerbound.com.BannerboundServerConfig;
import me.bannerbound.com.pms.civpm.CivPM;
import me.bannerbound.com.pms.civpm.data.CPMServerRegion;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMClearWanderersPacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMRegionResponsePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;

public class CPMWandererManager {
    public static Boolean cpmWandererEnabled;

    public void configUpdated() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) return;

        if (cpmWandererEnabled == null) {
            cpmWandererEnabled = BannerboundServerConfig.cpmWandererEnabled;
            return;
        }

        if (cpmWandererEnabled != BannerboundServerConfig.cpmWandererEnabled) {
            if (BannerboundServerConfig.cpmWandererEnabled) {
                for (ServerPlayer player : server.overworld().players()) {
                    List<CPMServerRegion> regions = CivPM.getRegionManager().getLoadedRegionsForPlayer(player);

                    for (CPMServerRegion region : regions) {
                        PacketDistributor.sendToPlayer(player, new CPMRegionResponsePacket(region.getPos(), region.getStreamWandererEntries()));
                    }
                }
            } else {
                PacketDistributor.sendToAllPlayers(new CPMClearWanderersPacket());
            }

            cpmWandererEnabled = BannerboundServerConfig.cpmWandererEnabled;
        }
    }

    public void clearRegionsOfWanderers() {
        ObjectIterator<CPMServerRegion> cached_regions_iterator = CivPM.getRegionManager().cached_regions.values().iterator();

        while (cached_regions_iterator.hasNext()) {
            CPMServerRegion region = cached_regions_iterator.next();
        }
    }
}

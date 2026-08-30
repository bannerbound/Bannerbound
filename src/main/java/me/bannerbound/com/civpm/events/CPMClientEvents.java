package me.bannerbound.com.civpm.events;

import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.civpm.CivPMClient;
import me.bannerbound.com.civpm.managers.CPMClientRegionsManager;
import me.bannerbound.com.civpm.utils.CPMMathUtils;
import me.bannerbound.com.civpm.utils.CPMSpawnUtils;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Bannerbound.MODID)
public class CPMClientEvents {
    private static double oldX = 0;
    private static double oldZ = 0;

    @SubscribeEvent
    public static void onClientPlayerMove(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof LocalPlayer localPlayer)) return;
        if (localPlayer.getX() == oldX && localPlayer.getZ() == oldZ) return;

        oldX = localPlayer.getX();
        oldZ = localPlayer.getZ();

        long current_region = CPMMathUtils.CPM2DUtils.packBlockToRegion(localPlayer.getBlockX(), localPlayer.getBlockZ());

        if (CPMClientRegionsManager.current_region == current_region) return;

        CPMClientRegionsManager.current_region = current_region;
        CivPMClient.getRegionsManager().playerChangedRegion();
    }

    @SubscribeEvent
    public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        CPMClientRegionsManager.current_region = Long.MAX_VALUE;
        CivPMClient.getRegionsManager().cached_regions().clear();
        CPMSpawnUtils.playerLeftWorld();
    }
}

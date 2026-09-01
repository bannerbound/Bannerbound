package me.bannerbound.com.pms.civpm.managers.packets;

import me.bannerbound.com.BannerboundClientConfig;
import me.bannerbound.com.pms.civpm.CivPMClient;
import me.bannerbound.com.pms.civpm.data.CPMClientRegion;
import me.bannerbound.com.pms.civpm.entities.CPMFakeCitizenEntity;
import me.bannerbound.com.pms.civpm.managers.CPMRegionsManager;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMClearWanderersPacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMMoveWandererPacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMRegionResponsePacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMSpawnWandererPacket;
import me.bannerbound.com.pms.civpm.utils.CPMMathUtils;
import me.bannerbound.com.pms.PMSpawnUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CPMClientPacketsManager {
    public static void handleRegionResponsePacket(final CPMRegionResponsePacket payload, final IPayloadContext context) {
        if (!(context.player() instanceof LocalPlayer player)) return;

        CPMClientRegion oldRegion = CivPMClient.getRegionsManager().getRegion(payload.pos());

        if (oldRegion != null) {
            oldRegion.cleanup();
        }

        CPMClientRegion region = new CPMClientRegion(payload.pos(), payload.wanderers());

        if (CPMRegionsManager.isRegionLoadedByPlayer(region, player)) {
            CivPMClient.getRegionsManager().cacheRegion(region);
            region.populateRegion((ClientLevel) player.level());
        }
    }

    public static void handleMoveWandererPacket(final CPMMoveWandererPacket payload, final IPayloadContext context) {
        if (!(context.player() instanceof LocalPlayer player)) return;

        CPMClientRegion region = CivPMClient.getRegionsManager().getRegion(payload.region());
        if (region == null) return;
        if (!CPMRegionsManager.isRegionLoadedByPlayer(region, player)) return; // Rogue packet?

        region.addWanderer(payload.uuid(), payload.pos()); // update pos
        CPMFakeCitizenEntity entity = region.fake_entities().get(payload.uuid());

        if (entity == null) return;

        entity.setWalkTarget(payload.pos());
    }

    public static void handleSpawnWandererPacket(final CPMSpawnWandererPacket payload, final IPayloadContext context) {
        if (!(context.player() instanceof LocalPlayer player)) return;

        long region_pos = CPMMathUtils.CPM2DUtils.blockToPacked(payload.pos().getX(), payload.pos().getZ());
        CPMClientRegion region = CivPMClient.getRegionsManager().getRegion(region_pos);

        if (region == null) return;
        if (!CPMRegionsManager.isRegionLoadedByPlayer(region, player)) return;

        region.addWanderer(payload.uuid(), payload.pos());

        if (!BannerboundClientConfig.cpmWandererEnabled) {
            ClientLevel level = (ClientLevel) player.level();
            CPMFakeCitizenEntity entity = PMSpawnUtils.spawnFakeCitizenEntity(level, payload.uuid(), payload.pos());
            region.addFakeEntity(entity);
        }

    }

    public static void handleClearWanderersPacket(final CPMClearWanderersPacket payload, final IPayloadContext context) {
        if (!(context.player() instanceof LocalPlayer player)) return;

        for (CPMClientRegion region : CivPMClient.getRegionsManager().cached_regions().values()) {
            region.cleanup();
        }
    }
}

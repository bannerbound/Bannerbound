package me.bannerbound.com.civpm.managers.packets;

import me.bannerbound.com.civpm.CivPMClient;
import me.bannerbound.com.civpm.data.CPMClientRegion;
import me.bannerbound.com.civpm.entities.CPMFakeCitizenEntity;
import me.bannerbound.com.civpm.managers.CPMRegionsManager;
import me.bannerbound.com.civpm.packets.servertoclient.CPMMoveWandererPacket;
import me.bannerbound.com.civpm.packets.servertoclient.CPMRegionResponsePacket;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CPMClientPacketsManager {
    public static void handleRegionResponsePacket(final CPMRegionResponsePacket payload, final IPayloadContext context) {
        if (!(context.player() instanceof LocalPlayer player)) return;

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

        ClientLevel level = (ClientLevel) player.level();

        region.addWanderer(payload.uuid(), payload.pos()); // update pos
        CPMFakeCitizenEntity entity = region.fake_entities().get(payload.uuid());

        if (entity == null) return;

        entity.setWalkTarget(payload.pos());
    }
}

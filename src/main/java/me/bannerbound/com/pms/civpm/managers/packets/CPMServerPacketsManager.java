package me.bannerbound.com.pms.civpm.managers.packets;

import me.bannerbound.com.BannerboundServerConfig;
import me.bannerbound.com.pms.civpm.CivPM;
import me.bannerbound.com.pms.civpm.data.CPMRegion;
import me.bannerbound.com.pms.civpm.packets.clienttoserver.CPMRegionRequestPacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMRegionResponsePacket;
import me.bannerbound.com.pms.civpm.packets.utils.CPMPacketWandererEntry;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class CPMServerPacketsManager {
    public static void handleRegionRequestPacket(final CPMRegionRequestPacket payload, final IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) return;

        CPMRegion region = CivPM.getRegionManager().getRegion(payload.pos());

        List<CPMPacketWandererEntry> entries;

        if (BannerboundServerConfig.cpmWandererEnabled) {
            entries = region.getStreamWandererEntries();
        } else {
            entries = new ArrayList<>();
        }

        PacketDistributor.sendToPlayer(sender, new CPMRegionResponsePacket(region.getPos(), entries));
    }
}

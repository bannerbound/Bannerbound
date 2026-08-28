package com.bannerbound.core.civpm.managers.packets;

import com.bannerbound.core.civpm.CivPM;
import com.bannerbound.core.civpm.data.CPMRegion;
import com.bannerbound.core.civpm.packets.clienttoserver.CPMRegionRequestPacket;
import com.bannerbound.core.civpm.packets.servertoclient.CPMRegionResponsePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class CPMServerPacketsManager {
    private static final boolean SPAWN_WANDERERS = false;

    public static void handleRegionRequestPacket(final CPMRegionRequestPacket payload, final IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer sender)) return;

        CPMRegion region = CivPM.getRegionManager().getRegion(payload.pos());

        if (SPAWN_WANDERERS) {
            if (region.getWanderers().isEmpty()) {
                for (int i = 0; i < 30; i++) {
                    RandomSource randomSource = sender.level().getRandom();
                    BlockPos pos = new BlockPos(region.getBlockX() + randomSource.nextInt(48), -60, region.getBlockY() + randomSource.nextInt(48));
                    region.addWanderer(UUID.randomUUID(), pos);
                }
            }
        }


        PacketDistributor.sendToPlayer(sender, new CPMRegionResponsePacket(region.getPos(), region.getStreamWandererEntries()));
    }
}

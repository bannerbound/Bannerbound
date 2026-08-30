package me.bannerbound.com.civpm.managers.packets;

import me.bannerbound.com.civpm.CivPM;
import me.bannerbound.com.civpm.data.CPMRegion;
import me.bannerbound.com.civpm.packets.clienttoserver.CPMRegionRequestPacket;
import me.bannerbound.com.civpm.packets.servertoclient.CPMRegionResponsePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public class CPMServerPacketsManager {
    private static final boolean SPAWN_WANDERERS = true;

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

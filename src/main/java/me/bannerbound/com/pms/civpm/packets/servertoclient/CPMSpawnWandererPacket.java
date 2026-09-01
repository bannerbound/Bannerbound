package me.bannerbound.com.pms.civpm.packets.servertoclient;

import io.netty.buffer.ByteBuf;
import me.bannerbound.com.Bannerbound;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static me.bannerbound.com.pms.civpm.packets.utils.CPMUUIDCODEC.UUID_CODEC;

public record CPMSpawnWandererPacket(UUID uuid, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CPMSpawnWandererPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Bannerbound.MODID, "civpm_spawn_wanderer_packet")
    );

    public static final StreamCodec<ByteBuf, CPMSpawnWandererPacket> STREAM_CODEC = StreamCodec.composite(
            UUID_CODEC, CPMSpawnWandererPacket::uuid,
            BlockPos.STREAM_CODEC, CPMSpawnWandererPacket::pos,
            CPMSpawnWandererPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

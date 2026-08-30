package me.bannerbound.com.civpm.packets.servertoclient;

import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.civpm.utils.CPMPacketUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

import static me.bannerbound.com.civpm.packets.utils.CPMUUIDCODEC.UUID_CODEC;

public record CPMMoveWandererPacket(long region, UUID uuid, BlockPos pos) implements CustomPacketPayload {
    public static final Type<CPMMoveWandererPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Bannerbound.MODID, "civpm_move_wanderer_packet")
    );

    public static final StreamCodec<ByteBuf, CPMMoveWandererPacket> STREAM_CODEC = StreamCodec.composite(
        CPMPacketUtils.LONG, CPMMoveWandererPacket::region,
        UUID_CODEC, CPMMoveWandererPacket::uuid,
        BlockPos.STREAM_CODEC, CPMMoveWandererPacket::pos,
        CPMMoveWandererPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package com.bannerbound.core.civpm.packets.servertoclient;

import com.bannerbound.core.BannerboundCore;
import com.bannerbound.core.civpm.utils.CPMPacketUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

import static com.bannerbound.core.civpm.packets.utils.CPMUUIDCODEC.UUID_CODEC;

public record CPMMoveWandererPacket(long region, UUID uuid, BlockPos pos) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CPMMoveWandererPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(BannerboundCore.MODID, "civpm_move_wanderer_packet")
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

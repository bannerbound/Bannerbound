package me.bannerbound.com.civpm.packets.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import java.util.UUID;

import static me.bannerbound.com.civpm.packets.utils.CPMUUIDCODEC.UUID_CODEC;

public record CPMPacketWandererEntry(UUID uuid, BlockPos pos) {
    public static final StreamCodec<ByteBuf, CPMPacketWandererEntry> STREAM_CODEC = StreamCodec.composite(
            UUID_CODEC, CPMPacketWandererEntry::uuid,
            BlockPos.STREAM_CODEC, CPMPacketWandererEntry::pos,
            CPMPacketWandererEntry::new
    );
}
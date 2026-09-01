package me.bannerbound.com.pms.civpm.packets.servertoclient;

import io.netty.buffer.ByteBuf;
import me.bannerbound.com.Bannerbound;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CPMClearWanderersPacket() implements CustomPacketPayload {
    public static final Type<CPMClearWanderersPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Bannerbound.MODID, "civpm_clear_wanderers_packet")
    );

    public static final StreamCodec<ByteBuf, CPMClearWanderersPacket> STREAM_CODEC = StreamCodec.unit(new CPMClearWanderersPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

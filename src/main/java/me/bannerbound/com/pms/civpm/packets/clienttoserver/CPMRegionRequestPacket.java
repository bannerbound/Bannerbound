package me.bannerbound.com.pms.civpm.packets.clienttoserver;

import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.pms.civpm.utils.CPMPacketUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record CPMRegionRequestPacket(long pos) implements CustomPacketPayload {
    public static final Type<CPMRegionRequestPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Bannerbound.MODID, "civpm_region_request_packet")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CPMRegionRequestPacket> STREAM_CODEC = StreamCodec.composite(
        CPMPacketUtils.LONG, CPMRegionRequestPacket::pos,
        CPMRegionRequestPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

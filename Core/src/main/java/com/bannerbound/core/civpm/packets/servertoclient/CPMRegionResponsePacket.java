package com.bannerbound.core.civpm.packets.servertoclient;

import com.bannerbound.core.BannerboundCore;
import com.bannerbound.core.civpm.packets.utils.CPMPacketWandererEntry;
import com.bannerbound.core.civpm.utils.CPMPacketUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record CPMRegionResponsePacket(long pos, List<CPMPacketWandererEntry> wanderers) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<CPMRegionResponsePacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(BannerboundCore.MODID, "civpm_region_response_packet")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, CPMRegionResponsePacket> STREAM_CODEC = StreamCodec.composite(
            CPMPacketUtils.LONG, CPMRegionResponsePacket::pos,
            ByteBufCodecs.collection(ArrayList::new, CPMPacketWandererEntry.STREAM_CODEC), CPMRegionResponsePacket::wanderers,
            CPMRegionResponsePacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

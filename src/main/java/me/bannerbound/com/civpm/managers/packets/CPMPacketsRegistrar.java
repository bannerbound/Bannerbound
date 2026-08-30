package me.bannerbound.com.civpm.managers.packets;

import me.bannerbound.com.civpm.packets.clienttoserver.CPMRegionRequestPacket;
import me.bannerbound.com.civpm.packets.servertoclient.CPMMoveWandererPacket;
import me.bannerbound.com.civpm.packets.servertoclient.CPMRegionResponsePacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class CPMPacketsRegistrar {
    private static void registerClientToServer(PayloadRegistrar registrar) {
        registrar.playToServer(
                CPMRegionRequestPacket.TYPE,
                CPMRegionRequestPacket.STREAM_CODEC,
                CPMServerPacketsManager::handleRegionRequestPacket
        );
    }

    private static void registerServerToClient(PayloadRegistrar registrar) {
        registrar.playToClient(
                CPMRegionResponsePacket.TYPE,
                CPMRegionResponsePacket.STREAM_CODEC,
                CPMClientPacketsManager::handleRegionResponsePacket
        );

        registrar.playToClient(
                CPMMoveWandererPacket.TYPE,
                CPMMoveWandererPacket.STREAM_CODEC,
                CPMClientPacketsManager::handleMoveWandererPacket
        );
    }

    public static void registerPackets(PayloadRegistrar registrar) {
        registerClientToServer(registrar);
        registerServerToClient(registrar);
    }
}

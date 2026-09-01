package me.bannerbound.com.pms.civpm.managers.packets;

import me.bannerbound.com.pms.civpm.packets.clienttoserver.CPMRegionRequestPacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMClearWanderersPacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMMoveWandererPacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMRegionResponsePacket;
import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMSpawnWandererPacket;
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

        registrar.playToClient(
                CPMSpawnWandererPacket.TYPE,
                CPMSpawnWandererPacket.STREAM_CODEC,
                CPMClientPacketsManager::handleSpawnWandererPacket
        );

        registrar.playToClient(
                CPMClearWanderersPacket.TYPE,
                CPMClearWanderersPacket.STREAM_CODEC,
                CPMClientPacketsManager::handleClearWanderersPacket
        );
    }

    public static void registerPackets(PayloadRegistrar registrar) {
        registerClientToServer(registrar);
        registerServerToClient(registrar);
    }
}

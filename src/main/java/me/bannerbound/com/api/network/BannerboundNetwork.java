package me.bannerbound.com.api.network;

import me.bannerbound.com.pms.civpm.managers.packets.CPMPacketsRegistrar;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class BannerboundNetwork {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        CPMPacketsRegistrar.registerPackets(registrar);
    }
}

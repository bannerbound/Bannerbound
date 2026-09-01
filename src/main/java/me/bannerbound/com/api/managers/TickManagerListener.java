package me.bannerbound.com.api.managers;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public interface TickManagerListener {
    void serverTick(ServerTickEvent.Post event);
    void clientTick(ClientTickEvent.Post event);
}

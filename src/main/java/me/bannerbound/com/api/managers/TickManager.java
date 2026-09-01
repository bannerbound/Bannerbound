package me.bannerbound.com.api.managers;

import me.bannerbound.com.Bannerbound;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TickManager {
    private static final Map<String, TickManagerListener> serverListeners = new ConcurrentHashMap<>();
    private static final Map<String, TickManagerListener> clientListeners = new ConcurrentHashMap<>();

    private static TickManagerListenerID getListenerAnnotation(TickManagerListener listener) {
        Class<?> clazz = listener.getClass();
        if (clazz.isAnnotationPresent(TickManagerListenerID.class)) {
            return clazz.getAnnotation(TickManagerListenerID.class);
        }
        throw new IllegalArgumentException(
                "Class " + clazz.getSimpleName() + " must be annotated with @TickManagerListenerID"
        );
    }

    public static void addListener(TickManagerListener listener) {
        TickManagerListenerID annotation = getListenerAnnotation(listener);
        TickManagerListenerSide side = annotation.side();

        if (side == TickManagerListenerSide.BOTH) {
            serverListeners.put(annotation.value(), listener);
            clientListeners.put(annotation.value(), listener);
        } else {
            if (side == TickManagerListenerSide.SERVER) {
                serverListeners.put(annotation.value(), listener);
            }
            if (side == TickManagerListenerSide.CLIENT) {
                clientListeners.put(annotation.value(), listener);
            }
        }


    }

    public static void removeListener(TickManagerListener listener) {
        TickManagerListenerID annotation = getListenerAnnotation(listener);
        serverListeners.remove(annotation.value());
        clientListeners.remove(annotation.value());
    }

    @EventBusSubscriber(modid = Bannerbound.MODID)
    public static class TickManagerServerEvents {
        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Post event) {
            for (TickManagerListener listener : serverListeners.values()) {
                listener.serverTick(event);
            }
        }
    }

    @EventBusSubscriber(modid = Bannerbound.MODID, value = Dist.CLIENT)
    public static class TickManagerClientEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            for (TickManagerListener listener : clientListeners.values()) {
                listener.clientTick(event);
            }
        }
    }
}
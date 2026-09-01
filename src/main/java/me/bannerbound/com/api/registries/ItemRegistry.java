package me.bannerbound.com.api.registries;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public final static DeferredRegister.Items ITEMS = Registry.ITEMS;

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}

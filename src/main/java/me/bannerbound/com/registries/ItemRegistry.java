package me.bannerbound.com.registries;

import me.bannerbound.com.Bannerbound;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public final static DeferredRegister.Items ITEMS = Registry.ITEMS;

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}

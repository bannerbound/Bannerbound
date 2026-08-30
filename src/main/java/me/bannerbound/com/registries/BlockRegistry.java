package me.bannerbound.com.registries;

import me.bannerbound.com.Bannerbound;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public final static DeferredRegister.Blocks BLOCKS = Registry.BLOCKS;

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}

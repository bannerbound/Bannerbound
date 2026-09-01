package me.bannerbound.com.api.registries;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockRegistry {
    public final static DeferredRegister.Blocks BLOCKS = Registry.BLOCKS;
    public final static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = Registry.BLOCK_ENTITY_TYPES;

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
    }
}

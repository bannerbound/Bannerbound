package me.bannerbound.com.registries;

import me.bannerbound.com.civpm.entities.CPMFakeCitizenEntity;
import me.bannerbound.com.civpm.entities.renderers.CPMFakeCitizenRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    public final static DeferredRegister<EntityType<?>> ENTITY_TYPES = Registry.ENTITY_TYPES;

    public static final DeferredHolder<EntityType<?>, EntityType<CPMFakeCitizenEntity>> CPM_FAKE_CITIZEN_ENTITY =
            ENTITY_TYPES.register("cpm_fake_citizen",
                    () -> EntityType.Builder.<CPMFakeCitizenEntity>of(CPMFakeCitizenEntity::new, MobCategory.MISC)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("cpm_fake_citizen")
            );

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);

        bus.addListener(EntityRegistry::registerEntityAttributes);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CPM_FAKE_CITIZEN_ENTITY.get(), CPMFakeCitizenRenderer::new);
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CPM_FAKE_CITIZEN_ENTITY.get(), CPMFakeCitizenEntity.createAttributes().build());
    }
}

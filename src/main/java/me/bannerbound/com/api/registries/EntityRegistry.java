package me.bannerbound.com.api.registries;

import me.bannerbound.com.pms.arrowpm.entities.APMArrowEntity;
import me.bannerbound.com.pms.arrowpm.entities.APMArrowEntityRenderer;
import me.bannerbound.com.pms.civpm.entities.CPMFakeCitizenEntity;
import me.bannerbound.com.pms.civpm.entities.renderers.CPMFakeCitizenRenderer;
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

    public static final DeferredHolder<EntityType<?>, EntityType<APMArrowEntity>> APM_ARROW_ENTITY =
            ENTITY_TYPES.register("apm_arrow",
                    () -> EntityType.Builder.<APMArrowEntity>of(APMArrowEntity::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)
                            .build("apm_arrow"));

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);

        bus.addListener(EntityRegistry::registerEntityAttributes);
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CPM_FAKE_CITIZEN_ENTITY.get(), CPMFakeCitizenRenderer::new);
        event.registerEntityRenderer(APM_ARROW_ENTITY.get(), APMArrowEntityRenderer::new);
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(CPM_FAKE_CITIZEN_ENTITY.get(), CPMFakeCitizenEntity.createAttributes().build());
    }
}

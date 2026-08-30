package me.bannerbound.com;

import me.bannerbound.com.civpm.entities.renderers.CPMFakeCitizenRenderer;
import me.bannerbound.com.registries.EntityRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = Bannerbound.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Bannerbound.MODID, value = Dist.CLIENT)
public class BannerboundClient {
    public BannerboundClient(ModContainer container) {

    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        EntityRegistry.registerEntityRenderers(event);
    }
}

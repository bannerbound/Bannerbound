package me.bannerbound.com;

import me.bannerbound.com.pms.civpm.CivPMClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Bannerbound.MODID)
public class BannerboundClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    //#region CivPM

    static {
        BUILDER.push("civpm");
    }

    private static final ModConfigSpec.BooleanValue CPM_WANDERER_ENABLED = BUILDER
            .comment(" When disabled, wanderers won't exist")
            .define("ClientCivPMWandererEnabled", true);

    private static final ModConfigSpec.BooleanValue CPM_SIMPLIFIED_WALKING = BUILDER
            .comment(" When enabled, wanderers will fly towards the location instead of checking for blocks, saving on gravity calculation costs & block checks")
            .define("ClientCivPMSimplifiedWalking", false);

    static {
        BUILDER.pop();
    }

    public static Boolean cpmWandererEnabled;
    public static Boolean cpmSimplifiedWalking;

    //#endregion CivPM

    static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onModConfigLoading(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            bakeConfig(event);
        }
    }

    @SubscribeEvent
    static void onModConfigReloading(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == SPEC) {
            bakeConfig(event);
        }
    }

    public static void bakeConfig(final ModConfigEvent event) {
        if (cpmWandererEnabled != CPM_WANDERER_ENABLED.get()) {
            cpmWandererEnabled = CPM_WANDERER_ENABLED.get();

            if (cpmWandererEnabled) {
                CivPMClient.getRegionsManager().loadWanderers();
            } else {
                CivPMClient.getRegionsManager().unloadWanderers();
            }
        }

        cpmSimplifiedWalking = CPM_SIMPLIFIED_WALKING.get();
    }
}

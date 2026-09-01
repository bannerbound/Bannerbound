package me.bannerbound.com;

import me.bannerbound.com.pms.civpm.CivPM;
import me.bannerbound.com.pms.civpm.managers.CPMWandererWalkManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = Bannerbound.MODID)
public class BannerboundServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue BANNERBOUND_DEVCOMMAND_ENABLED = BUILDER
            .comment(" Enable the dev command")
            .define("BannerboundDevcommandEnabled", false);


    public static Boolean bannerboundDevcommandEnabled = false;

    static {
        BUILDER.push("civpm");
    }

    //#region CivPM
    private static final ModConfigSpec.BooleanValue CPM_WANDERER_ENABLED = BUILDER
            .comment(" When disabled, wanderers won't exist")
            .define("CivPMWandererEnabled", true);

    private static final ModConfigSpec.BooleanValue CPM_WANDERER_WALKING = BUILDER
            .comment(" When disabled, wanderers will no longer walk. It will improve server performance for big servers by disabling, but the NPCs will feel more dead")
            .define("CivPMWandererWalk", true);

    private static final ModConfigSpec.BooleanValue CPM_WANDERER_COMMUNICATION = BUILDER
            .comment(" When disabled, wanderers will no longer interact with each other")
            .define("CivPMWandererCommunication", true);

    static {
        BUILDER.pop();
    }

    public static Boolean cpmWandererEnabled;
    public static Boolean cpmWandererWalking;
    public static Boolean cpmWandererCommunication;
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
        bannerboundDevcommandEnabled = BANNERBOUND_DEVCOMMAND_ENABLED.get();

        cpmWandererEnabled = CPM_WANDERER_ENABLED.get();
        cpmWandererWalking = CPM_WANDERER_WALKING.get();
        cpmWandererCommunication = CPM_WANDERER_COMMUNICATION.get();

        CivPM civPm = CivPM.getInstance();
        CPMWandererWalkManager walkManager = CivPM.getWandererWalkManager();

        if (cpmWandererEnabled && cpmWandererWalking) {
            civPm.addListener(walkManager);
        } else {
            civPm.removeListener(walkManager);
        }

        CivPM.getWandererManager().configUpdated();
    }
}

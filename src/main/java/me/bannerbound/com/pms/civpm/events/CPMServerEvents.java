package me.bannerbound.com.pms.civpm.events;

import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.pms.civpm.CivPM;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber (modid = Bannerbound.MODID)
public class CPMServerEvents {
    @SubscribeEvent
    public static void worldSaveTriggered(LevelEvent.Save event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.dimension() == ServerLevel.OVERWORLD) {
                CivPM.getRegionManager().saveAllChangedRegions(event);
            }
        }
    }
}

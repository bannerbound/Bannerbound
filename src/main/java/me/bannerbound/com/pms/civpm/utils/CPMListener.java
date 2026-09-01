package me.bannerbound.com.pms.civpm.utils;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public interface CPMListener {
    void tick(ServerTickEvent.Post event, ServerLevel overworld);
}

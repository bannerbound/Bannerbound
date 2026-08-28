package com.bannerbound.core.civpm.managers;

import com.bannerbound.core.civpm.data.CPMClientRegion;
import com.bannerbound.core.civpm.packets.clienttoserver.CPMRegionRequestPacket;
import com.bannerbound.core.civpm.utils.CPMMathUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

public class CPMClientRegionsManager {
    public static long current_region = 0L;
    private final Long2ObjectOpenHashMap<CPMClientRegion> cached_regions = new Long2ObjectOpenHashMap<>();

    public void playerChangedRegion() {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null) return;

        checkForUnloadedRegions();
        loadSurroundingRegions(CPMMathUtils.CPM2DUtils.blockToRegion(localPlayer.getBlockX(), localPlayer.getBlockZ()), true);
    }

    public void loadSurroundingRegions(Vector2i pos, boolean check_cache) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                long region_pos = CPMMathUtils.CPM2DUtils.pack(pos.x + x, pos.y + y);

                if (!check_cache || !cached_regions.containsKey(region_pos)) {
                    requestRegionDataFromServer(region_pos);
                }
            }
        }
    }

    public void requestRegionDataFromServer(long position) {
        PacketDistributor.sendToServer(new CPMRegionRequestPacket(position));
    }

    public Long2ObjectOpenHashMap<CPMClientRegion> cached_regions() { return cached_regions; }

    public void cacheRegion(CPMClientRegion region) {
        cached_regions.put(region.getPos(), region);
    }

    public void checkForUnloadedRegions() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        ObjectIterator<CPMClientRegion> iterator = cached_regions.values().iterator();

        while (iterator.hasNext()) {
            CPMClientRegion region = iterator.next();

            if (!CPMRegionsManager.isRegionLoadedByPlayer(region, player)) {
                region.cleanup();
                iterator.remove();
            }
        }
    }

    @Nullable
    public CPMClientRegion getRegion(long pos) {
        return cached_regions.get(pos);
    }
}

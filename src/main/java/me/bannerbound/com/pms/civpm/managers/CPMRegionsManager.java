package me.bannerbound.com.pms.civpm.managers;

import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.pms.civpm.data.CPMRegion;
import me.bannerbound.com.pms.civpm.data.CPMServerRegion;
import me.bannerbound.com.pms.civpm.utils.CPMMathUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CPMRegionsManager {
    Long2ObjectMap<CPMServerRegion> cached_regions = new Long2ObjectOpenHashMap<>();
    LongSet changed_regions = new LongOpenHashSet();

    public void saveAllChangedRegions(LevelEvent.Save event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        MinecraftServer server = serverLevel.getServer();

        Path worldDataDir;
        Path regionsFolder;

        try {
            worldDataDir = server.getWorldPath(LevelResource.ROOT).resolve("data");
            regionsFolder = worldDataDir.resolve("cpm_regions");
            Files.createDirectories(regionsFolder);
        } catch (IOException e) {
            Bannerbound.LOGGER.error("Failed to create region save directories: {}", e.getMessage());
            return;
        }

        List<ServerPlayer> playerList = serverLevel.players();

        for (long changedRegionPos : changed_regions) {
            CPMServerRegion region = cached_regions.get(changedRegionPos);
            if (region == null) continue;

            CPMServerRegion.Serialization.saveToFile(region, regionsFolder.resolve("region_" + region.getX() + "-" + region.getY() + ".dat"));
        }

        changed_regions.clear();

        ObjectIterator<CPMServerRegion> cached_regions_iterator = cached_regions.values().iterator();

        while (cached_regions_iterator.hasNext()) {
            CPMServerRegion region = cached_regions_iterator.next();
            boolean loaded = isRegionLoaded(region, playerList);

            if (!loaded) {
                cached_regions_iterator.remove();
            }
        }
    }

    public static boolean isRegionLoadedByPlayer(CPMRegion region, Player player) {
        return CPMMathUtils.CPM2DUtils.distanceToRegionSqr(region.getPos(), player.blockPosition()) < 9;
    }

    private static boolean isRegionLoaded(CPMServerRegion region, List<ServerPlayer> playerList) {
        boolean loaded = false;

        for (ServerPlayer player : playerList) {
            if (isRegionLoadedByPlayer(region, player)) {
                loaded = true;
            }
        }

        return loaded;
    }

    public void regionChanged(CPMRegion region) {
        changed_regions.add(region.getPos());
    }

    @Nullable
    public CPMServerRegion loadRegion(long pos) {
        return loadRegion(pos, true);
    }

    @Nullable
    public CPMServerRegion loadRegion(long pos, boolean cache) {
        CPMServerRegion cached_region = cached_regions.get(pos);

        if (cached_region != null) {
            return cached_region;
        }

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return null;

        Path worldDataDir;
        Path regionsFolder;

        try {
            worldDataDir = server.getWorldPath(LevelResource.ROOT).resolve("data");
            regionsFolder = worldDataDir.resolve("cpm_regions");
            Files.createDirectories(regionsFolder);
        } catch (IOException e) {
            Bannerbound.LOGGER.error("Failed to create region save directories: {}", e.getMessage());
            return null;
        }

        Path regionFile = regionsFolder.resolve("region_" + CPMMathUtils.CPM2DUtils.unpackX(pos) + "-" + CPMMathUtils.CPM2DUtils.unpackY(pos) + ".dat");
        if (!Files.exists(regionFile)) return null;

        if (cache) {
            CPMServerRegion region = CPMRegion.Serialization.loadFromFile(regionFile);
            cacheRegion(region);
            return region;
        } else {
            return CPMRegion.Serialization.loadFromFile(regionFile);
        }
    }

    public CPMServerRegion getRegion(long pos) {
        if (cached_regions.containsKey(pos)) {
            return cached_regions.get(pos);
        }

        CPMServerRegion loaded_region = loadRegion(pos, true);
        if (loaded_region != null) {
            return loaded_region;
        }

        CPMServerRegion new_region = new CPMServerRegion(pos);
        cacheRegion(new_region);
        return new_region;
    }

    public List<CPMServerRegion> getLoadedRegionsForPlayer(ServerPlayer player) {
        List<CPMServerRegion> regions = new ArrayList<>();
        long playerRegion = CPMMathUtils.CPM2DUtils.getRegionPosForPlayer(player);
        int centerRX = CPMMathUtils.CPM2DUtils.unpackX(playerRegion);
        int centerRZ = CPMMathUtils.CPM2DUtils.unpackY(playerRegion);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                regions.add(getRegionIfCached(CPMMathUtils.CPM2DUtils.pack(centerRX + dx, centerRZ + dz)));
            }
        }

        return regions;
    }

    @Nullable
    public CPMServerRegion getRegionIfCached(long pos) {
        return cached_regions.get(pos);
    }

    public void cacheRegion(CPMServerRegion region) {
        cached_regions.put(region.getPos(), region);
    }
}

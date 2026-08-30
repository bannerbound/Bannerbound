package me.bannerbound.com.civpm.managers;

import me.bannerbound.com.civpm.CivPM;
import me.bannerbound.com.civpm.data.CPMRegion;
import me.bannerbound.com.civpm.packets.servertoclient.CPMMoveWandererPacket;
import me.bannerbound.com.civpm.utils.CPMMathUtils;
import me.bannerbound.com.civpm.utils.CPMPathfinderUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class CPMWandererWalkManager {
    private final int MAX_CITIZENS_WALKING_PER_TRIGGER = 1;
    private final int WALK_TRIGGER_COOLDOWN = 10;
    private final float TRIGGER_CHANCE = 0.4F;

    private int walkCooldown = 0;

    public void tick(ServerTickEvent.Post event, ServerLevel overworld) {
        walkCooldown++;

        if (walkCooldown >= WALK_TRIGGER_COOLDOWN) {
            walkCooldown = 0;
            RandomSource source = overworld.getRandom();
            List<ServerPlayer> players = overworld.players();
            if (players.isEmpty()) return;

            Set<Long> activeRegions = new HashSet<>();
            for (ServerPlayer player : players) {
                long playerRegion = CPMMathUtils.CPM2DUtils.getRegionPosForPlayer(player);
                int centerRX = CPMMathUtils.CPM2DUtils.unpackX(playerRegion);
                int centerRZ = CPMMathUtils.CPM2DUtils.unpackY(playerRegion); 

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        activeRegions.add(CPMMathUtils.CPM2DUtils.pack(centerRX + dx, centerRZ + dz));
                    }
                }
            }

            Map<Long, List<CPMMoveWandererPacket>> pendingMovements = new HashMap<>();

            for (long targetRegionPos : activeRegions) {
                if (source.nextFloat() > TRIGGER_CHANCE) {
                    continue;
                }

                CPMRegion region = CivPM.getRegionManager().getRegionIfCached(targetRegionPos);
                if (region == null) continue;
                List<UUID> wandererIds = region.getWandererIds();
                if (wandererIds.isEmpty()) continue;

                int amountOfCitizens = MAX_CITIZENS_WALKING_PER_TRIGGER;
                int wanderersSize = wandererIds.size();

                if (amountOfCitizens > wanderersSize) {
                    amountOfCitizens = wanderersSize;
                }

                List<CPMMoveWandererPacket> regionPackets = new ArrayList<>();

                for (int i = 0; i < amountOfCitizens; i++) {
                    UUID randomId = wandererIds.get(source.nextInt(wanderersSize));
                    BlockPos currentPos = region.getWanderers().get(randomId);
                    if (currentPos == null) continue;

                    BlockPos targetPos = CPMPathfinderUtils.findRandomStraightPath(overworld, currentPos, 8);
                    if (targetPos != null) {
                        region.getWanderers().put(randomId, targetPos);
                        region.changed();

                        regionPackets.add(new CPMMoveWandererPacket(targetRegionPos, randomId, targetPos));
                    }
                }

                if (!regionPackets.isEmpty()) {
                    pendingMovements.put(targetRegionPos, regionPackets);
                }
            }

            
            for (ServerPlayer player : players) {
                long playerRegion = CPMMathUtils.CPM2DUtils.getRegionPosForPlayer(player);
                int centerRX = CPMMathUtils.CPM2DUtils.unpackX(playerRegion);
                int centerRZ = CPMMathUtils.CPM2DUtils.unpackY(playerRegion);

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        long targetRegionPos = CPMMathUtils.CPM2DUtils.pack(centerRX + dx, centerRZ + dz);

                        
                        List<CPMMoveWandererPacket> packets = pendingMovements.get(targetRegionPos);
                        if (packets != null) {
                            for (CPMMoveWandererPacket packet : packets) {
                                PacketDistributor.sendToPlayer(player, packet);
                            }
                        }
                    }
                }
            }
        }
    }
}
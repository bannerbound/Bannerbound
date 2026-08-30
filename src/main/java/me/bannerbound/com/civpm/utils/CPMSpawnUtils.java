package me.bannerbound.com.civpm.utils;

import me.bannerbound.com.civpm.entities.CPMFakeCitizenEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class CPMSpawnUtils {
    private static final AtomicInteger CLIENT_ID_COUNTER = new AtomicInteger(-Math.abs("civpm".hashCode()));
    private static final HashMap<UUID, Integer> UUID_TO_ID_CACHE = new HashMap<>();

    private static int getUniqueClientOnlyId(ClientLevel level) {
        int nextClientOnlyId = CLIENT_ID_COUNTER.getAndDecrement();

        while (level.getEntity(nextClientOnlyId) != null) {
            nextClientOnlyId = CLIENT_ID_COUNTER.getAndDecrement();
        }

        return nextClientOnlyId;
    }

    // we dont want to take up too many IDs. Cuz that would be bad :(
    private static int getUniqueClientOnlyIdForUUID(ClientLevel level, UUID uuid) {
        Integer id = UUID_TO_ID_CACHE.get(uuid);

        if (id != null) {
            return id;
        }

        int newId = getUniqueClientOnlyId(level);
        UUID_TO_ID_CACHE.put(uuid, newId);

        return newId;
    }

    public static void playerLeftWorld() {
        CLIENT_ID_COUNTER.set(-Math.abs("civpm".hashCode()));
        UUID_TO_ID_CACHE.clear();
    }

    public static void spawnClientOnlyEntity(ClientLevel level, Entity entity, boolean checkCache) {
        int uniqueId;

        if (checkCache) {
            uniqueId = getUniqueClientOnlyIdForUUID(level, entity.getUUID());
        } else {
            uniqueId = getUniqueClientOnlyId(level);
        }

        entity.setId(uniqueId);
        level.addEntity(entity);
    }

    public static CPMFakeCitizenEntity spawnFakeCitizenEntity(ClientLevel level, UUID uuid, BlockPos pos) {
        CPMFakeCitizenEntity entity = new CPMFakeCitizenEntity(level);
        entity.setUUID(uuid);
        entity.setInvulnerable(true);
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        float randomYaw = level.getRandom().nextFloat() * 360.0F;
        entity.setYRot(randomYaw);
        entity.setYBodyRot(randomYaw);
        entity.setYHeadRot(randomYaw);
        entity.yRotO = randomYaw;
        entity.yBodyRotO = randomYaw;
        entity.yHeadRotO = randomYaw;

        CPMSpawnUtils.spawnClientOnlyEntity(level, entity, true);

        return entity;
    }

}

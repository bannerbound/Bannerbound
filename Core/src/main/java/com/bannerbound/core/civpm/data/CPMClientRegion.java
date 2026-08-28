package com.bannerbound.core.civpm.data;

import com.bannerbound.core.civpm.entities.CPMFakeCitizenEntity;
import com.bannerbound.core.civpm.packets.utils.CPMPacketWandererEntry;
import com.bannerbound.core.civpm.utils.CPMSpawnUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CPMClientRegion extends CPMRegion {
    private final HashMap<UUID, CPMFakeCitizenEntity> fake_entities = new HashMap<>();

    public CPMClientRegion(long pos, List<CPMPacketWandererEntry> entries) {
        super(pos);

        for (CPMPacketWandererEntry entry : entries) {
            this.wanderers.put(entry.uuid(), entry.pos());
        }
    }

    public CPMClientRegion(long pos) {
        super(pos);
    }

    public HashMap<UUID, CPMFakeCitizenEntity> fake_entities() {
        return fake_entities;
    }

    public void addFakeEntity(CPMFakeCitizenEntity entity) {
        fake_entities.put(entity.getUUID(), entity);
    }

    public void removeFakeEntity(CPMFakeCitizenEntity entity) {
        fake_entities.remove(entity.getUUID());
    }

    public void cleanup() {
        for (CPMFakeCitizenEntity entity : fake_entities.values()) {
            entity.discard();
        }

        fake_entities.clear();
    }

    public void populateRegion(ClientLevel level) {
        if (!fake_entities.isEmpty()) {
            cleanup();
        }

        for (Map.Entry<UUID, BlockPos> entry : wanderers.entrySet()) {
            UUID uuid = entry.getKey();
            BlockPos pos = entry.getValue();

            CPMFakeCitizenEntity entity = CPMSpawnUtils.spawnFakeCitizenEntity(level, uuid, pos);
            fake_entities.put(uuid, entity);
        }
    }
}

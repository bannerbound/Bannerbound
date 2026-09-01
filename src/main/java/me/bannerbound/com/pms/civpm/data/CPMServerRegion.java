package me.bannerbound.com.pms.civpm.data;

import me.bannerbound.com.pms.civpm.packets.servertoclient.CPMSpawnWandererPacket;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

public class CPMServerRegion extends CPMRegion {
    public CPMServerRegion(long pos, List<WandererEntry> entries) {
        super(pos, entries);
    }

    public CPMServerRegion(long pos) {
        super(pos);
    }

    public void spawnCitizen(UUID uuid, BlockPos pos) {
        this.addWanderer(uuid, pos);

        PacketDistributor.sendToAllPlayers(new CPMSpawnWandererPacket(uuid, pos));
    }

    public void spawnCitizen(BlockPos pos) {
        spawnCitizen(UUID.randomUUID(), pos);
    }
}

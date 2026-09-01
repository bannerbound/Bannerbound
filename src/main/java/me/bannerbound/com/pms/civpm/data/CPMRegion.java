package me.bannerbound.com.pms.civpm.data;

import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.pms.civpm.CivPM;
import me.bannerbound.com.pms.civpm.packets.utils.CPMPacketWandererEntry;
import me.bannerbound.com.pms.civpm.utils.CPMMathUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CPMRegion {
    public record WandererEntry(UUID uuid, BlockPos pos) {
        public static final Codec<WandererEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("id").forGetter(WandererEntry::uuid),
                BlockPos.CODEC.fieldOf("pos").forGetter(WandererEntry::pos)
        ).apply(instance, WandererEntry::new));
    }

    public static class Serialization {
        public static final Codec<CPMServerRegion> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.optionalFieldOf("pos", 0L).forGetter(CPMRegion::getPos),
                WandererEntry.CODEC.listOf().optionalFieldOf("wanderers", List.of()).forGetter(CPMRegion::getWandererEntries)
        ).apply(instance, CPMServerRegion::new));

        public static void saveToFile(CPMServerRegion data, Path path) {
            try {
                CompoundTag tag = (CompoundTag) Serialization.CODEC.encodeStart(NbtOps.INSTANCE, data)
                        .getOrThrow(IllegalStateException::new);

                NbtIo.writeCompressed(tag, path);
            } catch (IOException | IllegalStateException e) {
                System.err.println("Failed to write region save file: " + e.getMessage());
            }
        }

        public static CPMServerRegion loadFromFile(Path path) {
            try {
                CompoundTag tag = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());

                return Serialization.CODEC.parse(NbtOps.INSTANCE, tag)
                        .getOrThrow(IllegalStateException::new);
            } catch (IOException | IllegalStateException e) {
                Bannerbound.LOGGER.error("Failed to read region save file: {}", e.getMessage());
                return null;
            }
        }
    }

    protected final long pos;
    protected final HashMap<UUID, BlockPos> wanderers;
    private final List<UUID> wandererIds = new ArrayList<>();
    protected boolean changed;

    public CPMRegion(long pos, List<WandererEntry> entries) {
        this.pos = pos;
        this.wanderers = new HashMap<>();
        for (WandererEntry entry : entries) {
            this.wanderers.put(entry.uuid(), entry.pos());
            this.wandererIds.add(entry.uuid());
        }
    }

    public CPMRegion(long pos) {
        this.pos = pos;
        this.wanderers = new HashMap<>();
    }

    public int getX() { return CPMMathUtils.CPM2DUtils.unpackX(pos); }
    public int getY() { return CPMMathUtils.CPM2DUtils.unpackY(pos); }

    public int getBlockX() {return getX() * 48;}
    public int getBlockY() {return getY() * 48;}

    public long getPos() { return pos; }

    public void changed() { CivPM.getRegionManager().regionChanged(this); }

    public HashMap<UUID, BlockPos> getWanderers() { return wanderers; }
    public List<UUID> getWandererIds() { return wandererIds; }

    public List<WandererEntry> getWandererEntries() {
        return wanderers.entrySet().stream()
                .map(entry -> new WandererEntry(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<CPMPacketWandererEntry> getStreamWandererEntries() {
        return wanderers.entrySet().stream()
                .map(entry -> new CPMPacketWandererEntry(entry.getKey(), entry.getValue()))
                .toList();
    }

    public void removeWanderer(UUID wanderer) {
        if (wanderers.containsKey(wanderer)) {
            wanderers.remove(wanderer);
            wandererIds.remove(wanderer);
            changed();
        }
    }

    public void addWanderer(UUID wanderer, BlockPos pos) {
        if (wanderers.size() > 3000) {
            throw new IllegalArgumentException("There can only be max 3000 Wanderers in a region");
        }

        if (!wanderers.containsKey(wanderer)) {
            wandererIds.add(wanderer);
        }

        wanderers.put(wanderer, pos);
        changed();
    }

    public void addWanderer(UUID wanderer, int x, int y, int z) {
        addWanderer(wanderer, new BlockPos(x, y, z));
    }

    @Override
    public String toString() {
        return String.format("Region(%d, %d)", getX(), getY());
    }

    public boolean isChanged() { return changed; }
    public void setChanged(boolean changed) { this.changed = changed; }
}
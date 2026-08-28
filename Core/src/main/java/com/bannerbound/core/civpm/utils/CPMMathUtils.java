package com.bannerbound.core.civpm.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import org.joml.Vector2i;

public class CPMMathUtils {
    public static class CPM2DUtils {
        public static long pack(int x, int y) {
            return ((long) x << 32) | (y & 0xFFFFFFFFL);
        }

        public static int unpackX(long packed) {
            return (int) (packed >> 32);
        }

        public static int unpackY(long packed) {
            return (int) packed;
        }

        public static long packX(long pos, int newX) {
            return ((long) newX << 32) | (pos & 0xFFFFFFFFL);
        }

        public static long packY(long pos, int newY) {
            return (pos & 0xFFFFFFFF00000000L) | (newY & 0xFFFFFFFFL);
        }

        public static long packBlockToRegion(int x, int y) {
            return pack(Mth.floor(x / 48.0), Mth.floor(y / 48.0));
        }

        public static Vector2i unpackToRegion(long packed) {
            return new Vector2i(unpackX(packed), unpackY(packed));
        }

        public static Vector2i unpackToVec(long packed) {
            return new Vector2i(unpackX(packed) * 48, unpackY(packed) * 48);
        }

        public static BlockPos unpackToBlock(long packed) {
            return new BlockPos(unpackX(packed) * 48, 0, unpackY(packed) * 48);
        }

        public static Vector2i blockToRegion(int x, int z) {
            return new Vector2i(Mth.floor(x / 48.0), Mth.floor(z / 48.0));
        }

        public static BlockPos blockToRegionBlock(int x, int z) {
            return new BlockPos(Mth.floor(x / 48.0), 0, Mth.floor(z / 48.0));
        }

        public static int distanceToRegionSqr(long regionPos, BlockPos pos) {
            BlockPos blockPos = blockToRegionBlock(pos.getX(), pos.getZ());
            return distanceToRegionSqr(regionPos, blockPos.getX(), blockPos.getZ());
        }

        public static int distanceToRegionSqr(long regionPos, Vector2i pos) {
            BlockPos blockPos = blockToRegionBlock(pos.x, pos.y);
            return distanceToRegionSqr(regionPos, blockPos.getX(), blockPos.getZ());
        }

        public static int distanceToRegionSqr(long regionPos, int x, int z) {
            int rx = unpackX(regionPos);
            int rz = unpackY(regionPos);

            int dx = rx - x;
            int dz = rz - z;

            return dx * dx + dz * dz;
        }

        public static int distanceToRegionSqr(long region1, long region2) {
            return distanceToRegionSqr(region1, unpackX(region2), unpackY(region2));
        }

        public static long getRegionPosForPlayer(Player player) {
            return pack(Mth.floor(player.getX() / 48.0), Mth.floor(player.getZ() / 48.0));
        }

        public static long offsetRegionPosBy(long region_pos, int x, int y) {
            int rx = CPMMathUtils.CPM2DUtils.unpackX(region_pos);
            int ry = CPMMathUtils.CPM2DUtils.unpackY(region_pos);
            return pack(rx + x, ry + y);
        }
    }
}

package me.bannerbound.com.civpm.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CPMPathfinderUtils {

    @Nullable
    public static BlockPos findRandomStraightPath(Level level, BlockPos startPos, int maxDistance) {
        int startRegionX = Mth.floor(startPos.getX() / 48.0);
        int startRegionZ = Mth.floor(startPos.getZ() / 48.0);

        RandomSource random = level.getRandom();
        float angle = random.nextFloat() * (float) (Math.PI * 2);
        int distance = random.nextInt(maxDistance - 3) + 4;
        double stepX = Mth.cos(angle);
        double stepZ = Mth.sin(angle);

        int currentY = startPos.getY();
        BlockPos.MutableBlockPos stepPos = new BlockPos.MutableBlockPos();

        for (int i = 1; i <= distance; i++) {
            int nextX = startPos.getX() + (int) Math.round(stepX * i);
            int nextZ = startPos.getZ() + (int) Math.round(stepZ * i);

            int nextRegionX = Mth.floor(nextX / 48.0);
            int nextRegionZ = Mth.floor(nextZ / 48.0);

            if (nextRegionX != startRegionX || nextRegionZ != startRegionZ) {
                return null;
            }

            stepPos.set(nextX, currentY, nextZ);

            if (isWalkable(level, stepPos, nextX, currentY, nextZ)) {
                continue;
            } else if (isWalkable(level, stepPos, nextX, currentY + 1, nextZ)) {
                currentY++;
            } else if (isWalkable(level, stepPos, nextX, currentY - 1, nextZ)) {
                currentY--;
            } else {
                return null;
            }
        }

        return new BlockPos(
                startPos.getX() + (int) Math.round(stepX * distance),
                currentY,
                startPos.getZ() + (int) Math.round(stepZ * distance)
        );
    }

    private static boolean isWalkable(Level level, BlockPos.MutableBlockPos pos, int x, int y, int z) {
        pos.set(x, y - 1, z);
        BlockState floorState = level.getBlockState(pos);
        if (floorState.getCollisionShape(level, pos).isEmpty()) return false;

        pos.set(x, y, z);
        BlockState bodyState = level.getBlockState(pos);
        if (!bodyState.getCollisionShape(level, pos).isEmpty()) return false;

        pos.set(x, y + 1, z);
        BlockState headState = level.getBlockState(pos);
        return headState.getCollisionShape(level, pos).isEmpty();
    }
}
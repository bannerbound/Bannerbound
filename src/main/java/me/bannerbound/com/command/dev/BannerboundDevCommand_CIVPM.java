package me.bannerbound.com.command.dev;

import com.mojang.brigadier.context.CommandContext;
import me.bannerbound.com.api.settlement.Era;
import me.bannerbound.com.pms.civpm.CivPM;
import me.bannerbound.com.pms.civpm.CivPMClient;
import me.bannerbound.com.pms.civpm.data.CPMClientRegion;
import me.bannerbound.com.pms.civpm.data.CPMRegion;
import me.bannerbound.com.pms.civpm.data.CPMServerRegion;
import me.bannerbound.com.pms.civpm.entities.CPMFakeCitizenEntity;
import me.bannerbound.com.pms.civpm.utils.CPMMathUtils;
import me.bannerbound.com.pms.civpm.utils.CPMPathfinderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.UUID;

public class BannerboundDevCommand_CIVPM {
    public static int executeCPMSpawnWanderer(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();

        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("You must be a player to use this command!"));
            return 0;
        }

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        CPMServerRegion region = CivPM.getRegionManager().getRegion(region_pos);
        region.spawnCitizen(player.blockPosition());

        ctx.getSource().sendSuccess(() -> Component.literal("Successfully spawned a wanderer"), false);
        return 1;
    }

    public static int executeCPMCurrentRegion(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();

        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("You must be a player to use this command!"));
            return 0;
        }

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        ctx.getSource().sendSuccess(() -> Component.literal(new CPMRegion(region_pos).toString()), false);

        return 1;
    }

    public static int executeCPMPathfinderBenchmark(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();

        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("You must be a player to use this command!"));
            return 0;
        }

        {
            BlockPos startPos = player.blockPosition();
            long start = System.nanoTime();

            int runs = 10000;
            for (int i = 0; i < runs; i++) {
                CPMPathfinderUtils.findRandomStraightPath(player.level(), startPos, 8);
            }

            long duration = System.nanoTime() - start;
            player.sendSystemMessage(Component.literal("Total time for 10,000 runs: " + (duration / 1_000_000) + " ms"));
            player.sendSystemMessage(Component.literal("True average time per run: " + (duration / runs) + " nanoseconds"));
        }
        {
            long start = System.nanoTime();

            int runs = 100000;
            for (int i = 0; i < runs; i++) {
                List<ServerPlayer> players = ((ServerLevel) player.level()).players();
                for (ServerPlayer p : players) {
                    long region_pos = CPMMathUtils.CPM2DUtils.getRegionPosForPlayer(p);
                    CPMRegion region = CivPM.getRegionManager().getRegionIfCached(region_pos);
                    if (region == null) continue;
                    List<UUID> wandererIds = region.getWandererIds();
                    if (wandererIds.isEmpty()) continue;
                }
            }

            long duration = System.nanoTime() - start;
            player.sendSystemMessage(Component.literal("True average outer-loop overhead per tick: " + (duration / runs) + " nanoseconds"));
        }

        return 1;
    }

    public static int executeCPMPopulateRegion(CommandContext<CommandSourceStack> ctx, int amountToSpawn) {
        ServerPlayer player = ctx.getSource().getPlayer();

        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("You must be a player to use this command!"));
            return 0;
        }

        ServerLevel level = (ServerLevel) player.level();

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        CPMServerRegion region = CivPM.getRegionManager().getRegion(region_pos);

        RandomSource randomSource = level.getRandom();

        int spawned = 0;

        if (region.getWanderers().isEmpty()) {
            for (int i = 0; i < amountToSpawn; i++) {
                int x = region.getBlockX() + randomSource.nextInt(48);
                int z = region.getBlockY() + randomSource.nextInt(48);

                int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

                if (!level.getFluidState(new BlockPos(x, height - 1, z)).isEmpty()) continue;

                region.spawnCitizen(new BlockPos(x, height, z));
                spawned++;
            }
        }

        player.sendSystemMessage(Component.literal("Spawned " + spawned + " citizens!"));

        return 1;
    }

    public static int executeCPMCleanup(CommandContext<CommandSourceStack> ctx) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return 0;

        for (Entity entity : server.overworld().getEntities().getAll()) {
            if (entity instanceof CPMFakeCitizenEntity citizenEntity) {
                entity.discard();
            }
        }

        return 1;
    }

    public static int executeCPMClientSetSkin(CommandContext<CommandSourceStack> ctx, int skin) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        CPMClientRegion region = CivPMClient.getRegionsManager().getRegion(region_pos);

        if (region == null) return 0;

        for (CPMFakeCitizenEntity entity : region.fake_entities().values()) {
            entity.setTextureVariant(skin);
        }

        return 1;
    }

    public static int executeCPMClientSetEra(CommandContext<CommandSourceStack> ctx, String eraString) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        CPMClientRegion region = CivPMClient.getRegionsManager().getRegion(region_pos);

        if (region == null) return 0;

        Era era = Era.fromName(eraString);

        if (era == null) {
            era = Era.ANCIENT;
        }

        for (CPMFakeCitizenEntity entity : region.fake_entities().values()) {
            entity.setEra(era);
        }

        return 1;
    }

    public static int executeCPMClientFreeze(CommandContext<CommandSourceStack> ctx, boolean freeze) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        CPMClientRegion region = CivPMClient.getRegionsManager().getRegion(region_pos);

        if (region == null) return 0;

        for (CPMFakeCitizenEntity entity : region.fake_entities().values()) {
            entity.setIsFrozen(freeze);
        }

        return 1;
    }
}

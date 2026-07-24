package com.bannerbound.core.command;

import com.bannerbound.core.BannerboundCore;
import com.bannerbound.core.civpm.CivPM;
import com.bannerbound.core.civpm.data.CPMRegion;
import com.bannerbound.core.civpm.utils.CPMMathUtils;
import com.bannerbound.core.civpm.utils.CPMPathfinderUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;
import java.util.UUID;

/*
Pretty much a Development-Only command
 */

@EventBusSubscriber(modid = BannerboundCore.MODID)
public class BannerboundDevCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (FMLEnvironment.production) {
            return;
        }

        LiteralArgumentBuilder<CommandSourceStack> devCommand =
                Commands.literal("bannerbounddev").requires(source -> source.hasPermission(2))
                        .then(Commands.literal("cpm")
                                .then(Commands.literal("spawn_wanderer").executes(BannerboundDevCommand::executeSpawnWanderer))
                                .then(Commands.literal("current_region").executes(BannerboundDevCommand::executeCurrentRegion))
                                .then(Commands.literal("benchmark_pathfinder").executes(BannerboundDevCommand::executeCPMPathfinderBenchmark))
                        );

        event.getDispatcher().register(devCommand);

    }

    private static int executeSpawnWanderer(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();

        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("You must be a player to use this command!"));
            return 0;
        }

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        CPMRegion region = CivPM.getRegionManager().getRegion(region_pos);
        region.addWanderer(UUID.randomUUID(), player.getBlockX(), player.getBlockY(), player.getBlockZ());

        // TODO: Send spawn packet

        ctx.getSource().sendSuccess(() -> Component.literal("Successfully spawned a wanderer"), false);
        return 1;
    }

    private static int executeCurrentRegion(CommandContext<CommandSourceStack> ctx) {
        ServerPlayer player = ctx.getSource().getPlayer();

        if (player == null) {
            ctx.getSource().sendFailure(Component.literal("You must be a player to use this command!"));
            return 0;
        }

        long region_pos = CPMMathUtils.CPM2DUtils.packBlockToRegion(player.getBlockX(), player.getBlockZ());
        ctx.getSource().sendSuccess(() -> Component.literal(new CPMRegion(region_pos).toString()), false);

        return 1;
    }

    private static int executeCPMPathfinderBenchmark(CommandContext<CommandSourceStack> ctx) {
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
            BlockPos startPos = player.blockPosition();
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
}

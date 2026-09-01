package me.bannerbound.com.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.bannerbound.com.Bannerbound;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.bannerbound.com.BannerboundServerConfig;
import me.bannerbound.com.command.dev.BannerboundDevCommand_CIVPM;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Bannerbound.MODID)
public class BannerboundDevCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        if (FMLEnvironment.production && !BannerboundServerConfig.bannerboundDevcommandEnabled) {
            return;
        }

        LiteralArgumentBuilder<CommandSourceStack> devCommand =
                Commands.literal("bannerbounddev").requires(source -> source.hasPermission(2))
                        .then(Commands.literal("civpm")
                                .then(Commands.literal("spawn_wanderer").executes(BannerboundDevCommand_CIVPM::executeCPMSpawnWanderer))
                                .then(Commands.literal("current_region").executes(BannerboundDevCommand_CIVPM::executeCPMCurrentRegion))
                                .then(Commands.literal("benchmark_pathfinder").executes(BannerboundDevCommand_CIVPM::executeCPMPathfinderBenchmark))
                                .then(Commands.literal("populate_region").then(
                                        Commands.argument("amount", IntegerArgumentType.integer())
                                            .executes(ctx -> BannerboundDevCommand_CIVPM.executeCPMPopulateRegion(ctx, IntegerArgumentType.getInteger(ctx, "amount"))))
                                        .executes(ctx -> BannerboundDevCommand_CIVPM.executeCPMPopulateRegion(ctx, 30)))
                                .then(Commands.literal("cleanup").executes(BannerboundDevCommand_CIVPM::executeCPMCleanup))
                        )
                        .then(Commands.literal("arrowpm")
                                .then(Commands.literal("spawn_arrow"))
                                .then(Commands.literal("clear_arrows"))
                                .then(Commands.literal("barrage"))
                        )
                ;

        event.getDispatcher().register(devCommand);
    }
}

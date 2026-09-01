package me.bannerbound.com.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.command.dev.BannerboundDevCommand_ARROWPM;
import me.bannerbound.com.command.dev.BannerboundDevCommand_CIVPM;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = Bannerbound.MODID, value = Dist.CLIENT)
public class BannerboundDevCommandClient {
    @SubscribeEvent
    public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        if (FMLEnvironment.production) {
            return;
        }

        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                Commands.literal("bannerboundclientdev")
                        .then(Commands.literal("arrowpm")
                                .then(Commands.literal("spawn_arrow").executes(BannerboundDevCommand_ARROWPM::executeAPMSpawnArrow))
                                .then(Commands.literal("clear_arrows").executes(BannerboundDevCommand_ARROWPM::executeAPMClearArrows))
                                .then(Commands.literal("barrage").executes(BannerboundDevCommand_ARROWPM::executeAPMBarrage))
                                .then(Commands.literal("lastbarrage").executes(BannerboundDevCommand_ARROWPM::executeAPMLastBarrage))
                        )
                        .then(Commands.literal("civpm")
                                .then(Commands.literal("setera")
                                        .then(Commands.argument("era", StringArgumentType.string())
                                            .executes(ctx -> BannerboundDevCommand_CIVPM.executeCPMClientSetEra(ctx, StringArgumentType.getString(ctx, "era")))))

                                .then(Commands.literal("setskin")
                                        .then(Commands.argument("skin", IntegerArgumentType.integer())
                                                .executes(ctx -> BannerboundDevCommand_CIVPM.executeCPMClientSetSkin(ctx, IntegerArgumentType.getInteger(ctx, "skin")))))

                                .then(Commands.literal("setfrozen")
                                        .then(Commands.argument("frozen", IntegerArgumentType.integer())
                                                .executes(ctx -> BannerboundDevCommand_CIVPM.executeCPMClientFreeze(ctx, IntegerArgumentType.getInteger(ctx, "frozen") != 0))))
                        )
        );
    }
}

package me.bannerbound.com.command.dev;

import com.mojang.brigadier.context.CommandContext;
import me.bannerbound.com.pms.PMSpawnUtils;
import me.bannerbound.com.pms.arrowpm.entities.APMArrowEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BannerboundDevCommand_ARROWPM {
    public static List<APMArrowEntity> cachedEntities = new ArrayList<>();

    static Vec3 lastPosition = Vec3.ZERO;
    static Vec3 lastRotation = Vec3.ZERO;

    public static int executeAPMSpawnArrow(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        APMArrowEntity arrow = new APMArrowEntity(player.level());
        Vec3 shootVec = player.getLookAngle().scale(2.0);
        arrow.setDeltaMovement(shootVec);

        double horizontalDistance = Math.sqrt(shootVec.x * shootVec.x + shootVec.z * shootVec.z);
        arrow.setYRot((float) (Mth.atan2(shootVec.x, shootVec.z) * (180F / (float) Math.PI)));
        arrow.setXRot((float) (Mth.atan2(shootVec.y, horizontalDistance) * (180F / (float) Math.PI)));
        arrow.yRotO = arrow.getYRot();
        arrow.xRotO = arrow.getXRot();

        arrow.setPos(player.getEyePosition());


        if (player.level() instanceof ClientLevel clientLevel) {
            PMSpawnUtils.spawnClientOnlyEntity(clientLevel, arrow, false);
        }

        cachedEntities.add(arrow);

        return 1;
    }

    public static int executeAPMClearArrows(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        int amount = cachedEntities.size();

        for (APMArrowEntity entity : cachedEntities) {
            entity.discard();
        }

        cachedEntities.clear();

        return 1;
    }

    public static int executeAPMBarrage(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        if (!(player.level() instanceof ClientLevel clientLevel)) return 0;

        Vec3 startPosition = player.getEyePosition().add(0, 5, 0);
        Vec3 direction = player.getLookAngle();

        lastPosition = startPosition;
        lastRotation = direction;

        int spawned = spawnBarrage(clientLevel, startPosition, direction, 100, 0.5, 2.0);

        return 1;
    }

    public static int executeAPMLastBarrage(CommandContext<CommandSourceStack> context) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return 0;

        if (!(player.level() instanceof ClientLevel clientLevel)) return 0;

        int spawned = spawnBarrage(clientLevel, lastPosition, lastRotation, 100, 0.5, 2.0);

        //player.sendSystemMessage(Component.literal("Spawned a barrage of " + spawned + " arrows!"));
        return 1;
    }

    public static int spawnBarrage(ClientLevel clientLevel, Vec3 origin, Vec3 direction, int gridSize, double spacing, double speed) {
        Vec3 normDir = direction.normalize();

        Vec3 right;
        if (Math.abs(normDir.y) > 0.99) {
            right = new Vec3(1, 0, 0);
        } else {
            right = new Vec3(-normDir.z, 0, normDir.x).normalize();
        }
        Vec3 up = right.cross(normDir).normalize();

        double offset = (gridSize - 1) * spacing / 2.0;
        int spawnedCount = 0;

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                double xOffset = (col * spacing) - offset;
                double yOffset = (row * spacing) - offset;

                Vec3 spawnPos = origin
                        .add(right.scale(xOffset))
                        .add(up.scale(yOffset));

                APMArrowEntity arrow = new APMArrowEntity(clientLevel);

                double speedVariation = (speed * 0.9) + (Math.random() * (speed * 0.2));
                Vec3 shootVec = normDir.scale(speedVariation);

                arrow.setDeltaMovement(shootVec);
                arrow.setPos(spawnPos);

                double horizontalDistance = Math.sqrt(shootVec.x * shootVec.x + shootVec.z * shootVec.z);
                float yRot = (float) (Mth.atan2(shootVec.x, shootVec.z) * (180F / (float) Math.PI));
                float xRot = (float) (Mth.atan2(shootVec.y, horizontalDistance) * (180F / (float) Math.PI));

                arrow.setYRot(yRot);
                arrow.setXRot(xRot);
                arrow.yRotO = yRot;
                arrow.xRotO = xRot;

                arrow.xo = spawnPos.x;
                arrow.yo = spawnPos.y;
                arrow.zo = spawnPos.z;

                PMSpawnUtils.spawnClientOnlyEntity(clientLevel, arrow, false);
                cachedEntities.add(arrow);
                spawnedCount++;
            }
        }

        return spawnedCount;
    }
}

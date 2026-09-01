package me.bannerbound.com.pms.arrowpm.entities;

import me.bannerbound.com.api.registries.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class APMArrowEntity extends Entity {
    static final double GRAVITY = 0.05;
    static final double DRAG = 0.99;

    public APMArrowEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    public APMArrowEntity(Level level) {
        super(EntityRegistry.APM_ARROW_ENTITY.get(), level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {}

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {}

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {}

    protected void renderingTick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.tickCount++;
    }

    protected void movementTick() {
        Vec3 movement = this.getDeltaMovement();

        this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);

        double nextVx = movement.x * DRAG;
        double nextVy = (movement.y - GRAVITY) * DRAG;
        double nextVz = movement.z * DRAG;

        this.setDeltaMovement(nextVx, nextVy, nextVz);

        double horizontalDistance = Math.sqrt(nextVx * nextVx + nextVz * nextVz);
        if (horizontalDistance > 0.001) {
            float yRot = (float) (Mth.atan2(nextVx, nextVz) * (180F / (float) Math.PI));
            float xRot = (float) (Mth.atan2(nextVy, horizontalDistance) * (180F / (float) Math.PI));

            this.setYRot(yRot);
            this.setXRot(xRot);
        }
    }

    @Override
    public void tick() {
        renderingTick();
        movementTick();

        /*
        if (this.tickCount > MAX_LIFETIME) {
            this.discard();
        }
        * */
    }
}

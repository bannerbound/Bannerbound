package me.bannerbound.com.civpm.entities;

import me.bannerbound.com.Bannerbound;
import me.bannerbound.com.api.settlement.CitizenGender;
import me.bannerbound.com.api.settlement.Era;
import me.bannerbound.com.registries.EntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CPMFakeCitizenEntity extends Mob {
    private static final int WALK_TIMEOUT = 120;

    private final int textureVariant;

    private int gravityCheckTick = 0;
    private boolean isFalling = false;
    protected CPMFakeCitizenEntity socializingWith = null;
    private int lookCooldown = 0;
    private float targetYaw = 0.0F;
    private float targetHeadYaw = 0.0F;
    private Vec3 walkTargetVec = null;
    private int walkTimeoutSpent = 0;

    public CPMFakeCitizenEntity(EntityType<? extends Mob> entityType, Level level) {
        super(entityType, level);

        textureVariant = level.getRandom().nextInt(4) + 1;
    }

    public CPMFakeCitizenEntity(Level level) {
        super(EntityRegistry.CPM_FAKE_CITIZEN_ENTITY.get(), level);

        textureVariant = level.getRandom().nextInt(4) + 1;
        gravityCheckTick = level.getRandom().nextInt(20);
    }

    public boolean isChild() {
        return false;
    }

    public CitizenGender getGender() {
        return CitizenGender.MALE;
    }

    public int getHappiness() {
        return 75;
    }

    public Era getEra() {
        return Era.ANCIENT;
    }

    public int getTextureVariant() {
        return textureVariant;
    }

    public void setWalkTarget(BlockPos targetPos) {
        this.walkTargetVec = Vec3.atBottomCenterOf(targetPos);
        this.isFalling = false;
    }

    private boolean isBlockBelowSolid() {
        BlockPos posBelow = BlockPos.containing(this.getX(), this.getY() - 0.01, this.getZ());
        BlockState stateBelow = this.level().getBlockState(posBelow);
        return !stateBelow.getCollisionShape(this.level(), posBelow).isEmpty();
    }

    protected void renderingTick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();
        this.yHeadRotO = this.yHeadRot;
        this.yBodyRotO = this.yBodyRot;
        this.tickCount++;

        double speed2d = this.getDeltaMovement().horizontalDistance();
        float animSpeed = (float) Math.min(speed2d * 6.0F, 1.0F);
        this.walkAnimation.update(animSpeed, 0.4F);
    }

    protected void physicsTick() {
        if (isFalling) {
            Vec3 delta = this.getDeltaMovement();
            delta = delta.add(0, -0.08, 0);
            this.setDeltaMovement(delta);

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (this.onGround()) {
                this.isFalling = false;
                this.setDeltaMovement(Vec3.ZERO);
            };
        } else {
            gravityCheckTick++;

            if (gravityCheckTick > 20) {
                gravityCheckTick = 0;

                if (!this.isBlockBelowSolid()) {
                    this.isFalling = true;
                    this.setOnGround(false);
                    System.out.println(this.stringUUID + " is off the ground!");
                }
            }
        }
    }

    private void randomLookingTick() {
        lookCooldown--;

        if (lookCooldown <= 0) {
            lookCooldown = this.level().getRandom().nextInt(140) + 60;

            boolean headOnly = this.level().getRandom().nextBoolean();

            if (headOnly) {
                targetYaw = yBodyRot;
                float headOffset = this.level().getRandom().nextFloat() * 120.0F - 60.0F;
                targetHeadYaw = targetYaw + headOffset;
            } else {
                targetYaw = this.getYRot() + (this.level().getRandom().nextFloat() * 120.0F - 60.0F);
                float headOffset = this.level().getRandom().nextFloat() * 30.0F - 15.0F;
                targetHeadYaw = targetYaw + headOffset;
            }
        }

        float smoothBodyYaw = Mth.approachDegrees(yBodyRot, targetYaw, 3.0F);
        this.setYBodyRot(smoothBodyYaw);
        this.setYRot(smoothBodyYaw);

        float smoothHeadYaw = Mth.approachDegrees(yHeadRot, targetHeadYaw, 6.0F);

        float headBodyDiff = Mth.wrapDegrees(smoothHeadYaw - smoothBodyYaw);
        float maxNeckAngle = 65.0F;

        if (Math.abs(headBodyDiff) > maxNeckAngle) {
            smoothHeadYaw = smoothBodyYaw + Mth.clamp(headBodyDiff, -maxNeckAngle, maxNeckAngle);
        }

        this.setYHeadRot(smoothHeadYaw);
    }

    private void walkingTick() {
        if (walkTargetVec == null) return;

        walkTimeoutSpent++;

        if (walkTimeoutSpent >= WALK_TIMEOUT) {
            this.setOnGround(true);
            this.isFalling = false;
            this.setPos(walkTargetVec);
            walkTargetVec = null;
            walkTimeoutSpent = 0;
            this.setDeltaMovement(Vec3.ZERO);
            return;
        }

        Vec3 pos = this.position();
        Vec3 dir = walkTargetVec.subtract(pos);

        double distanceSqr = dir.horizontalDistanceSqr();

        if (distanceSqr < 0.25) {
            this.walkTargetVec = null;
            this.setDeltaMovement(Vec3.ZERO);
            walkTimeoutSpent = 0;
        } else {
            dir = dir.normalize().scale(0.24);

            double yVelocity = this.getDeltaMovement().y;
            if (!this.onGround()) {
                yVelocity -= 0.08;
            } else {
                yVelocity = Math.max(0, yVelocity);
            }

            this.setDeltaMovement(new Vec3(dir.x, yVelocity, dir.z));

            float targetAngle = (float) (Mth.atan2(-dir.x, dir.z) * (180F / Math.PI));

            float smoothBodyAngle = Mth.approachDegrees(this.yBodyRot, targetAngle, 10.0F);
            this.setYRot(smoothBodyAngle);
            this.setYBodyRot(smoothBodyAngle);

            float smoothHeadAngle = Mth.approachDegrees(this.yHeadRot, targetAngle, 15.0F);
            this.setYHeadRot(smoothHeadAngle);

            this.move(MoverType.SELF, this.getDeltaMovement());

            if (this.horizontalCollision && this.onGround()) {
                Vec3 currentVel = this.getDeltaMovement();

                this.setDeltaMovement(new Vec3(currentVel.x, 0.5F, currentVel.z));

                this.setOnGround(false);
            }
        }
    }

    @Override
    public void tick() {
        this.renderingTick();
        this.physicsTick();

        if (walkTargetVec != null) {
            this.walkingTick();
        } else {
            if (socializingWith == null)
                this.randomLookingTick();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0);
    }
}

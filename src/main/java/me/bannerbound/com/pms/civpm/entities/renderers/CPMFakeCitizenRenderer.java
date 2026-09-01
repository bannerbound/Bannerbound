package me.bannerbound.com.pms.civpm.entities.renderers;

import me.bannerbound.com.api.settlement.CitizenGender;
import me.bannerbound.com.api.settlement.Era;
import me.bannerbound.com.pms.civpm.entities.CPMFakeCitizenEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
@ApiStatus.Internal
public class CPMFakeCitizenRenderer extends HumanoidMobRenderer<CPMFakeCitizenEntity, HumanoidModel<CPMFakeCitizenEntity>> {
    public static CPMFakeCitizenEntity CURRENT_RENDER;

    private static final ResourceLocation FALLBACK_TEXTURE =
        ResourceLocation.fromNamespaceAndPath("bannerbound", "textures/entity/citizen.png");
    private static final int MAX_VARIANT_PROBE = 16;

    private final HumanoidModel<CPMFakeCitizenEntity> wideModel;
    private final HumanoidModel<CPMFakeCitizenEntity> slimModel;
    private final Map<String, Integer> variantCountCache = new HashMap<>();

    public CPMFakeCitizenRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER)), 0.5f);
        this.wideModel = this.getModel();
        this.slimModel = new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_SLIM));
        this.addLayer(new HumanoidArmorLayer<>(this,
            new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
            new HumanoidModel<>(ctx.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
            ctx.getModelManager()));
    }

    private static final float CHILD_SCALE = 0.65f;

    @Override
    public void render(CPMFakeCitizenEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        // swap body model before super.render; armor layers keep their own models
        HumanoidModel<CPMFakeCitizenEntity> body = entity.getGender() == CitizenGender.FEMALE ? slimModel : wideModel;
        this.model = body;
        HumanoidModel.ArmPose usePose = HumanoidModel.ArmPose.EMPTY;
        boolean usedMainHand = true;
        if (entity.isUsingItem()) {
            net.minecraft.world.item.UseAnim anim = entity.getUseItem().getUseAnimation();
            if (anim == net.minecraft.world.item.UseAnim.SPEAR) {
                usePose = HumanoidModel.ArmPose.THROW_SPEAR;
            } else if (anim == net.minecraft.world.item.UseAnim.BOW) {
                usePose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
            if (usePose != HumanoidModel.ArmPose.EMPTY) {
                usedMainHand = entity.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND;
            }
        }
        boolean rightIsMain = entity.getMainArm() == net.minecraft.world.entity.HumanoidArm.RIGHT;
        boolean useRightArm = usedMainHand == rightIsMain;
        // reset both arms each frame: the gender models are shared/reused across citizens
        body.rightArmPose = useRightArm ? usePose : HumanoidModel.ArmPose.EMPTY;
        body.leftArmPose = useRightArm ? HumanoidModel.ArmPose.EMPTY : usePose;
        body.crouching = entity.isCrouching();
        CURRENT_RENDER = entity;
        try {
            if (entity.isChild()) {
                poseStack.pushPose();
                poseStack.scale(CHILD_SCALE, CHILD_SCALE, CHILD_SCALE);
                super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
                poseStack.popPose();
            } else {
                super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
            }
        } finally {
            CURRENT_RENDER = null;
        }
        // draw AFTER super.render: back in clean world space, before the model flip mangles font
        //SpeechBubbleLayer.draw(entity, poseStack, buffer, packedLight);
        //SpeechBubbleLayer.drawBlocked(entity, poseStack, buffer, packedLight);
        maybeEmitUnhappyParticles(entity);
        maybeEmitHappyParticles(entity);
    }

    private static final int UNHAPPY_THRESHOLD = 30;
    private static final int VERY_UNHAPPY_THRESHOLD = 15;
    private static final int HAPPY_THRESHOLD = 80;
    private static final int VERY_HAPPY_THRESHOLD = 95;

    private static void maybeEmitUnhappyParticles(CPMFakeCitizenEntity entity) {
        if (!entity.level().isClientSide || entity.isChild()) {
            return;
        }
        int happiness = entity.getHappiness();
        if (happiness > UNHAPPY_THRESHOLD) {
            return;
        }
        var random = entity.getRandom();
        float chance = happiness <= VERY_UNHAPPY_THRESHOLD ? 0.010f : 0.004f;
        if (random.nextFloat() >= chance) {
            return;
        }
        double jitter = 0.30;
        double x = entity.getX() + (random.nextDouble() - 0.5) * jitter;
        double z = entity.getZ() + (random.nextDouble() - 0.5) * jitter;
        double y = entity.getEyeY() + 0.5;
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.ANGRY_VILLAGER, x, y, z, 0.0, 0.0, 0.0);
    }

    private static void maybeEmitHappyParticles(CPMFakeCitizenEntity entity) {
        if (!entity.level().isClientSide || entity.isChild()) {
            return;
        }
        int happiness = entity.getHappiness();
        if (happiness < HAPPY_THRESHOLD) {
            return;
        }
        var random = entity.getRandom();
        float chance = happiness >= VERY_HAPPY_THRESHOLD ? 0.005f : 0.0025f;
        if (random.nextFloat() >= chance) {
            return;
        }
        double jitter = 0.30;
        double x = entity.getX() + (random.nextDouble() - 0.5) * jitter;
        double z = entity.getZ() + (random.nextDouble() - 0.5) * jitter;
        double y = entity.getEyeY() + 0.5;
        entity.level().addParticle(
            net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, x, y, z, 0.0, 0.0, 0.0);
    }

    @Override
    public ResourceLocation getTextureLocation(CPMFakeCitizenEntity entity) {
        CitizenGender gender = entity.getGender();
        Era era = entity.getEra();
        String setKey = gender.texturePrefix() + "_" + era.key();
        int variantCount = variantCountCache.computeIfAbsent(setKey, this::probeVariantCount);
        if (variantCount <= 0) {
            return FALLBACK_TEXTURE;
        }
        int variant = Math.floorMod(entity.getTextureVariant(), variantCount) + 1;
        return textureFor(setKey, variant);
    }

    private int probeVariantCount(String setKey) {
        var resourceManager = Minecraft.getInstance().getResourceManager();
        int count = 0;
        for (int n = 1; n <= MAX_VARIANT_PROBE; n++) {
            if (resourceManager.getResource(textureFor(setKey, n)).isPresent()) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private static ResourceLocation textureFor(String setKey, int variant) {
        return ResourceLocation.fromNamespaceAndPath("bannerbound",
            String.format("textures/entity/citizen/%s_%02d.png", setKey, variant));
    }
}

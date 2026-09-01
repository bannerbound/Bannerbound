package me.bannerbound.com.pms.arrowpm.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import me.bannerbound.com.pms.arrowpm.entities.APMArrowEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class APMArrowEntityRenderer extends EntityRenderer<APMArrowEntity> {
    private static final ResourceLocation ARROW_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public APMArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(APMArrowEntity entity) {
        return ARROW_LOCATION;
    }

    @Override
    public void render(APMArrowEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float yRot = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F;
        float xRot = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        poseStack.mulPose(Axis.ZP.rotationDegrees(xRot));

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        PoseStack.Pose pose = poseStack.last();

        drawVertex(pose, consumer, -8, -2, -2, 0.0F, 0.15625F, -1, 0, 0, packedLight);
        drawVertex(pose, consumer, -8, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, packedLight);
        drawVertex(pose, consumer, -8, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, packedLight);
        drawVertex(pose, consumer, -8, 2, -2, 0.0F, 0.3125F, -1, 0, 0, packedLight);

        drawVertex(pose, consumer, -8, 2, -2, 0.0F, 0.15625F, 1, 0, 0, packedLight);
        drawVertex(pose, consumer, -8, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, packedLight);
        drawVertex(pose, consumer, -8, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, packedLight);
        drawVertex(pose, consumer, -8, -2, -2, 0.0F, 0.3125F, 1, 0, 0, packedLight);

        for (int i = 0; i < 4; ++i) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            drawVertex(pose, consumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, packedLight);
            drawVertex(pose, consumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, packedLight);
            drawVertex(pose, consumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, packedLight);
            drawVertex(pose, consumer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, packedLight);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    public void drawVertex(PoseStack.Pose pose, VertexConsumer consumer, int x, int y, int z, float u, float v, int nx, int ny, int nz, int packedLight) {
        consumer.addVertex(pose, (float) x * 0.05625F, (float) y * 0.05625F, (float) z * 0.05625F)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(packedLight)
                .setNormal(pose, (float) nx, (float) ny, (float) nz);
    }
}
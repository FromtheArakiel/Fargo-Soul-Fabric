package dev.arakiel.fargosoulfabric.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.render.AbstractSummonRenderer;
import dev.arakiel.fargosoulfabric.client.render.helper.SphereRendererHelper;
import dev.arakiel.fargosoulfabric.common.entity.TrackingBlood;
import net.minecraft.client.renderer.MultiBufferSource;

/** The blood a hemorrhage leaves behind, which flies to the next target. */
public class TrackingBloodRenderer extends AbstractSummonRenderer<TrackingBlood> {

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        SphereRendererHelper.builder()
                .radius(0.05f)
                .layers(4)
                .sides(6)
                .color(0xFFFF2020)
                .alpha(0.9f * this.getAlpha())
                .innerRatio(0.5f)
                .render(poseStack, bufferSource);
    }
}

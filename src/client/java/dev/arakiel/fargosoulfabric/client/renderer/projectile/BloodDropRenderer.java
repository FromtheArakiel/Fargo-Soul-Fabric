package dev.arakiel.fargosoulfabric.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.render.AbstractSummonRenderer;
import dev.arakiel.fargosoulfabric.client.render.helper.SphereRendererHelper;
import dev.arakiel.fargosoulfabric.common.entity.BloodDrop;
import net.minecraft.client.renderer.MultiBufferSource;

/** The droplet a bleeding target leaves behind. */
public class BloodDropRenderer extends AbstractSummonRenderer<BloodDrop> {

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        SphereRendererHelper.builder()
                .radius(0.1f)
                .layers(4)
                .sides(6)
                .color(0xFFCC2020)
                .alpha(0.85f * this.getAlpha())
                .innerRatio(0.5f)
                .render(poseStack, bufferSource);
    }
}

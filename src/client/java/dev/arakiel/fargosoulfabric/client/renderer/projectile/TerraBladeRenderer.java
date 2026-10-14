package dev.arakiel.fargosoulfabric.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.client.render.AbstractAttachmentEntityRenderer;
import dev.arakiel.fargosoulfabric.client.render.ModelContext;
import dev.arakiel.fargosoulfabric.client.render.RenderContext;
import dev.arakiel.fargosoulfabric.client.render.RenderUtil;
import dev.arakiel.fargosoulfabric.client.render.trail.RibbonTrailContext;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import dev.arakiel.fargosoulfabric.common.entity.TerraBlade;
import dev.arakiel.fargosoulfabric.client.register.FargoSoulModelRegister;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;

/** The blade of sorrows, with the ribbon trail it leaves while it attacks. */
public class TerraBladeRenderer extends AbstractAttachmentEntityRenderer<TerraBlade> {

    @Override
    protected RenderContext<TerraBlade> createContext(TerraBlade blade, PathNode visualNode, float partialTick,
                                                     int packedLight) {
        RenderContext<TerraBlade> context = super.createContext(blade, visualNode, partialTick, packedLight);
        RibbonTrailContext<TerraBlade> trail = new RibbonTrailContext<>();
        trail.timer(blade.attacking ? blade.trailTimer : 0);
        trail.colorRGB(0xF8E687);
        trail.segmentsPerNode(4);
        trail.historyLength(6);
        trail.upOffset(1.015f);
        context.trail(trail);
        context.model(new ModelContext()
                .scale(1.5f)
                .translateOffset(-0.5f, -0.5f, -0.5f)
                .rotationOffset(0.0f, 90.0f, 45.0f));
        return context;
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderUtil.renderStandalone(FargoSoulModelRegister.TERRA_BLADE, poseStack, bufferSource,
                FastColor.ARGB32.color((int) (this.getAlpha() * 255), 255, 255, 255), RenderUtil.FULL_LIGHT);
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        DynamicLightDispatcher.addLightSources(this.context.visualNode.pos(), 8);
    }
}

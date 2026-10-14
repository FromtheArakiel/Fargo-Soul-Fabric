package dev.arakiel.fargosoulfabric.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.client.render.AbstractSummonRenderer;
import dev.arakiel.fargosoulfabric.client.render.ModelContext;
import dev.arakiel.fargosoulfabric.client.render.RenderContext;
import dev.arakiel.fargosoulfabric.client.render.RenderUtil;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.entity.ShadowOrb;
import dev.arakiel.fargosoulfabric.client.register.FSFModelRegister;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;

/** The magic orbs the ancient shadow enchantment summons. */
public class ShadowOrbRenderer extends AbstractSummonRenderer<ShadowOrb> {

    @Override
    protected RenderContext<ShadowOrb> createContext(ShadowOrb orb, PathNode visualNode, float partialTick,
                                                     int packedLight) {
        RenderContext<ShadowOrb> context = super.createContext(orb, visualNode, partialTick, packedLight);
        float rotation = orb.getTickCount() + partialTick;
        context.model(new ModelContext()
                .translateOffset(-0.5f, -0.5f, -0.5f)
                .rotationOffset(rotation, rotation, rotation)
                .scale(0.75f));
        return context;
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderUtil.renderStandalone(FSFModelRegister.SHADOW_ORB, poseStack, bufferSource,
                FastColor.ARGB32.color((int) (this.getAlpha() * 255), 255, 255, 255), RenderUtil.FULL_LIGHT);
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        DynamicLightDispatcher.addLightSources(this.context.visualNode.pos(), 12);
    }
}

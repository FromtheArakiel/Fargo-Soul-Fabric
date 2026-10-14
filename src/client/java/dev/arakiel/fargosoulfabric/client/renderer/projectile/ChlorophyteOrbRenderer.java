package dev.arakiel.fargosoulfabric.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.render.AbstractSummonRenderer;
import dev.arakiel.fargosoulfabric.client.render.ModelContext;
import dev.arakiel.fargosoulfabric.client.render.RenderContext;
import dev.arakiel.fargosoulfabric.client.render.RenderUtil;
import dev.arakiel.fargosoulfabric.client.render.rendererHelper.LaserRendererHelper;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.entity.ChlorophyteOrb;
import dev.arakiel.fargosoulfabric.client.register.FSFModelRegister;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** The chlorophyte crystals of the nature enchantment, which fire a laser at their target. */
public class ChlorophyteOrbRenderer extends AbstractSummonRenderer<ChlorophyteOrb> {

    @Override
    protected RenderContext<ChlorophyteOrb> createContext(ChlorophyteOrb orb, PathNode visualNode, float partialTick,
                                                         int packedLight) {
        RenderContext<ChlorophyteOrb> context = super.createContext(orb, visualNode, partialTick, packedLight);
        context.model(new ModelContext().translateOffset(-0.5f, -0.5f, -0.5f));
        return context;
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        RenderUtil.renderStandalone(FSFModelRegister.CHLOROPHYTE_CRYSTAL, poseStack, bufferSource,
                FastColor.ARGB32.color((int) (this.getAlpha() * 255), 255, 255, 255), RenderUtil.FULL_LIGHT);
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        ChlorophyteOrb orb = this.context.entity;
        if (!(orb.getOwner().level().getEntity(orb.getTargetId()) instanceof LivingEntity target)) {
            return;
        }
        PathNode visualNode = this.context.visualNode;
        float partialTick = this.context.partialTick;
        Vec3 from = visualNode.pos();
        Vec3 to = target.getBoundingBox().getCenter();
        float length = (float) from.distanceTo(target.getPosition(partialTick).add(0, target.getBbHeight() / 2, 0));
        Vec3 direction = to.subtract(from);
        if (direction.lengthSqr() < 1.0E-6) {
            return;
        }
        direction = direction.normalize();
        poseStack.pushPose();
        Quaternionf rotation = new Quaternionf().rotateTo(new Vector3f(0, 0, -1),
                new Vector3f((float) direction.x, (float) direction.y, (float) direction.z));
        poseStack.mulPose(rotation);
        LaserRendererHelper.builder()
                .length(length)
                .radius(0.2f, 0.2f)
                .layers(3)
                .segments(10)
                .color(0xFF3AFF7A)
                .alpha(0.25f)
                .innerRatio(0.4f)
                .render(poseStack, bufferSource);
        poseStack.popPose();
    }
}

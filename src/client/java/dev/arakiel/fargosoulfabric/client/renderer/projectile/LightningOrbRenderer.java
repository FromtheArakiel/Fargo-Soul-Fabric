package dev.arakiel.fargosoulfabric.client.renderer.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.client.render.AbstractSummonRenderer;
import dev.arakiel.fargosoulfabric.client.render.helper.LightningRendererHelper;
import dev.arakiel.fargosoulfabric.client.render.helper.SphereRendererHelper;
import dev.arakiel.fargosoulfabric.common.entity.LightningOrb;
import java.util.Set;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** The thunder orb of the terra enchantment, which arcs to the entities it hit. */
public class LightningOrbRenderer extends AbstractSummonRenderer<LightningOrb> {

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        SphereRendererHelper.builder()
                .radius(0.25f)
                .layers(3)
                .sides(6)
                .color(0x38ffec)
                .alpha(0.75f)
                .innerRatio(0.5f)
                .render(poseStack, bufferSource);
    }

    @Override
    protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
        LightningOrb orb = this.context.entity;
        Vec3 origin = this.context.visualNode.pos();
        Set<Integer> ids = orb.getIdList();
        Player owner = orb.getOwner();
        Level level = owner.level();
        for (Integer id : ids) {
            Entity entity = level.getEntity(id);
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            RandomSource random = living.getRandom();
            random.setSeed(orb.hashCode() + living.hashCode());
            AABB box = living.getBoundingBox();
            LightningRendererHelper.builder()
                    .from(origin)
                    .to(box.getCenter().offsetRandom(random, (float) box.getSize() * 0.5f))
                    .renderOrigin(origin)
                    .layers(1)
                    .segments(4)
                    .branches(0)
                    .jitter(0.1f)
                    .branchLength(0.0f)
                    .radius(0.02f, 0.02f)
                    .color(0x38ffec)
                    .alpha(0.5f * this.getAlpha())
                    .render(poseStack, bufferSource, random);
        }
        DynamicLightDispatcher.addLightSources(origin, 8);
    }
}

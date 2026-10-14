package dev.arakiel.fargosoulfabric.client.render.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import dev.arakiel.fargosoulfabric.client.render.FSFRenderTypes;
import dev.arakiel.fargosoulfabric.client.render.RenderContext;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class RibbonTrailContext<T extends Summon> extends TrailContext<T> {
   public float upOffset = 0.0F;
   public float downOffset = 0.0F;
   public RenderContext.AlphaBoostFunction<T> tipAlphaBoost = (entity, progress) -> (1.0F - progress) * 20.0F;
   public RenderContext.BrightnessBoostFunction<T> tipBrightnessBoost = (entity, progress) -> 1.0F - progress * 0.5F;

   public RibbonTrailContext() {
   }

   public RibbonTrailContext<T> upOffset(float upOffset) {
      this.upOffset = upOffset;
      return this;
   }

   public RibbonTrailContext<T> downOffset(float downOffset) {
      this.downOffset = downOffset;
      return this;
   }

   public RibbonTrailContext<T> tipAlphaBoost(RenderContext.AlphaBoostFunction<T> function) {
      this.tipAlphaBoost = function;
      return this;
   }

   public RibbonTrailContext<T> tipBrightnessBoost(RenderContext.BrightnessBoostFunction<T> function) {
      this.tipBrightnessBoost = function;
      return this;
   }

   @Override
   public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context) {
      List<TrailContext.InterpolatedNode> nodes = this.buildSmoothNodes(context.entity, context.visualNode, context.partialTick);
      int nodeCount = nodes.size();
      if (nodeCount > 1) {
         VertexConsumer buffer = bufferSource.getBuffer(FSFRenderTypes.getTrail());
         Pose pose = poseStack.last();
         Vec3 renderPos = context.visualNode.pos();
         Vector3f currTip = new Vector3f();
         Vector3f currBase = new Vector3f();
         Vector3f prevTip = new Vector3f();
         Vector3f prevBase = new Vector3f();

         for (int i = 0; i < nodeCount - 1; i++) {
            TrailContext.InterpolatedNode curr = nodes.get(i);
            TrailContext.InterpolatedNode prev = nodes.get(i + 1);
            float currProgress = (float)i / (float)(nodeCount - 1);
            float prevProgress = (float)(i + 1) / (float)(nodeCount - 1);
            currTip.set(0.0F, 0.0F, this.upOffset).rotate(curr.rot());
            currBase.set(0.0F, 0.0F, this.downOffset).rotate(curr.rot());
            prevTip.set(0.0F, 0.0F, this.upOffset).rotate(prev.rot());
            prevBase.set(0.0F, 0.0F, this.downOffset).rotate(prev.rot());
            int currColorRGB = this.colorFunction.getColor(context.entity, currProgress, context.partialTick);
            int prevColorRGB = this.colorFunction.getColor(context.entity, prevProgress, context.partialTick);
            float currBright = this.tipBrightnessBoost.getBoost(context.entity, currProgress);
            float prevBright = this.tipBrightnessBoost.getBoost(context.entity, prevProgress);
            float currAlphaBoost = this.tipAlphaBoost.getBoost(context.entity, currProgress);
            float prevAlphaBoost = this.tipAlphaBoost.getBoost(context.entity, prevProgress);
            int currTipColor = packColor(currColorRGB, Math.max(0.0F, 1.0F - currProgress) * 0.1F * currAlphaBoost, currBright);
            int currBaseColor = packColor(currColorRGB, Math.max(0.0F, 1.0F - currProgress * 2.5F) * 0.04F * currAlphaBoost, currBright);
            int prevTipColor = packColor(prevColorRGB, Math.max(0.0F, 1.0F - prevProgress) * 0.1F * prevAlphaBoost, prevBright);
            int prevBaseColor = packColor(prevColorRGB, Math.max(0.0F, 1.0F - prevProgress * 2.5F) * 0.04F * prevAlphaBoost, prevBright);
            float crx = (float)(curr.pos().x - renderPos.x);
            float cry = (float)(curr.pos().y - renderPos.y);
            float crz = (float)(curr.pos().z - renderPos.z);
            float prx = (float)(prev.pos().x - renderPos.x);
            float pry = (float)(prev.pos().y - renderPos.y);
            float prz = (float)(prev.pos().z - renderPos.z);
            buffer.addVertex(pose, crx + currTip.x, cry + currTip.y, crz + currTip.z)
               .setColor(currTipColor)
               .setUv(0.0F, 0.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(pose, crx + currBase.x, cry + currBase.y, crz + currBase.z)
               .setColor(currBaseColor)
               .setUv(1.0F, 0.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(pose, prx + prevBase.x, pry + prevBase.y, prz + prevBase.z)
               .setColor(prevBaseColor)
               .setUv(1.0F, 1.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(pose, prx + prevTip.x, pry + prevTip.y, prz + prevTip.z)
               .setColor(prevTipColor)
               .setUv(0.0F, 1.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
         }
      }
   }
}

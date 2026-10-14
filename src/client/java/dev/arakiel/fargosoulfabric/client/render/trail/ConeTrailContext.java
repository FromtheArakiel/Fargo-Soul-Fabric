package dev.arakiel.fargosoulfabric.client.render.trail;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import dev.arakiel.fargosoulfabric.client.render.FargoSoulRenderTypes;
import dev.arakiel.fargosoulfabric.client.render.RenderContext;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ConeTrailContext<T extends AttachmentEntity> extends TrailContext<T> {
   public float maxRadius = 0.2F;
   public float minRadiusRatio = 0.0F;
   public int resolution = 6;

   public ConeTrailContext() {
   }

   public ConeTrailContext<T> maxRadius(float radius) {
      this.maxRadius = radius;
      return this;
   }

   public ConeTrailContext<T> minRadiusRatio(float ratio) {
      this.minRadiusRatio = ratio;
      return this;
   }

   public ConeTrailContext<T> resolution(int resolution) {
      this.resolution = resolution;
      return this;
   }

   @Override
   public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context) {
      this.renderCone(poseStack, bufferSource, context, this.buildSmoothNodes(context.entity, context.visualNode, context.partialTick));
   }

   protected void renderCone(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context, List<TrailContext.InterpolatedNode> nodes) {
      int nodeCount = nodes.size();
      if (nodeCount > 1) {
         VertexConsumer buffer = bufferSource.getBuffer(FargoSoulRenderTypes.getTrail());
         Pose pose = poseStack.last();
         Vec3 renderPos = context.visualNode.pos();
         float[] cosArr = getCosArray(this.resolution);
         float[] sinArr = getSinArray(this.resolution);
         Vector3f currV1 = new Vector3f();
         Vector3f currV2 = new Vector3f();
         Vector3f prevV1 = new Vector3f();
         Vector3f prevV2 = new Vector3f();

         for (int i = 0; i < nodeCount - 1; i++) {
            TrailContext.InterpolatedNode curr = nodes.get(i);
            TrailContext.InterpolatedNode prev = nodes.get(i + 1);
            float currProgress = (float)i / (float)(nodeCount - 1);
            float prevProgress = (float)(i + 1) / (float)(nodeCount - 1);
            float currFade = this.fadeOut.getFade(currProgress);
            float prevFade = this.fadeOut.getFade(prevProgress);
            float currRadius = this.maxRadius * (this.minRadiusRatio + (1.0F - this.minRadiusRatio) * currFade);
            float prevRadius = this.maxRadius * (this.minRadiusRatio + (1.0F - this.minRadiusRatio) * prevFade);
            int currARGB = packColor(this.colorFunction.getColor(context.entity, currProgress, context.partialTick), currFade * 0.78431374F);
            int prevARGB = packColor(this.colorFunction.getColor(context.entity, prevProgress, context.partialTick), prevFade * 0.78431374F);
            float crx = (float)(curr.pos().x - renderPos.x);
            float cry = (float)(curr.pos().y - renderPos.y);
            float crz = (float)(curr.pos().z - renderPos.z);
            float prx = (float)(prev.pos().x - renderPos.x);
            float pry = (float)(prev.pos().y - renderPos.y);
            float prz = (float)(prev.pos().z - renderPos.z);

            for (int j = 0; j < this.resolution; j++) {
               float cos1 = cosArr[j];
               float sin1 = sinArr[j];
               float cos2 = cosArr[j + 1];
               float sin2 = sinArr[j + 1];
               currV1.set(cos1 * currRadius, sin1 * currRadius, 0.0F).rotate(curr.rot());
               currV2.set(cos2 * currRadius, sin2 * currRadius, 0.0F).rotate(curr.rot());
               prevV1.set(cos1 * prevRadius, sin1 * prevRadius, 0.0F).rotate(prev.rot());
               prevV2.set(cos2 * prevRadius, sin2 * prevRadius, 0.0F).rotate(prev.rot());
               buffer.addVertex(pose, crx + currV1.x, cry + currV1.y, crz + currV1.z)
                  .setColor(currARGB)
                  .setUv(0.0F, 0.0F)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(15728880)
                  .setNormal(0.0F, 0.0F, 1.0F);
               buffer.addVertex(pose, crx + currV2.x, cry + currV2.y, crz + currV2.z)
                  .setColor(currARGB)
                  .setUv(1.0F, 0.0F)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(15728880)
                  .setNormal(0.0F, 0.0F, 1.0F);
               buffer.addVertex(pose, prx + prevV2.x, pry + prevV2.y, prz + prevV2.z)
                  .setColor(prevARGB)
                  .setUv(1.0F, 1.0F)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(15728880)
                  .setNormal(0.0F, 0.0F, 1.0F);
               buffer.addVertex(pose, prx + prevV1.x, pry + prevV1.y, prz + prevV1.z)
                  .setColor(prevARGB)
                  .setUv(0.0F, 1.0F)
                  .setOverlay(OverlayTexture.NO_OVERLAY)
                  .setLight(15728880)
                  .setNormal(0.0F, 0.0F, 1.0F);
            }
         }
      }
   }
}

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

public class DropletTrailContext<T extends AttachmentEntity> extends ConeTrailContext<T> {
   public DropletTrailContext() {
   }

   @Override
   public void render(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context) {
      List<TrailContext.InterpolatedNode> nodes = this.buildSmoothNodes(context.entity, context.visualNode, context.partialTick);
      int nodeCount = nodes.size();
      if (nodeCount > 1) {
         this.renderCone(poseStack, bufferSource, context, nodes);
         this.renderHead(poseStack, bufferSource, context, nodes.getFirst());
      }
   }

   private void renderHead(PoseStack poseStack, MultiBufferSource bufferSource, RenderContext<T> context, TrailContext.InterpolatedNode headNode) {
      VertexConsumer buffer = bufferSource.getBuffer(FargoSoulRenderTypes.getTrail());
      Pose pose = poseStack.last();
      float[] cosArr = getCosArray(this.resolution);
      float[] sinArr = getSinArray(this.resolution);
      float headFade = this.fadeOut.getFade(0.0F);
      float headRadius = this.maxRadius * (this.minRadiusRatio + (1.0F - this.minRadiusRatio) * headFade);
      int headARGB = packColor(this.colorFunction.getColor(context.entity, 0.0F, context.partialTick), headFade * 0.78431374F);
      Vec3 renderPos = context.visualNode.pos();
      float hrx = (float)(headNode.pos().x - renderPos.x);
      float hry = (float)(headNode.pos().y - renderPos.y);
      float hrz = (float)(headNode.pos().z - renderPos.z);
      int hemisphereSegments = Math.max(2, this.resolution / 2);
      Vector3f v1 = new Vector3f();
      Vector3f v2 = new Vector3f();
      Vector3f v3 = new Vector3f();
      Vector3f v4 = new Vector3f();

      for (int lat = 0; lat < hemisphereSegments; lat++) {
         float latAngle1 = (float)((Math.PI / 2) * (double)lat / (double)hemisphereSegments);
         float latAngle2 = (float)((Math.PI / 2) * (double)(lat + 1) / (double)hemisphereSegments);
         float r1 = (float)Math.cos((double)latAngle1) * headRadius;
         float r2 = (float)Math.cos((double)latAngle2) * headRadius;
         float h1 = (float)Math.sin((double)latAngle1) * headRadius;
         float h2 = (float)Math.sin((double)latAngle2) * headRadius;

         for (int lon = 0; lon < this.resolution; lon++) {
            float cos1 = cosArr[lon];
            float sin1 = sinArr[lon];
            float cos2 = cosArr[lon + 1];
            float sin2 = sinArr[lon + 1];
            v1.set(cos1 * r1, sin1 * r1, h1).rotate(headNode.rot());
            v2.set(cos2 * r1, sin2 * r1, h1).rotate(headNode.rot());
            v3.set(cos2 * r2, sin2 * r2, h2).rotate(headNode.rot());
            v4.set(cos1 * r2, sin1 * r2, h2).rotate(headNode.rot());
            buffer.addVertex(pose, hrx + v1.x, hry + v1.y, hrz + v1.z)
               .setColor(headARGB)
               .setUv(0.0F, 0.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(pose, hrx + v2.x, hry + v2.y, hrz + v2.z)
               .setColor(headARGB)
               .setUv(1.0F, 0.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(pose, hrx + v3.x, hry + v3.y, hrz + v3.z)
               .setColor(headARGB)
               .setUv(1.0F, 1.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
            buffer.addVertex(pose, hrx + v4.x, hry + v4.y, hrz + v4.z)
               .setColor(headARGB)
               .setUv(0.0F, 1.0F)
               .setOverlay(OverlayTexture.NO_OVERLAY)
               .setLight(15728880)
               .setNormal(0.0F, 0.0F, 1.0F);
         }
      }
   }
}

package dev.arakiel.fargosoulfabric.client.render.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.arakiel.fargosoulfabric.client.render.FSFRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Matrix4f;

public class LaserRendererHelper {
   private float length = 1.0F;
   private float radiusStart = 0.1F;
   private float radiusEnd = 0.1F;
   private int layers = 4;
   private int segments = 12;
   private int colorRGB = -1;
   private float alpha = 0.8F;
   private float innerRatio = 0.3F;

   private LaserRendererHelper() {
   }

   public static LaserRendererHelper builder() {
      return new LaserRendererHelper();
   }

   public LaserRendererHelper length(float length) {
      this.length = length;
      return this;
   }

   public LaserRendererHelper radius(float start, float end) {
      this.radiusStart = start;
      this.radiusEnd = end;
      return this;
   }

   public LaserRendererHelper layers(int layers) {
      this.layers = Math.max(1, layers);
      return this;
   }

   public LaserRendererHelper segments(int segments) {
      this.segments = Math.max(3, segments);
      return this;
   }

   public LaserRendererHelper color(int argb) {
      this.colorRGB = argb;
      return this;
   }

   public LaserRendererHelper alpha(float alpha) {
      this.alpha = alpha;
      return this;
   }

   public LaserRendererHelper innerRatio(float ratio) {
      this.innerRatio = Math.max(0.0F, Math.min(1.0F, ratio));
      return this;
   }

   public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
      VertexConsumer consumer = bufferSource.getBuffer(FSFRenderTypes.getTrail());
      Matrix4f pose = poseStack.last().pose();
      int baseR = ARGB32.red(this.colorRGB);
      int baseG = ARGB32.green(this.colorRGB);
      int baseB = ARGB32.blue(this.colorRGB);
      int baseA = Math.clamp((long)Math.round(this.alpha * 255.0F), 0, 255);

      for (int layer = 0; layer < this.layers; layer++) {
         float layerRatio = this.layers == 1 ? 0.0F : (float)layer / (float)(this.layers - 1);
         float radiusScale = mix(this.innerRatio, 1.0F, layerRatio);
         float layerAlpha = mix(1.0F, 0.15F, layerRatio);
         this.renderLayer(consumer, pose, radiusScale, layerAlpha, baseR, baseG, baseB, baseA);
      }
   }

   private void renderLayer(VertexConsumer consumer, Matrix4f pose, float radiusScale, float layerAlpha, int baseR, int baseG, int baseB, int baseA) {
      float rNear = this.radiusStart * radiusScale;
      float rFar = this.radiusEnd * radiusScale;
      int a = Math.max(0, Math.min(255, Math.round((float)baseA * layerAlpha)));
      int vertexColor = ARGB32.color(a, baseR, baseG, baseB);

      for (int j = 0; j < this.segments; j++) {
         float angle1 = (float)j / (float)this.segments * (float) (Math.PI * 2);
         float angle2 = (float)(j + 1) / (float)this.segments * (float) (Math.PI * 2);
         float cos1 = (float)Math.cos((double)angle1);
         float sin1 = (float)Math.sin((double)angle1);
         float cos2 = (float)Math.cos((double)angle2);
         float sin2 = (float)Math.sin((double)angle2);
         float u = ((float)j + 0.5F) / (float)this.segments;
         this.emitVertex(consumer, pose, cos1 * rNear, sin1 * rNear, 0.0F, vertexColor, u, 0.0F);
         this.emitVertex(consumer, pose, cos2 * rNear, sin2 * rNear, 0.0F, vertexColor, u, 0.0F);
         this.emitVertex(consumer, pose, cos2 * rFar, sin2 * rFar, -this.length, vertexColor, u, 1.0F);
         this.emitVertex(consumer, pose, cos1 * rFar, sin1 * rFar, -this.length, vertexColor, u, 1.0F);
      }
   }

   private void emitVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int color, float u, float v) {
      consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 0.0F, 1.0F);
   }

   private static float mix(float a, float b, float t) {
      return a + (b - a) * t;
   }
}

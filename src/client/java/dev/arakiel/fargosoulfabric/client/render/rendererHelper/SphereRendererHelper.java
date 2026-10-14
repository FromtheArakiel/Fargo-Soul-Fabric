package dev.arakiel.fargosoulfabric.client.render.rendererHelper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.arakiel.fargosoulfabric.client.render.FargoSoulRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.FastColor.ARGB32;
import org.joml.Matrix4f;

public class SphereRendererHelper {
   private float radius = 0.3F;
   private int layers = 4;
   private int sides = 12;
   private int colorRGB = -1;
   private float alpha = 0.9F;
   private float innerRatio = 0.4F;

   private SphereRendererHelper() {
   }

   public static SphereRendererHelper builder() {
      return new SphereRendererHelper();
   }

   public SphereRendererHelper radius(float radius) {
      this.radius = radius;
      return this;
   }

   public SphereRendererHelper layers(int layers) {
      this.layers = Math.max(1, layers);
      return this;
   }

   public SphereRendererHelper sides(int sides) {
      this.sides = Math.max(3, sides);
      return this;
   }

   public SphereRendererHelper color(int argb) {
      this.colorRGB = argb;
      return this;
   }

   public SphereRendererHelper alpha(float alpha) {
      this.alpha = alpha;
      return this;
   }

   public SphereRendererHelper innerRatio(float ratio) {
      this.innerRatio = Math.clamp(ratio, 0.0F, 1.0F);
      return this;
   }

   public void render(PoseStack poseStack, MultiBufferSource bufferSource) {
      VertexConsumer consumer = bufferSource.getBuffer(FargoSoulRenderTypes.getTrail());
      Matrix4f pose = poseStack.last().pose();
      int baseR = ARGB32.red(this.colorRGB);
      int baseG = ARGB32.green(this.colorRGB);
      int baseB = ARGB32.blue(this.colorRGB);
      int baseA = Math.clamp((long)Math.round(this.alpha * 255.0F), 0, 255);

      for (int layer = 0; layer < this.layers; layer++) {
         float layerRatio = this.layers == 1 ? 0.0F : (float)layer / (float)(this.layers - 1);
         float radiusScale = mix(this.innerRatio, 1.0F, layerRatio);
         float layerAlpha = mix(1.0F, 0.15F, layerRatio);
         float r = this.radius * radiusScale;
         this.renderLayer(consumer, pose, r, layerAlpha, baseR, baseG, baseB, baseA, this.sides);
      }
   }

   private void renderLayer(VertexConsumer consumer, Matrix4f pose, float r, float layerAlpha, int baseR, int baseG, int baseB, int baseA, int sides) {
      int a = Math.clamp((long)Math.round((float)baseA * layerAlpha), 0, 255);
      int vertexColor = ARGB32.color(a, baseR, baseG, baseB);
      int stacks = Math.max(2, sides / 2);

      for (int ring = 0; ring < stacks; ring++) {
         float phi0 = (float)ring / (float)stacks * (float) Math.PI;
         float phi1 = (float)(ring + 1) / (float)stacks * (float) Math.PI;
         float y0 = (float)Math.cos((double)phi0);
         float y1 = (float)Math.cos((double)phi1);
         float xz0 = (float)Math.sin((double)phi0);
         float xz1 = (float)Math.sin((double)phi1);

         for (int seg = 0; seg < sides; seg++) {
            float theta0 = (float)seg / (float)sides * (float) (Math.PI * 2);
            float theta1 = (float)(seg + 1) / (float)sides * (float) (Math.PI * 2);
            float ct0 = (float)Math.cos((double)theta0);
            float st0 = (float)Math.sin((double)theta0);
            float ct1 = (float)Math.cos((double)theta1);
            float st1 = (float)Math.sin((double)theta1);
            float u0 = (float)seg / (float)sides;
            float u1 = (float)(seg + 1) / (float)sides;
            float v0 = (float)ring / (float)stacks;
            float v1 = (float)(ring + 1) / (float)stacks;
            float nx0y0 = xz0 * ct0;
            float nz0y0 = xz0 * st0;
            float nx1y0 = xz0 * ct1;
            float nz1y0 = xz0 * st1;
            float nx1y1 = xz1 * ct1;
            float nz1y1 = xz1 * st1;
            float nx0y1 = xz1 * ct0;
            float nz0y1 = xz1 * st0;
            this.emitVertex(consumer, pose, nx0y0 * r, y0 * r, nz0y0 * r, vertexColor, u0, v0, nx0y0, y0, nz0y0);
            this.emitVertex(consumer, pose, nx1y0 * r, y0 * r, nz1y0 * r, vertexColor, u1, v0, nx1y0, y0, nz1y0);
            this.emitVertex(consumer, pose, nx1y1 * r, y1 * r, nz1y1 * r, vertexColor, u1, v1, nx1y1, y1, nz1y1);
            this.emitVertex(consumer, pose, nx0y1 * r, y1 * r, nz0y1 * r, vertexColor, u0, v1, nx0y1, y1, nz0y1);
         }
      }
   }

   private void emitVertex(VertexConsumer consumer, Matrix4f pose, float x, float y, float z, int color, float u, float v, float nx, float ny, float nz) {
      consumer.addVertex(pose, x, y, z).setColor(color).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(nx, ny, nz);
   }

   private static float mix(float a, float b, float t) {
      return a + (b - a) * t;
   }
}

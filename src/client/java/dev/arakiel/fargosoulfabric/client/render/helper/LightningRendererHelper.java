package dev.arakiel.fargosoulfabric.client.render.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.arakiel.fargosoulfabric.client.render.FSFRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LightningRendererHelper {
   private Vec3 start = Vec3.ZERO;
   private Vec3 end = Vec3.ZERO;
   private Vec3 renderOrigin = Vec3.ZERO;
   private int layers = 4;
   private int segments = 16;
   private int branches = 0;
   private float jitter = 0.3F;
   private float branchLength = 0.4F;
   private float radiusCore = 0.05F;
   private float radiusOuter = 0.2F;
   private int colorRGB = -1;
   private float alpha = 0.9F;
   private float innerRatio = 0.25F;

   private LightningRendererHelper() {
   }

   public static LightningRendererHelper builder() {
      return new LightningRendererHelper();
   }

   public LightningRendererHelper from(Vec3 start) {
      this.start = start;
      return this;
   }

   public LightningRendererHelper to(Vec3 end) {
      this.end = end;
      return this;
   }

   public LightningRendererHelper renderOrigin(Vec3 renderOrigin) {
      this.renderOrigin = renderOrigin;
      return this;
   }

   public LightningRendererHelper layers(int layers) {
      this.layers = Math.max(1, layers);
      return this;
   }

   public LightningRendererHelper segments(int segments) {
      this.segments = Math.max(2, segments);
      return this;
   }

   public LightningRendererHelper branches(int branches) {
      this.branches = Math.max(0, branches);
      return this;
   }

   public LightningRendererHelper jitter(float jitter) {
      this.jitter = jitter;
      return this;
   }

   public LightningRendererHelper branchLength(float branchLength) {
      this.branchLength = branchLength;
      return this;
   }

   public LightningRendererHelper radius(float core, float outer) {
      this.radiusCore = core;
      this.radiusOuter = outer;
      return this;
   }

   public LightningRendererHelper color(int argb) {
      this.colorRGB = argb;
      return this;
   }

   public LightningRendererHelper alpha(float alpha) {
      this.alpha = alpha;
      return this;
   }

   public LightningRendererHelper innerRatio(float ratio) {
      this.innerRatio = Math.max(0.0F, Math.min(1.0F, ratio));
      return this;
   }

   public void render(PoseStack poseStack, MultiBufferSource bufferSource, RandomSource random) {
      VertexConsumer consumer = bufferSource.getBuffer(FSFRenderTypes.getTrail());
      Matrix4f pose = poseStack.last().pose();
      Vector3f sLocal = worldToLocal(this.start, this.renderOrigin);
      Vector3f eLocal = worldToLocal(this.end, this.renderOrigin);
      Vector3f dir = new Vector3f(eLocal).sub(sLocal);
      float length = dir.length();
      if (!(length < 1.0E-4F)) {
         dir.div(length);
         int baseR = ARGB32.red(this.colorRGB);
         int baseG = ARGB32.green(this.colorRGB);
         int baseB = ARGB32.blue(this.colorRGB);
         int baseA = Math.max(0, Math.min(255, Math.round(this.alpha * 255.0F)));
         Vector3f perpA = this.perpendicular(dir, random);
         Vector3f perpB = new Vector3f();
         dir.cross(perpA, perpB);
         if (perpB.lengthSquared() < 1.0E-8F) {
            perpB.set(0.0F, 1.0F, 0.0F);
         } else {
            perpB.normalize();
         }

         Vector3f[] points = this.sampleBoltPoints(sLocal, dir, length, perpA, perpB, random);

         for (int layer = 0; layer < this.layers; layer++) {
            float layerRatio = this.layers == 1 ? 0.0F : (float)layer / (float)(this.layers - 1);
            float radius = mix(this.radiusCore, this.radiusOuter, mix(this.innerRatio, 1.0F, layerRatio));
            float layerAlpha = mix(1.0F, 0.15F, layerRatio);
            int a = Math.max(0, Math.min(255, Math.round((float)baseA * layerAlpha)));
            int vertexColor = ARGB32.color(a, baseR, baseG, baseB);
            this.renderLayer(consumer, pose, points, perpA, perpB, radius, vertexColor);
         }

         for (int b = 0; b < this.branches; b++) {
            int idx = 1 + random.nextInt(points.length - 2);
            Vector3f branchStart = points[idx];
            Vector3f segDir = new Vector3f(points[idx + 1]).sub(points[idx - 1]);
            if (segDir.lengthSquared() < 1.0E-8F) {
               segDir.set(dir);
            } else {
               segDir.normalize();
            }

            Vector3f bpa = this.perpendicular(segDir, random);
            Vector3f bpb = new Vector3f();
            segDir.cross(bpa, bpb);
            if (bpb.lengthSquared() < 1.0E-8F) {
               bpb.set(0.0F, 1.0F, 0.0F);
            } else {
               bpb.normalize();
            }

            float along = (random.nextFloat() * 2.0F - 1.0F) * 0.3F;
            float orthoA = random.nextFloat() * 2.0F - 1.0F;
            float orthoB = random.nextFloat() * 2.0F - 1.0F;
            Vector3f branchDir = new Vector3f(segDir).mul(along).add(new Vector3f(bpa).mul(orthoA)).add(new Vector3f(bpb).mul(orthoB));
            if (!(branchDir.lengthSquared() < 1.0E-6F)) {
               branchDir.normalize();
               float branchLen = length * this.branchLength;
               Vector3f[] bPoints = this.sampleBoltPoints(branchStart, branchDir, branchLen, bpa, bpb, random);

               for (int layer = 0; layer < this.layers; layer++) {
                  float layerRatio = this.layers == 1 ? 0.0F : (float)layer / (float)(this.layers - 1);
                  float radius = mix(this.radiusCore, this.radiusOuter, mix(this.innerRatio, 1.0F, layerRatio)) * 0.6F;
                  float layerAlpha = mix(1.0F, 0.15F, layerRatio);
                  int a = Math.max(0, Math.min(255, Math.round((float)baseA * 0.7F * layerAlpha)));
                  int vertexColor = ARGB32.color(a, baseR, baseG, baseB);
                  this.renderLayer(consumer, pose, bPoints, bpa, bpb, radius, vertexColor);
               }
            }
         }
      }
   }

   private Vector3f[] sampleBoltPoints(Vector3f origin, Vector3f dir, float length, Vector3f perpA, Vector3f perpB, RandomSource random) {
      Vector3f[] points = new Vector3f[this.segments + 1];
      float jitterAmt = length * this.jitter;

      for (int i = 0; i <= this.segments; i++) {
         float t = (float)i / (float)this.segments;
         float axial = t * length;
         float envelope = 1.0F - Math.abs(2.0F * t - 1.0F);
         float offA = (random.nextFloat() * 2.0F - 1.0F) * jitterAmt * envelope;
         float offB = (random.nextFloat() * 2.0F - 1.0F) * jitterAmt * envelope;
         points[i] = new Vector3f(origin).add(new Vector3f(dir).mul(axial)).add(new Vector3f(perpA).mul(offA)).add(new Vector3f(perpB).mul(offB));
      }

      return points;
   }

   private void renderLayer(VertexConsumer consumer, Matrix4f pose, Vector3f[] points, Vector3f perpA, Vector3f perpB, float radius, int vertexColor) {
      int radial = 6;
      float[][] ring = new float[radial][2];

      for (int k = 0; k < radial; k++) {
         float ang = (float)k / (float)radial * (float) (Math.PI * 2);
         ring[k][0] = (float)Math.cos((double)ang);
         ring[k][1] = (float)Math.sin((double)ang);
      }

      for (int i = 0; i < this.segments; i++) {
         Vector3f p0 = points[i];
         Vector3f p1 = points[i + 1];
         Vector3f segDir = new Vector3f(p1).sub(p0);
         float segLen = segDir.length();
         if (!(segLen < 1.0E-6F)) {
            segDir.div(segLen);

            for (int k = 0; k < radial; k++) {
               int k2 = (k + 1) % radial;
               float ca1 = ring[k][0];
               float sa1 = ring[k][1];
               float ca2 = ring[k2][0];
               float sa2 = ring[k2][1];
               float u = ((float)k + 0.5F) / (float)radial;
               float v0 = (float)i / (float)this.segments;
               float v1 = (float)(i + 1) / (float)this.segments;
               Vector3f v00 = this.ringOffset(p0, perpA, perpB, ca1, sa1, radius);
               Vector3f v01 = this.ringOffset(p0, perpA, perpB, ca2, sa2, radius);
               Vector3f v11 = this.ringOffset(p1, perpA, perpB, ca2, sa2, radius);
               Vector3f v10 = this.ringOffset(p1, perpA, perpB, ca1, sa1, radius);
               this.emitVertex(consumer, pose, v00, vertexColor, u, v0, segDir);
               this.emitVertex(consumer, pose, v01, vertexColor, u, v0, segDir);
               this.emitVertex(consumer, pose, v11, vertexColor, u, v1, segDir);
               this.emitVertex(consumer, pose, v10, vertexColor, u, v1, segDir);
            }
         }
      }
   }

   private Vector3f perpendicular(Vector3f dir, RandomSource random) {
      Vector3f ref = Math.abs(dir.x) < 0.9F ? new Vector3f(1.0F, 0.0F, 0.0F) : new Vector3f(0.0F, 1.0F, 0.0F);
      Vector3f perp = new Vector3f();
      dir.cross(ref, perp);
      if (perp.lengthSquared() < 1.0E-8F) {
         perp.set(0.0F, 0.0F, 1.0F);
      } else {
         perp.normalize();
      }

      if (random.nextBoolean()) {
         perp.negate();
      }

      return perp;
   }

   private Vector3f ringOffset(Vector3f center, Vector3f perpA, Vector3f perpB, float ca, float sa, float radius) {
      return new Vector3f(center).add(new Vector3f(perpA).mul(ca * radius)).add(new Vector3f(perpB).mul(sa * radius));
   }

   private void emitVertex(VertexConsumer consumer, Matrix4f pose, Vector3f v, int color, float u, float vCoord, Vector3f normal) {
      consumer.addVertex(pose, v.x, v.y, v.z)
         .setColor(color)
         .setUv(u, vCoord)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(normal.x, normal.y, normal.z);
   }

   private static Vector3f worldToLocal(Vec3 world, Vec3 origin) {
      return new Vector3f((float)(world.x - origin.x), (float)(world.y - origin.y), (float)(world.z - origin.z));
   }

   private static float mix(float a, float b, float t) {
      return a + (b - a) * t;
   }
}

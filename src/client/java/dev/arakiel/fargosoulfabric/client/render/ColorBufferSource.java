package dev.arakiel.fargosoulfabric.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FastColor.ARGB32;
import org.jetbrains.annotations.NotNull;

public class ColorBufferSource implements MultiBufferSource {
   private final MultiBufferSource inner;
   private int colorARGB = -1;
   private float alpha = 1.0F;

   public ColorBufferSource(MultiBufferSource inner) {
      this.inner = inner;
   }

   public ColorBufferSource setColor(int argb) {
      this.colorARGB = argb;
      return this;
   }

   public ColorBufferSource setAlpha(float alpha) {
      this.alpha = alpha;
      return this;
   }

   @NotNull
   public VertexConsumer getBuffer(@NotNull RenderType renderType) {
      VertexConsumer consumer = this.inner.getBuffer(renderType);
      return (this.colorARGB == -1 && this.alpha >= 1.0F
         ? consumer
         : new ColorBufferSource.ColorVertexConsumer(consumer, this.colorARGB, this.alpha));
   }

   private static record ColorVertexConsumer(VertexConsumer inner, int colorARGB, float alpha) implements VertexConsumer {
      private ColorVertexConsumer(VertexConsumer inner, int colorARGB, float alpha) {
         this.inner = inner;
         this.colorARGB = colorARGB;
         this.alpha = alpha;
      }

      @NotNull
      public VertexConsumer addVertex(float x, float y, float z) {
         this.inner.addVertex(x, y, z);
         return this;
      }

      @NotNull
      public VertexConsumer setColor(int r, int g, int b, int a) {
         if (this.colorARGB != -1) {
            r = ARGB32.red(this.colorARGB);
            g = ARGB32.green(this.colorARGB);
            b = ARGB32.blue(this.colorARGB);
            a = ARGB32.alpha(this.colorARGB);
         }

         if (this.alpha < 1.0F) {
            a = (int)((float)a * this.alpha);
         }

         return this.inner.setColor(r, g, b, a);
      }

      @NotNull
      public VertexConsumer setUv(float u, float v) {
         this.inner.setUv(u, v);
         return this;
      }

      @NotNull
      public VertexConsumer setUv1(int u, int v) {
         this.inner.setUv1(u, v);
         return this;
      }

      @NotNull
      public VertexConsumer setUv2(int u, int v) {
         this.inner.setUv2(u, v);
         return this;
      }

      @NotNull
      public VertexConsumer setNormal(float x, float y, float z) {
         this.inner.setNormal(x, y, z);
         return this;
      }
   }
}

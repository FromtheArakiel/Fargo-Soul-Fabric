package dev.arakiel.fargosoulfabric.utils;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface EasingCurve {
   EasingCurve LINEAR = t -> t;
   EasingCurve EASE_IN_QUAD = t -> t * t;
   EasingCurve EASE_OUT_QUAD = t -> t * (2.0F - t);
   EasingCurve EASE_IN_OUT_QUAD = t -> t < 0.5F ? 2.0F * t * t : -1.0F + (4.0F - 2.0F * t) * t;
   EasingCurve EASE_IN_CUBIC = t -> t * t * t;
   EasingCurve EASE_OUT_CUBIC = t -> {
      float t1 = t - 1.0F;
      return t1 * t1 * t1 + 1.0F;
   };
   EasingCurve EASE_IN_OUT_CUBIC = t -> t < 0.5F ? 4.0F * t * t * t : (t - 1.0F) * (2.0F * t - 2.0F) * (2.0F * t - 2.0F) + 1.0F;
   EasingCurve EASE_IN_BACK = t -> {
      float s = 1.70158F;
      return t * t * ((s + 1.0F) * t - s);
   };
   EasingCurve EASE_OUT_BACK = t -> {
      float s = 1.70158F;
      float t1 = t - 1.0F;
      return t1 * t1 * ((s + 1.0F) * t1 + s) + 1.0F;
   };
   EasingCurve EASE_IN_OUT_BACK = t -> {
      float s = 2.5949094F;
      if (t < 0.5F) {
         float t2 = 2.0F * t;
         return 0.5F * t2 * t2 * ((s + 1.0F) * t2 - s);
      } else {
         float t2 = 2.0F * t - 2.0F;
         return 0.5F * (t2 * t2 * ((s + 1.0F) * t2 + s) + 2.0F);
      }
   };
   EasingCurve EASE_OUT_BOUNCE = t -> {
      float n = 7.5625F;
      float d = 2.75F;
      if (t < 1.0F / d) {
         return n * t * t;
      } else if (t < 2.0F / d) {
         float t1 = t - 1.5F / d;
         return n * t1 * t1 + 0.75F;
      } else if ((double)t < 2.5 / (double)d) {
         float t1 = t - 2.25F / d;
         return n * t1 * t1 + 0.9375F;
      } else {
         float t1 = t - 2.625F / d;
         return n * t1 * t1 + 0.984375F;
      }
   };
   EasingCurve EASE_OUT_ELASTIC = t -> t != 0.0F && t != 1.0F
         ? (float)Math.pow(2.0, (double)(-10.0F * t)) * (float)Math.sin((double)(t - 0.075F) * (Math.PI * 2) / 0.3F) + 1.0F
         : t;

   float apply(float var1);

   default EasingCurve compose(EasingCurve outer) {
      return t -> outer.apply(this.apply(t));
   }

   default EasingCurve andThen(EasingCurve after) {
      return t -> this.apply(after.apply(t));
   }

   static EasingCurve.BezierBuilder bezier() {
      return new EasingCurve.BezierBuilder();
   }

   public static final class BezierBuilder {
      private static final int TABLE_SIZE = 256;
      private final List<Float> controlY = new ArrayList<>();
      private boolean uniformX = true;
      private final List<Float> explicitX = new ArrayList<>();

      BezierBuilder() {
      }

      public EasingCurve.BezierBuilder control(float y) {
         this.uniformX = true;
         this.controlY.add(y);
         return this;
      }

      public EasingCurve.BezierBuilder control(float x, float y) {
         this.uniformX = false;
         if (!this.explicitX.isEmpty() && x <= this.explicitX.getLast()) {
            throw new IllegalArgumentException("控制点 X 必须单调递增：当前 x=" + x + "，前一个 x=" + this.explicitX.getLast());
         } else {
            this.explicitX.add(x);
            this.controlY.add(y);
            return this;
         }
      }

      public EasingCurve build() {
         if (this.controlY.isEmpty()) {
            return EasingCurve.LINEAR;
         } else {
            int n = this.controlY.size() + 2;
            float[] px = new float[n];
            float[] py = new float[n];
            px[0] = 0.0F;
            py[0] = 0.0F;
            if (this.uniformX) {
               for (int i = 0; i < this.controlY.size(); i++) {
                  px[i + 1] = (float)(i + 1) / (float)(this.controlY.size() + 1);
                  py[i + 1] = this.controlY.get(i);
               }
            } else {
               for (int i = 0; i < this.controlY.size(); i++) {
                  px[i + 1] = this.explicitX.get(i);
                  py[i + 1] = this.controlY.get(i);
               }
            }

            px[n - 1] = 1.0F;
            py[n - 1] = 1.0F;
            float[] tableX = new float[257];
            float[] tableY = new float[257];

            for (int i = 0; i <= 256; i++) {
               float t = (float)i / 256.0F;
               float[] point = deCasteljau(px, py, t);
               tableX[i] = point[0];
               tableY[i] = point[1];
            }

            return progress -> {
               if (progress <= 0.0F) {
                  return 0.0F;
               } else {
                  return progress >= 1.0F ? 1.0F : lookupY(tableX, tableY, progress);
               }
            };
         }
      }

      private static float[] deCasteljau(float[] px, float[] py, float t) {
         int n = px.length;
         float[] x = px.clone();
         float[] y = py.clone();
         float invT = 1.0F - t;

         for (int k = n - 1; k > 0; k--) {
            for (int i = 0; i < k; i++) {
               x[i] = x[i] * invT + x[i + 1] * t;
               y[i] = y[i] * invT + y[i + 1] * t;
            }
         }

         return new float[]{x[0], y[0]};
      }

      private static float lookupY(float[] tableX, float[] tableY, float x) {
         for (int i = 1; i < tableX.length; i++) {
            if (tableX[i] >= x) {
               float x0 = tableX[i - 1];
               float x1 = tableX[i];
               float y0 = tableY[i - 1];
               float y1 = tableY[i];
               float denom = x1 - x0;
               if (denom < 1.0E-8F) {
                  return (y0 + y1) * 0.5F;
               }

               float t = (x - x0) / denom;
               return y0 + t * (y1 - y0);
            }
         }

         return tableY[tableY.length - 1];
      }
   }
}

package dev.arakiel.fargosoulfabric.client.render;

import dev.arakiel.fargosoulfabric.client.render.trail.TrailContext;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import software.bernie.geckolib.util.Color;

public class RenderContext<T extends Summon> {
   public final T entity;
   public final PathNode visualNode;
   public final float partialTick;
   public int packedLight;
   public Color color = Color.WHITE;
   public TrailContext<T> trail;
   public ModelContext model = new ModelContext();

   public RenderContext(T entity, PathNode visualNode, float partialTick, int packedLight) {
      this.entity = entity;
      this.visualNode = visualNode;
      this.partialTick = partialTick;
      this.packedLight = packedLight;
   }

   public RenderContext<T> packedLight(int packedLight) {
      this.packedLight = packedLight;
      return this;
   }

   public RenderContext<T> color(Color color) {
      this.color = color;
      return this;
   }

   public RenderContext<T> trail(TrailContext<T> trail) {
      this.trail = trail;
      return this;
   }

   public RenderContext<T> model(ModelContext model) {
      this.model = model;
      return this;
   }

   public boolean hasTrail() {
      return this.trail != null && this.trail.timer > 0;
   }

   @FunctionalInterface
   public interface AlphaBoostFunction<T extends Summon> {
      float getBoost(T var1, float var2);
   }

   @FunctionalInterface
   public interface BrightnessBoostFunction<T extends Summon> {
      float getBoost(T var1, float var2);
   }

   @FunctionalInterface
   public interface ColorFunction<T extends Summon> {
      int getColor(T var1, float var2, float var3);
   }

   @FunctionalInterface
   public interface FadeFunction {
      float getFade(float var1);
   }
}

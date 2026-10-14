package dev.arakiel.fargosoulfabric.client.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.util.Color;

public class SummonGeoRenderer<T extends Summon> extends GeoObjectRenderer<T> {
   private final AbstractSummonGeoRenderer<T> abstractSummonGeoRenderer;

   public SummonGeoRenderer(AbstractSummonGeoRenderer<T> abstractSummonGeoRenderer, GeoModel<T> model) {
      super(model);
      this.abstractSummonGeoRenderer = abstractSummonGeoRenderer;
   }

   public Color getRenderColor(T animatable, float partialTick, int packedLight) {
      return this.abstractSummonGeoRenderer.getRenderColor(animatable, partialTick, packedLight);
   }

   public void preRender(
      PoseStack poseStack,
      T animatable,
      BakedGeoModel model,
      @Nullable MultiBufferSource bufferSource,
      @Nullable VertexConsumer buffer,
      boolean isReRender,
      float partialTick,
      int packedLight,
      int packedOverlay,
      int colour
   ) {
      this.objectRenderTranslations = new Matrix4f(poseStack.last().pose());
   }
}

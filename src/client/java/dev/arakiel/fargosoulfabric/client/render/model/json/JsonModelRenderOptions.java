package dev.arakiel.fargosoulfabric.client.render.model.json;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;

public final class JsonModelRenderOptions {
   private final ModelResourceLocation model;
   private int color = -1;
   private int packedLight = 15728880;

   public JsonModelRenderOptions(ModelResourceLocation model) {
      this.model = model;
   }

   public JsonModelRenderOptions color(int argb) {
      this.color = argb;
      return this;
   }

   public JsonModelRenderOptions light(int packedLight) {
      this.packedLight = packedLight;
      return this;
   }

   public boolean render(PoseStack poseStack, MultiBufferSource bufferSource) {
      return JsonModelRenderer.render(this.model, poseStack, bufferSource, this.color, this.packedLight);
   }
}

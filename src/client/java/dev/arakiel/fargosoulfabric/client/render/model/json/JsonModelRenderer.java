package dev.arakiel.fargosoulfabric.client.render.model.json;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.arakiel.fargosoulfabric.client.render.ColorBufferSource;
import dev.arakiel.fargosoulfabric.client.render.FSFRenderTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class JsonModelRenderer {
   public JsonModelRenderer() {
   }

   public static ResourceLocation resourcePath(ResourceLocation modelId) {
      return ResourceLocation.fromNamespaceAndPath(modelId.getNamespace(), "fargosoul_model/json/" + modelId.getPath() + "/" + fileName(modelId.getPath()));
   }

   public static ModelResourceLocation standaloneLocation(ResourceLocation modelId) {
      return new ModelResourceLocation(resourcePath(modelId), "standalone");
   }

   private static String fileName(String path) {
      int slash = path.lastIndexOf(47);
      return slash >= 0 ? path.substring(slash + 1) : path;
   }

   static boolean render(ModelResourceLocation modelLocation, PoseStack poseStack, MultiBufferSource bufferSource, int color, int packedLight) {
      ModelManager modelManager = Minecraft.getInstance().getModelManager();
      BakedModel model = modelManager.getModel(modelLocation);
      if (model != modelManager.getMissingModel()) {
         ColorBufferSource colorBufferSource = new ColorBufferSource(bufferSource);
         colorBufferSource.setColor(color);
         VertexConsumer consumer = colorBufferSource.getBuffer(FSFRenderTypes.getModel());
         dev.arakiel.fargosoulfabric.client.render.RenderUtil.renderModel(model, poseStack, consumer, packedLight);
      }

      return true;
   }
}

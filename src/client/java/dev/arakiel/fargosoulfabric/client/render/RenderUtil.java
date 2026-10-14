package dev.arakiel.fargosoulfabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class RenderUtil {
   public static final int FULL_LIGHT = 15728880;

   private RenderUtil() {
   }

   public static void renderImage(
      ResourceLocation texture, Vec3 center, float width, float height, MultiBufferSource bufferSource, boolean alwaysVisible, int tintColor
   ) {
      Camera camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
      VertexConsumer consumer = bufferSource.getBuffer(FSFRenderTypes.texture(texture, alwaysVisible));
      Vec3 camPos = camera.getPosition();
      Quaternionf rotation = new Quaternionf(camera.rotation()).mul(Axis.XN.rotationDegrees(180.0F), new Quaternionf());
      Matrix4f matrix = new Matrix4f()
         .rotate(rotation)
         .setTranslation((float)(center.x - camPos.x), (float)(center.y - camPos.y), (float)(center.z - camPos.z));
      float halfWidth = width / 2.0F;
      float halfHeight = height / 2.0F;
      float[][] corners = new float[][]{
         {-halfWidth, -halfHeight, 0.0F, 0.0F}, {-halfWidth, halfHeight, 0.0F, 1.0F}, {halfWidth, halfHeight, 1.0F, 1.0F}, {halfWidth, -halfHeight, 1.0F, 0.0F}
      };
      Vector3f v = new Vector3f();

      for (float[] corner : corners) {
         matrix.transformPosition(corner[0], corner[1], 0.0F, v);
         consumer.addVertex(v.x, v.y, v.z, tintColor, corner[2], corner[3], OverlayTexture.NO_OVERLAY, 15728880, 0.0F, 0.0F, 1.0F);
      }
   }

   /**
    * Renders one of the models the mod declares as standalone, centered on the pose of
    * {@code poseStack}.
    */
   public static void renderStandalone(ModelResourceLocation modelLocation, PoseStack poseStack,
                                       MultiBufferSource bufferSource, int tintColor, int packedLight) {
      BakedModel model = Minecraft.getInstance().getModelManager().getModel(modelLocation);
      renderModel(model, poseStack, bufferSource.getBuffer(FSFRenderTypes.getModel()), packedLight);
   }

   /** Draws a baked model that is already positioned by {@code poseStack}. */
   public static void renderModel(BakedModel model, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
      Minecraft.getInstance()
              .getBlockRenderer()
              .getModelRenderer()
              .renderModel(poseStack.last(), consumer, null, model, 1.0F, 1.0F, 1.0F, packedLight,
                      OverlayTexture.NO_OVERLAY);
   }
}

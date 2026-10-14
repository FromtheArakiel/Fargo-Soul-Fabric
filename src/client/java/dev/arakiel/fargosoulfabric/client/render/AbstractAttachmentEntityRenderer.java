package dev.arakiel.fargosoulfabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import software.bernie.geckolib.util.Color;

public abstract class AbstractAttachmentEntityRenderer<T extends AttachmentEntity> implements IAttachmentEntityRenderer<T> {
   /** Whether the summon fades out when the player stands right inside it. */
   public static boolean alphaModify = true;

   protected RenderContext<T> context;

   public AbstractAttachmentEntityRenderer() {
   }

   protected RenderContext<T> createContext(T entity, PathNode visualNode, float partialTick, int packedLight) {
      return new RenderContext<>(entity, visualNode, partialTick, packedLight);
   }

   @Override
   public void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, int packedLight, PathNode visualNode) {
      this.context = this.createContext(entity, visualNode, partialTick, packedLight);
      if (this.context != null) {
         float alpha = this.getAlpha();
         this.context
            .color(
               Color.ofARGB(
                  alpha * this.context.color.getAlphaFloat(),
                  this.context.color.getRedFloat(),
                  this.context.color.getGreenFloat(),
                  this.context.color.getBlueFloat()
               )
            );
         poseStack.pushPose();
         if (this.context.hasTrail()) {
            this.context.trail.render(poseStack, bufferSource, this.context);
         }

         this.modelModify(poseStack, bufferSource);
         poseStack.popPose();
      }
   }

   protected void modelModify(PoseStack poseStack, MultiBufferSource bufferSource) {
      PathNode visualNode = this.context.visualNode;
      ModelContext model = this.context.model;
      Quaternionf rotation = new Quaternionf()
         .mul(Axis.YN.rotationDegrees(visualNode.yaw()))
         .mul(Axis.XP.rotationDegrees(visualNode.pitch()))
         .mul(Axis.ZP.rotationDegrees(visualNode.roll()))
         .mul(Axis.YN.rotationDegrees(model.yawOffset))
         .mul(Axis.XP.rotationDegrees(model.pitchOffset))
         .mul(Axis.ZP.rotationDegrees(model.rollOffset));
      poseStack.pushPose();
      poseStack.mulPose(rotation);
      poseStack.scale(model.scaleX, model.scaleY, model.scaleZ);
      poseStack.translate(model.translateX, model.translateY, model.translateZ);
      this.renderModel(poseStack, bufferSource);
      this.render(poseStack, bufferSource);
      poseStack.popPose();
   }

   protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
   }

   protected void render(PoseStack poseStack, MultiBufferSource bufferSource) {
   }

   protected float getAlpha() {
      return alphaModify ? this.getAlphaModify() : 1.0F;
   }

   protected float getAlphaModify() {
      Minecraft minecraft = Minecraft.getInstance();
      Player player = minecraft.player;
      if (player != null && minecraft.options.getCameraType().isFirstPerson()) {
         Vec3 entityPos = this.context.visualNode.pos();
         Vec3 eyePos = player.getEyePosition(this.context.partialTick);
         double distance = entityPos.distanceTo(eyePos);
         float minDistance = 0.5F * this.context.model.alphaDistanceFactor;
         float maxDistance = 4.0F * this.context.model.alphaDistanceFactor;
         if (distance <= (double)minDistance) {
            return 0.0F;
         } else {
            return distance >= (double)maxDistance
               ? 1.0F
               : Math.clamp((float)((distance - (double)minDistance) / (double)(maxDistance - minDistance)), 0.102F, 1.0F);
         }
      } else {
         return 1.0F;
      }
   }
}

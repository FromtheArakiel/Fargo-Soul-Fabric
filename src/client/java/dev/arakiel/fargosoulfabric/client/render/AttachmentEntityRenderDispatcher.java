package dev.arakiel.fargosoulfabric.client.render;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.IBlockCollision;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class AttachmentEntityRenderDispatcher {
   /** Set to true to render the summon hitboxes, the debug view of the original mod. */
   public static boolean debugMode = false;

   private static final Map<AttachmentEntityType<?>, IAttachmentEntityRenderer<?>> renderers = new HashMap<>();

   public AttachmentEntityRenderDispatcher() {
   }

   public static void render(ClientLevel level, Camera camera, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
      for (AbstractClientPlayer player : level.players()) {
         List<AttachmentEntity> entities = Attachments.get(player, FargoSoulAttachmentRegister.ENTITY_DATA).getRenderCache();
         Vec3 cameraPos = camera.getPosition();
         int playerLight = getLightCoords(level, player.getLightProbePosition(partialTick));
         boolean showHitboxes = Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes();
         VertexConsumer debugConsumer = showHitboxes ? bufferSource.getBuffer(RenderType.lines()) : null;

         for (AttachmentEntity entity : entities) {
            poseStack.pushPose();
            PathNode renderNode = entity.getRenderNode(partialTick);
            Vec3 pos = renderNode.pos();
            poseStack.translate(pos.x() - cameraPos.x(), pos.y() - cameraPos.y(), pos.z() - cameraPos.z());
            IAttachmentEntityRenderer<AttachmentEntity> renderer = getRenderer(entity);
            if (renderer != null) {
               int lightCoords = Math.max(playerLight, getLightCoords(level, pos));
               renderer.render(entity, poseStack, bufferSource, partialTick, lightCoords, renderNode);
            }

            if (debugMode) {
               debugRender(poseStack, entity, showHitboxes, renderNode, debugConsumer);
            }

            poseStack.popPose();
         }
      }
   }

   private static int getLightCoords(Level level, Vec3 pos) {
      BlockPos blockPos = BlockPos.containing(pos);
      int sky = level.getBrightness(LightLayer.SKY, blockPos);
      int block = Math.max(level.getBrightness(LightLayer.BLOCK, blockPos), level.getBlockState(blockPos).getLightEmission());
      int packed = LightTexture.pack(block, sky);
      return DynamicLightDispatcher.INSTANCE.getDynamicLight(pos, packed);
   }

   private static void debugRender(PoseStack poseStack, AttachmentEntity entity, boolean showHitboxes, PathNode renderNode, VertexConsumer debugConsumer) {
      if (showHitboxes) {
         LevelRenderer.renderLineBox(poseStack, debugConsumer, -0.001, -0.001, -0.001, 0.001, 0.001, 0.001, 1.0F, 1.0F, 0.0F, 1.0F);
         poseStack.pushPose();
         poseStack.mulPose(Axis.YN.rotationDegrees(renderNode.yaw()));
         poseStack.mulPose(Axis.XP.rotationDegrees(renderNode.pitch()));
         poseStack.mulPose(Axis.ZP.rotationDegrees(renderNode.roll()));
         LevelRenderer.renderLineBox(poseStack, debugConsumer, -1.0E-4, -1.0E-4, 0.0, 1.0E-4, 1.0E-4, 2.0, 0.0F, 0.0F, 1.0F, 1.0F);
         LevelRenderer.renderLineBox(poseStack, debugConsumer, -1.0E-4, 0.0, -1.0E-4, 1.0E-4, 0.5, 1.0E-4, 0.0F, 0.0F, 1.0F, 1.0F);
         if (entity instanceof IEntityCollision<?> iCollideAttack && iCollideAttack.renderHitbox()) {
            LevelRenderer.renderLineBox(poseStack, debugConsumer, iCollideAttack.getHitbox(), 1.0F, 0.0F, 0.0F, 1.0F);
         }

         poseStack.popPose();
         if (entity instanceof IBlockCollision<?> iBlockCollision) {
            LevelRenderer.renderLineBox(poseStack, debugConsumer, iBlockCollision.getBlockCollisionBox(), 0.0F, 1.0F, 0.0F, 1.0F);
         }
      }
   }

   public static <T extends AttachmentEntity> IAttachmentEntityRenderer<T> getRenderer(T entity) {
      AttachmentEntityType<T> type = typeOf(entity);
      return getRendererFor(type);
   }

   @SuppressWarnings("unchecked")
   private static <T extends AttachmentEntity> AttachmentEntityType<T> typeOf(T entity) {
      return (AttachmentEntityType<T>) entity.getType();
   }

   @SuppressWarnings("unchecked")
   private static <T extends AttachmentEntity> IAttachmentEntityRenderer<T> getRendererFor(
           AttachmentEntityType<T> type) {
      return (IAttachmentEntityRenderer<T>) renderers.get(type);
   }

   public static <T extends AttachmentEntity> void register(AttachmentEntityType<T> type, IAttachmentEntityRenderer<T> renderer) {
      if (!renderers.containsKey(type)) {
         renderers.put(type, renderer);
      }
   }
}
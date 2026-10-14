package dev.arakiel.fargosoulfabric.client.render;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.common.summon.IBlockCollision;
import dev.arakiel.fargosoulfabric.common.summon.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
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

public class SummonRenderDispatcher {
   /** Set to true to render the summon hitboxes, the debug view of the original mod. */
   public static boolean debugMode = false;

   private static final Map<SummonType<?>, ISummonRenderer<?>> renderers = new HashMap<>();

   public SummonRenderDispatcher() {
   }

   public static void render(ClientLevel level, Camera camera, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
      for (AbstractClientPlayer player : level.players()) {
         List<Summon> entities = Attachments.get(player, FSFAttachmentRegister.ENTITY_DATA).getRenderCache();
         Vec3 cameraPos = camera.getPosition();
         int playerLight = getLightCoords(level, player.getLightProbePosition(partialTick));
         boolean showHitboxes = Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes();
         VertexConsumer debugConsumer = showHitboxes ? bufferSource.getBuffer(RenderType.lines()) : null;

         for (Summon entity : entities) {
            poseStack.pushPose();
            PathNode renderNode = entity.getRenderNode(partialTick);
            Vec3 pos = renderNode.pos();
            poseStack.translate(pos.x() - cameraPos.x(), pos.y() - cameraPos.y(), pos.z() - cameraPos.z());
            ISummonRenderer<Summon> renderer = getRenderer(entity);
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

   private static void debugRender(PoseStack poseStack, Summon entity, boolean showHitboxes, PathNode renderNode, VertexConsumer debugConsumer) {
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

   public static <T extends Summon> ISummonRenderer<T> getRenderer(T entity) {
      SummonType<T> type = typeOf(entity);
      return getRendererFor(type);
   }

   @SuppressWarnings("unchecked")
   private static <T extends Summon> SummonType<T> typeOf(T entity) {
      return (SummonType<T>) entity.getType();
   }

   @SuppressWarnings("unchecked")
   private static <T extends Summon> ISummonRenderer<T> getRendererFor(
           SummonType<T> type) {
      return (ISummonRenderer<T>) renderers.get(type);
   }

   public static <T extends Summon> void register(SummonType<T> type, ISummonRenderer<T> renderer) {
      if (!renderers.containsKey(type)) {
         renderers.put(type, renderer);
      }
   }
}
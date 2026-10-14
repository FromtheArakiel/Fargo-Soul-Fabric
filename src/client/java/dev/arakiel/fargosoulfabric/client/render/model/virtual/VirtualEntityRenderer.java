package dev.arakiel.fargosoulfabric.client.render.model.virtual;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.render.ColorBufferSource;
import dev.arakiel.fargosoulfabric.mixin.WalkAnimationStateAccessor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VirtualEntityRenderer {
   private static final Logger LOGGER = LoggerFactory.getLogger(VirtualEntityRenderer.class);
   private static final Map<EntityType<?>, Entity> GHOSTS = new HashMap<>();
   private static final Set<EntityType<?>> CUSTOMIZED = new HashSet<>();

   private VirtualEntityRenderer() {
   }

   public static boolean render(ResourceLocation entityTypeId, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
      EntityType<?> type = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
      return render(type, poseStack, bufferSource, partialTick, VirtualEntityPose.create());
   }

   public static boolean render(ResourceLocation entityTypeId, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, VirtualEntityPose pose) {
      EntityType<?> type = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
      return render(type, poseStack, bufferSource, partialTick, pose);
   }

   public static boolean render(EntityType<?> type, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick) {
      return render(type, poseStack, bufferSource, partialTick, VirtualEntityPose.create());
   }

   public static boolean render(EntityType<?> type, PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, VirtualEntityPose pose) {
      if (pose == null) {
         pose = VirtualEntityPose.create();
      }

      Minecraft minecraft = Minecraft.getInstance();
      Level level = minecraft.level;
      if (level == null) {
         return false;
      } else {
         Entity ghost = getOrCreateGhost(type, level, pose);
         if (ghost == null) {
            return false;
         } else {
            EntityRenderer<?> dispatcherRenderer = minecraft.getEntityRenderDispatcher().getRenderer(ghost);
            applyPose(ghost, pose);
            MultiBufferSource source = bufferSource;
            if (pose.color() != -1 || pose.alpha() < 1.0F) {
               source = new ColorBufferSource(bufferSource).setColor(pose.color()).setAlpha(pose.alpha());
            }

            poseStack.pushPose();
            if (pose.scale() != 1.0F) {
               poseStack.scale(pose.scale(), pose.scale(), pose.scale());
            }

            @SuppressWarnings("unchecked")
            net.minecraft.client.renderer.entity.EntityRenderer<Entity> renderer =
                    (net.minecraft.client.renderer.entity.EntityRenderer<Entity>) dispatcherRenderer;
            renderer.render(ghost, pose.bodyYawDegrees(), partialTick, poseStack, source, pose.packedLight());
            poseStack.popPose();
            return true;
         }
      }
   }

   private static Entity getOrCreateGhost(EntityType<?> type, Level level, VirtualEntityPose pose) {
      Entity ghost = GHOSTS.get(type);
      boolean freshRequired = pose.customizer() != null || CUSTOMIZED.contains(type);
      if (ghost != null && ghost.level() == level && !freshRequired) {
         return ghost;
      } else {
         ghost = type.create(level);
         if (ghost != null) {
            GHOSTS.put(type, ghost);
            if (pose.customizer() != null) {
               CUSTOMIZED.add(type);
            } else {
               CUSTOMIZED.remove(type);
            }
         }

         return ghost;
      }
   }

   private static void applyPose(Entity entity, VirtualEntityPose pose) {
      entity.tickCount = pose.ageTicks();
      entity.moveTo(0.0, 0.0, 0.0, pose.bodyYawDegrees(), pose.headPitchDegrees());
      if (entity instanceof LivingEntity living) {
         float bodyYaw = pose.bodyYawDegrees();
         float headYaw = pose.headYawDegrees();
         living.yBodyRot = bodyYaw;
         living.yBodyRotO = bodyYaw;
         living.yHeadRot = headYaw;
         living.yHeadRotO = headYaw;
         living.oAttackAnim = pose.attackProgress();
         living.attackAnim = pose.attackProgress();
         living.hurtTime = pose.hurtTime();
         living.hurtDuration = Math.max(pose.hurtTime(), 10);
         living.deathTime = pose.deathTime();
         if (pose.deathTime() > 0) {
            living.setHealth(0.0F);
         } else if (living.getHealth() <= 0.0F) {
            living.setHealth(living.getMaxHealth());
         }

         applyWalk(living, pose);
         entity.setShiftKeyDown(pose.crouching());
         if (pose.swimming()) {
            entity.setSwimming(true);
            entity.setPose(Pose.SWIMMING);
         } else {
            entity.setSwimming(false);
            entity.setPose(pose.crouching() ? Pose.CROUCHING : Pose.STANDING);
         }

         entity.refreshDimensions();
      }

      if (pose.customizer() != null) {
         pose.customizer().accept(entity);
      }
   }

   private static void applyWalk(LivingEntity living, VirtualEntityPose pose) {
      float amount = Math.max(0.0F, pose.limbSwingAmount());
      float position = Math.max(0.0F, pose.limbSwingPosition());
      WalkAnimationStateAccessor accessor = (WalkAnimationStateAccessor)living.walkAnimation;
      accessor.setSpeedOld(amount);
      accessor.setPosition(position);
      living.walkAnimation.setSpeed(amount);
   }
}

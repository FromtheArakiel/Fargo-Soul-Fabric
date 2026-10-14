package dev.arakiel.fargosoulfabric.client.render.model.virtual;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EntityType;

public final class VirtualEntityRenderOptions {
   private final EntityType<?> entityType;
   private final float partialTick;
   private VirtualEntityPose pose = VirtualEntityPose.create();

   public VirtualEntityRenderOptions(EntityType<?> entityType, float partialTick) {
      this.entityType = entityType;
      this.partialTick = partialTick;
   }

   public VirtualEntityRenderOptions pose(VirtualEntityPose pose) {
      if (pose != null) {
         this.pose = pose;
      }

      return this;
   }

   public VirtualEntityRenderOptions pose(Consumer<VirtualEntityPose> configurator) {
      if (configurator != null) {
         configurator.accept(this.pose);
      }

      return this;
   }

   public boolean render(PoseStack poseStack, MultiBufferSource bufferSource) {
      return VirtualEntityRenderer.render(this.entityType, poseStack, bufferSource, this.partialTick, this.pose);
   }
}

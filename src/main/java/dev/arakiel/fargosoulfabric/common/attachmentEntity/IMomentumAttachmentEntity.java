package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface IMomentumAttachmentEntity {
   @NotNull
   Vec3 getVelocity();

   void setVelocity(@NotNull Vec3 var1);

   void addVelocity(@NotNull Vec3 var1);

   float getDrag();

   void setDrag(float var1);

   float getGravity();

   void setGravity(float var1);
}

package dev.arakiel.fargosoulfabric.common.summon;

import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface IMomentumSummon {
   @NotNull
   Vec3 getVelocity();

   void setVelocity(@NotNull Vec3 var1);

   void addVelocity(@NotNull Vec3 var1);

   float getDrag();

   void setDrag(float var1);

   float getGravity();

   void setGravity(float var1);
}

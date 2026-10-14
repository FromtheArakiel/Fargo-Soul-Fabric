package dev.arakiel.fargosoulfabric.mixin;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({WalkAnimationState.class})
public interface WalkAnimationStateAccessor {
   @Accessor
   float getSpeedOld();

   @Mutable
   @Accessor
   void setSpeedOld(float var1);

   @Accessor
   float getPosition();

   @Mutable
   @Accessor
   void setPosition(float var1);
}

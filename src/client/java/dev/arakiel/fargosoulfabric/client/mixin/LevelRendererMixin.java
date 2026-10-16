package dev.arakiel.fargosoulfabric.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets the dynamic light of the summons take part in the lighting of the world. */
@Mixin({LevelRenderer.class})
public class LevelRendererMixin {
   public LevelRendererMixin() {
   }

   @ModifyReturnValue(
      method = {"getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I"},
      at = {@At("RETURN")}
   )
   private static int fsf$getLightColor(int original, BlockAndTintGetter level, BlockState state, BlockPos pos) {
      return DynamicLightDispatcher.INSTANCE.getDynamicLight(level, state, pos, original);
   }

   /** Starts a new frame of dynamic light sources, once the visible sections are known. */
   @Inject(
      method = {"compileSections"},
      at = {@At("HEAD")}
   )
   private void fsf$compileSections(Camera camera, CallbackInfo ci) {
      DynamicLightDispatcher.INSTANCE.update((LevelRendererAccessor)this);
   }

}

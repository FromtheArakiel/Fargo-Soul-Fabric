package dev.arakiel.fargosoulfabric.client.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LevelRenderer.class})
public interface LevelRendererAccessor {
   @Invoker
   void callSetSectionDirty(int var1, int var2, int var3, boolean var4);
}

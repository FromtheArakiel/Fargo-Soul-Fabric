package dev.arakiel.fargosoulfabric.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.arakiel.fargosoulfabric.common.FSFHooks;
import dev.arakiel.fargosoulfabric.client.FSFClientHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets the gameplay code replace the fog the game just set up. */
@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void fsf$setupFog(Camera camera, FogRenderer.FogMode mode, float renderDistance,
                                           boolean thickFog, float partialTick, CallbackInfo ci) {
        FSFHooks.Value nearPlane = new FSFHooks.Value(RenderSystem.getShaderFogStart());
        FSFHooks.Value farPlane = new FSFHooks.Value(RenderSystem.getShaderFogEnd());
        FSFHooks.Value cancel = new FSFHooks.Value(0.0F);
        FSFClientHooks.FOG.invoker().onFog(camera, nearPlane, farPlane, cancel);
        if (cancel.isCanceled()) {
            RenderSystem.setShaderFogStart(nearPlane.get());
            RenderSystem.setShaderFogEnd(farPlane.get());
        }
    }
}

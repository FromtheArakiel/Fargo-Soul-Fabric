package dev.arakiel.fargosoulfabric.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import dev.arakiel.fargosoulfabric.client.api.FargoSoulClientHooks;
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
    private static void fargoSoul$setupFog(Camera camera, FogRenderer.FogMode mode, float renderDistance,
                                           boolean thickFog, float partialTick, CallbackInfo ci) {
        FargoSoulHooks.Value nearPlane = new FargoSoulHooks.Value(RenderSystem.getShaderFogStart());
        FargoSoulHooks.Value farPlane = new FargoSoulHooks.Value(RenderSystem.getShaderFogEnd());
        FargoSoulHooks.Value cancel = new FargoSoulHooks.Value(0.0F);
        FargoSoulClientHooks.FOG.invoker().onFog(camera, nearPlane, farPlane, cancel);
        if (cancel.isCanceled()) {
            RenderSystem.setShaderFogStart(nearPlane.get());
            RenderSystem.setShaderFogEnd(farPlane.get());
        }
    }
}

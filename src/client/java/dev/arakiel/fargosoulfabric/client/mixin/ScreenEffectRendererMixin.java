package dev.arakiel.fargosoulfabric.client.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Lets the gameplay code hide the fire overlay. */
@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @WrapWithCondition(method = "renderScreenEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ScreenEffectRenderer;renderFire(Lnet/minecraft/client/Minecraft;Lcom/mojang/blaze3d/vertex/PoseStack;)V"))
    private static boolean fargoSoul$renderFire(Minecraft minecraft, PoseStack poseStack) {
        FargoSoulHooks.Value cancel = new FargoSoulHooks.Value(0.0F);
        FargoSoulHooks.FIRE_OVERLAY.invoker().onFireOverlay(minecraft.player, cancel);
        return !cancel.isCanceled();
    }
}

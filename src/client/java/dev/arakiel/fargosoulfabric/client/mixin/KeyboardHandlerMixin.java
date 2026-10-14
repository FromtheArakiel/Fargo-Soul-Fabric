package dev.arakiel.fargosoulfabric.client.mixin;

import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.client.register.FargoSoulKeyRegister;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Turns raw key presses into the key hook. */
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void fargoSoul$keyPress(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (action != GLFW.GLFW_PRESS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        SoulKey soulKey = FargoSoulKeyRegister.resolve(key);
        if (soulKey != null) {
            FargoSoulHooks.KEY_INPUT.invoker().onKeyInput(minecraft.player, soulKey);
        }
    }
}

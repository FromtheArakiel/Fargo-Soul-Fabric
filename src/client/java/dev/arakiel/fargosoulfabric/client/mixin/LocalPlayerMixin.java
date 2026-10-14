package dev.arakiel.fargosoulfabric.client.mixin;

import dev.arakiel.fargosoulfabric.client.api.FargoSoulClientHooks;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires the movement hook right after the client updated its input. */
@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V",
            shift = At.Shift.AFTER))
    private void fargoSoul$movementInput(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;
        FargoSoulClientHooks.MOVEMENT_INPUT.invoker().onMovementInput(self, self.input);
    }
}

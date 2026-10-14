package dev.arakiel.fargosoulfabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires the player sided hooks. */
@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void fargoSoul$tick(CallbackInfo ci) {
        FargoSoulHooks.PLAYER_TICK.invoker().afterPlayerTick((Player) (Object) this);
    }

    @ModifyExpressionValue(method = "attack", at = @At(value = "CONSTANT", args = "floatValue=1.5"))
    private float fargoSoul$criticalDamage(float original, @Local(argsOnly = true) Entity target) {
        FargoSoulHooks.Value multiplier = new FargoSoulHooks.Value(1.0F);
        FargoSoulHooks.CRITICAL_HIT.invoker().onCriticalHit((Player) (Object) this, target, true, multiplier);
        return original * multiplier.get();
    }
}

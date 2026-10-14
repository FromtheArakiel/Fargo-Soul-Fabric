package dev.arakiel.fargosoulfabric.mixin;

import dev.arakiel.fargosoulfabric.common.FargoSoulAttributes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Hands out the summon attributes together with the vanilla ones. */
@Mixin(DefaultAttributes.class)
public class DefaultAttributesMixin {

    @Inject(method = "getSupplier", at = @At("RETURN"), cancellable = true)
    private static void fargoSoul$getSupplier(EntityType<? extends LivingEntity> type,
                                              CallbackInfoReturnable<AttributeSupplier> cir) {
        AttributeSupplier patched = FargoSoulAttributes.patch(type, cir.getReturnValue());
        if (patched != null) {
            cir.setReturnValue(patched);
        }
    }
}

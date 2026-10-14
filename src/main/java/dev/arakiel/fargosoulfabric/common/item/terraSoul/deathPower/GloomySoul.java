package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GloomySoul extends SoulItem {

    public GloomySoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (target.hasEffect(FargoSoulMobEffectRegister.ShadowFire)) {
            if (attacker.getRandom().nextFloat() < 0.05f) {
                target.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.DeathMark, 40));
            }
        } else {
            target.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.ShadowFire, 200));
        }
    }
}

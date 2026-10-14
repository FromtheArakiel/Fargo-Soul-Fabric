package dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeadSoul extends SoulItem {

    public LeadSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        target.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.LeadPoisoning, 100));
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (effectInstance.is(FargoSoulMobEffectRegister.LeadPoisoning)) {
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }
}

package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrystalAssassinSoul extends SoulItem {

    public CrystalAssassinSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSprint(Player player) {
        return true;
    }

    @Override
    public void sprintServer(Player player) {
        player.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.PreemptiveStrike, 20));
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (attacker.hasEffect(FargoSoulMobEffectRegister.PreemptiveStrike)) {
            attacker.removeEffect(FargoSoulMobEffectRegister.PreemptiveStrike);
            modifiers.add(new ValueModifier(0.6f, ValueOperation.ADD_MULTIPLIED_BASE));
            target.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.CrystalArmorBreak, 200));
        }
    }
}

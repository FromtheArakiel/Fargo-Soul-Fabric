package dev.arakiel.fargosoulfabric.common.item.terraSoul.lifePower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TurtleSoul extends SoulItem {

    public TurtleSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (container.getSource().is(DamageTypes.CACTUS)) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        } else {
            if (target.getHealth() < target.getMaxHealth() * 0.25f) {
                modifiers.add(new ValueModifier(-target.getMaxHealth() * 0.04f, ValueOperation.ADD_VALUE));
            }
            if (attacker instanceof LivingEntity living) {
                float ratio = target.getHealth() < target.getMaxHealth() * 0.5f ? 0.8f : 0.4f;
                living.hurt(target.damageSources().cactus(), container.getOriginalDamage() * ratio);
            }
        }
    }
}
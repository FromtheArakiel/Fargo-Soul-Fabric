package dev.arakiel.fargosoulfabric.common.item.terraSoul.forestPower;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.SoulKey;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PalmWoodSoul extends SoulItem {

    public PalmWoodSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (target.hasEffect(FSFMobEffectRegister.Oiled)) {
            modifiers.add(new ValueModifier(0.2f, ValueOperation.ADD_MULTIPLIED_BASE));
            target.igniteForTicks(100);
        }
    }

    @Override
    public @Nullable ResourceLocation keyAction(Player player, SoulKey key) {
        if (key == SoulKey.PalmWood) {
            return FSF.rl("palm_oil");
        }
        return super.keyAction(player, key);
    }

    @Override
    public void handleKeyAction(Player player, ResourceLocation location) {
        if (location.equals(FSF.rl("palm_oil"))) {
            List<LivingEntity> enemies = SoulTargets.get(player).getEntitiesInRadius(player.getBoundingBox().getCenter(), 6, null);
            for (LivingEntity enemy : enemies) {
                enemy.addEffect(new MobEffectInstance(FSFMobEffectRegister.Oiled, 400));
            }
            // 棕榈爆炸: the oil bursts out around the player.
            FSFEffects.aura(player.level(), player, ParticleTypes.LARGE_SMOKE, 50, 3.0D, 0.05D);
        }
    }
}

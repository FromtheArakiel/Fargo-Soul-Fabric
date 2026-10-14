package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.common.summon.IEntityCollision;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FSFItemRegister.*;

public class DeathPower extends SoulItem {

    public DeathPower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(AncientShadowSoulItem, CrystalAssassinSoulItem, DarkArtistSoulItem, GloomySoulItem, NecromancerSoulItem, NinjaSoulItem, PenetratingNinjaSoulItem);
    }

    @Override
    public void sprintClient(Player player, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(0.5f, ValueOperation.ADD_MULTIPLIED_BASE));
    }

    @Override
    public void sprintCollision(Player player, List<IEntityCollision.HitContext> list, Set<LivingEntity> hits) {
        for (IEntityCollision.HitContext context : list) {
            LivingEntity living = context.entity();
            living.addEffect(new MobEffectInstance(FSFMobEffectRegister.DeathMark, 200));
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (target.hasEffect(FSFMobEffectRegister.DeathMark)) {
            modifiers.add(new ValueModifier(2, ValueOperation.ADD_MULTIPLIED_BASE));
        }
        if (target.getHealth() < target.getMaxHealth() * 0.12f) {
            modifiers.add(new ValueModifier(target.getMaxHealth() * 0.25f, ValueOperation.ADD_VALUE));
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (effectInstance.is(FSFMobEffectRegister.DeathMark)) {
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }
}

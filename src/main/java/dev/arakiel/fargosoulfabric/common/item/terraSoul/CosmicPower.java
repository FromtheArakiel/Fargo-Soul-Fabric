package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FSFItemRegister.*;

public class CosmicPower extends SoulItem {

    public CosmicPower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(BlazeSoulItem, METEOR_SOUL, WizardSoulItem, StardustSoulItem, NebulaSoulItem, VortexSoulItem);
    }

    @Override
    public void tick(Player living) {
        Info soulInfo = SoulStates.getSoulState(living, SoulStateRegister.COSMIC_POWER_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(living, soulInfo);
        }
        if (living.tickCount % 20 == 0 && soulInfo.cosmicPower < soulInfo.maxCosmicPower) {
            soulInfo.cosmicPower++;
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(attacker, SoulStateRegister.COSMIC_POWER_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(attacker, soulInfo);
        }
        modifiers.add(new ValueModifier(0.8f * (soulInfo.cosmicPower / soulInfo.maxCosmicPower), ValueOperation.ADD_VALUE));
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(target, SoulStateRegister.COSMIC_POWER_INFO);
        if (soulInfo != null) {
            modifiers.add(new ValueModifier(-0.2f * (soulInfo.cosmicPower / soulInfo.maxCosmicPower), ValueOperation.ADD_MULTIPLIED_TOTAL));
            float damage = container.getOriginalDamage();
            float maxHealth = target.getMaxHealth();
            if (damage > maxHealth * 0.5f && soulInfo.cosmicPower >= 50) {
                soulInfo.cosmicPower -= 50;
                modifiers.add(new ValueModifier(-0.5f, ValueOperation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    @Override
    public boolean death(Player player, DamageSource source, boolean canceled) {
        SoulTargets targetCache = SoulTargets.get(player);
        List<LivingEntity> list = targetCache.getEntitiesInRadius(player.getBoundingBox().getCenter(), 6, null);
        for (LivingEntity living : list) {
            Invulnerability.attack(living)
                    .damageSource(player.damageSources().magic())
                    .damageAmount(player.getMaxHealth() * 36f)
                    .apply();
        }
        return super.death(player, source, canceled);
    }

    public static final class Info extends SoulState {

        public float cosmicPower = 0;
        public final float maxCosmicPower = 100;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.COSMIC_POWER_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.CosmicPowerItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("cosmicPower", cosmicPower);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            cosmicPower = tag.getFloat("cosmicPower");
        }
    }
}

package dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulState.SoulState;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SilverSoul extends SoulItem {

    public SilverSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void shieldBlock(Player player, DamageSource source, float blockedDamage) {
        int usingTicks = player.getTicksUsingItem();
        Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.SILVER_SOUL_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(player, soulInfo);
        }
        if (usingTicks >= 4 && usingTicks <= 8 && soulInfo.cooldown <= 0 && source.getEntity() instanceof LivingEntity attacker) {
            soulInfo.cooldown = 60;
            soulInfo.invincible = 8;
            attacker.hurt(player.damageSources().playerAttack(player), blockedDamage * 2.0f);
            player.addEffect(new MobEffectInstance(FSFMobEffectRegister.AmazingMoment, 30));
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (attacker.hasEffect(FSFMobEffectRegister.AmazingMoment)) {
            modifiers.add(new ValueModifier(1.5f, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(target, SoulStateRegister.SILVER_SOUL_INFO);
        if (soulInfo != null && soulInfo.invincible > 0) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;
        public int invincible = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.SILVER_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.SilverSoulItem)) {
                if (cooldown > 0) {
                    cooldown--;
                }
                if (invincible > 0) {
                    invincible--;
                }
                if (cooldown <= 0 && invincible <= 0) {
                    setRemove(true);
                }
            } else {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putInt("cooldown", cooldown);
            tag.putInt("invincible", invincible);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            cooldown = tag.getInt("cooldown");
            invincible = tag.getInt("invincible");
        }
    }
}

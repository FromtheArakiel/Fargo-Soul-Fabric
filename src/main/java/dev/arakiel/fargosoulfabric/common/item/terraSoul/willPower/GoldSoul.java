package dev.arakiel.fargosoulfabric.common.item.terraSoul.willPower;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.SoulKey;
import dev.arakiel.fargosoulfabric.FSF;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoldSoul extends SoulItem {

    public GoldSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        target.addEffect(new MobEffectInstance(FSFMobEffectRegister.Midas, 200));
    }

    @Override
    public void kill(Player player, LivingEntity target, DamageSource source) {
        if (target.hasEffect(FSFMobEffectRegister.Midas)) {
            int count = 1 + player.getRandom().nextInt(4);
            for (int i = 0; i < count; i++) {
                target.spawnAtLocation(new ItemStack(Items.GOLD_INGOT));
            }
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (effectInstance.is(FSFMobEffectRegister.Midas)) {
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(target, SoulStateRegister.GOLD_SOUL_INFO);
        if (info != null && info.active) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public void tick(Player player) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.GOLD_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        if (info.cd > 0) {
            info.cd--;
        }
        if (info.active) {
            if (info.invincible > 0) {
                info.invincible--;
            } else if (!tryConsumeGoldIngot(player)) {
                info.active = false;
            }
        }
    }

    private static boolean tryConsumeGoldIngot(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.GOLD_INGOT)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable ResourceLocation keyAction(Player player, SoulKey key) {
        if (key == SoulKey.Gold) {
            return FSF.rl("gold_stasis");
        }
        return super.keyAction(player, key);
    }

    @Override
    public void handleKeyAction(Player player, ResourceLocation location) {
        if (location.equals(FSF.rl("gold_stasis"))) {
            Info info = SoulStates.getSoulState(player, SoulStateRegister.GOLD_SOUL_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(player, info);
            }
            if (info.cd <= 0) {
                info.cd = 2400;
                info.invincible = 120;
                info.active = true;
                // 金身: the totem of undying particle, so the field is visible while it is up.
                FSFEffects.aura(player.level(), player, ParticleTypes.TOTEM_OF_UNDYING, 80, 0.9D, 0.35D);
            }
        }
    }

    public static final class Info extends SoulState {

        public boolean active = false;
        public int invincible = 0;
        public int cd = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.GOLD_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.GoldSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putBoolean("active", active);
            tag.putInt("invincible", invincible);
            tag.putInt("cd", cd);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            active = tag.getBoolean("active");
            invincible = tag.getInt("invincible");
            cd = tag.getInt("cd");
        }
    }
}

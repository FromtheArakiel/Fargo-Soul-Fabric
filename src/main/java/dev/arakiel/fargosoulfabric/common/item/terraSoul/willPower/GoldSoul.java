package dev.arakiel.fargosoulfabric.common.item.terraSoul.willPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
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
        target.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.Midas, 200));
    }

    @Override
    public void kill(Player player, LivingEntity target, DamageSource source) {
        if (target.hasEffect(FargoSoulMobEffectRegister.Midas)) {
            int count = 1 + player.getRandom().nextInt(4);
            for (int i = 0; i < count; i++) {
                target.spawnAtLocation(new ItemStack(Items.GOLD_INGOT));
            }
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (effectInstance.is(FargoSoulMobEffectRegister.Midas)) {
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulInfoData.getSoulInfo(target, FargoSoulSoulInfoRegister.GOLD_SOUL_INFO);
        if (info != null && info.active) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public void tick(Player player) {
        Info info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.GOLD_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulInfoData.putSoulInfo(player, info);
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
    public @Nullable ResourceLocation keyPressed(Player player, SoulKey key) {
        if (key == SoulKey.Gold) {
            return FargoSoul.rl("gold_stasis");
        }
        return super.keyPressed(player, key);
    }

    @Override
    public void keyHandle(Player player, ResourceLocation location) {
        if (location.equals(FargoSoul.rl("gold_stasis"))) {
            Info info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.GOLD_SOUL_INFO);
            if (info == null) {
                info = new Info();
                SoulInfoData.putSoulInfo(player, info);
            }
            if (info.cd <= 0) {
                info.cd = 2400;
                info.invincible = 120;
                info.active = true;
            }
        }
    }

    public static final class Info extends SoulInfo {

        public boolean active = false;
        public int invincible = 0;
        public int cd = 0;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.GOLD_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!SoulItemData.isEquipped(living, FargoSoulItemRegister.GoldSoulItem)) {
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

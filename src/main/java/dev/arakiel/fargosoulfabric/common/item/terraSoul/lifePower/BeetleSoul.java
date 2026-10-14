package dev.arakiel.fargosoulfabric.common.item.terraSoul.lifePower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BeetleSoul extends SoulItem {

    public BeetleSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canFly(Player player) {
        return true;
    }

    @Override
    public void tick(Player player) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.BEETLE_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        if (player.tickCount % 140 == 0 && info.endurance < 3) {
            info.endurance++;
        }
        if (player.tickCount % 20 == 0 && info.might > 0) {
            info.might--;
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(attacker, SoulStateRegister.BEETLE_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(attacker, info);
        }
        info.might = Math.min(3, info.might + 1);
        if (info.might > 0) {
            modifiers.add(new ValueModifier(0.3f * info.might, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(target, SoulStateRegister.BEETLE_SOUL_INFO);
        if (info != null) {
            if (info.endurance > 0 && target.tickCount - info.lastHurtTick >= 40) {
                info.endurance--;
                info.lastHurtTick = target.tickCount;
            }
            if (info.endurance > 0) {
                modifiers.add(new ValueModifier(-0.15f * info.endurance, ValueOperation.ADD_MULTIPLIED_TOTAL));
            }
        }
    }

    public static final class Info extends SoulState {

        public int endurance = 0;
        public int might = 0;
        public int lastHurtTick = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.BEETLE_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.BeetleSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putInt("endurance", endurance);
            tag.putInt("might", might);
            tag.putInt("lastHurtTick", lastHurtTick);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            endurance = tag.getInt("endurance");
            might = tag.getInt("might");
            lastHurtTick = tag.getInt("lastHurtTick");
        }
    }
}
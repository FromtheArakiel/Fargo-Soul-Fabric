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
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TinSoul extends SoulItem {

    public TinSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void criticalHit(Player player, LivingEntity target, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.TIN_SOUL_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(player, soulInfo);
        }
        modifiers.add(new ValueModifier(0.2f + soulInfo.extraCritDamage * 0.01f, ValueOperation.ADD_MULTIPLIED_BASE));
        soulInfo.extraCritDamage = Math.min(soulInfo.extraCritDamage + 10, soulInfo.maxExtraCritDamage);
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(target, SoulStateRegister.TIN_SOUL_INFO);
        if (soulInfo != null) {
            soulInfo.extraCritDamage = soulInfo.extraCritDamage / 2;
        }
    }

    public static final class Info extends SoulState {

        public float extraCritDamage = 0;
        public float maxExtraCritDamage = 80;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.TIN_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.TinSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("extraCritDamage", extraCritDamage);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            extraCritDamage = tag.getFloat("extraCritDamage");
        }
    }
}

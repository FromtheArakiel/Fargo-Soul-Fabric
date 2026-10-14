package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FSFItemRegister.*;

public class WillPower extends SoulItem {

    public WillPower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(GoldSoulItem, PlatinumSoulItem, GladiatorSoulItem, RedRidingSoulItem, ValhallaKnightSoulItem);
    }

    @Override
    public void healAmount(Player player, float amount, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(0.25f, ValueOperation.ADD_MULTIPLIED_BASE));
    }

    @Override
    public void kill(Player player, LivingEntity target, DamageSource source) {
        if (player.getRandom().nextFloat() < 0.4f) {
            Info info = SoulStates.getSoulState(player, SoulStateRegister.WILL_POWER_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(player, info);
            }
            info.dropScale = 16;
        }
    }

    @Override
    public float dropFromLootTableScale(Player attacker, LivingEntity target, float scale) {
        Info info = SoulStates.getSoulState(attacker, SoulStateRegister.WILL_POWER_INFO);
        if (info != null && info.dropScale > 1) {
            scale *= info.dropScale;
            info.dropScale = 1;
        }
        return scale;
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (target.hasEffect(FSFMobEffectRegister.Midas)) {
            modifiers.add(new ValueModifier(0.8f, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    public static final class Info extends SoulState {

        public float dropScale = 1;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.WILL_POWER_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.WillPowerItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("dropScale", dropScale);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            dropScale = tag.getFloat("dropScale");
        }
    }
}
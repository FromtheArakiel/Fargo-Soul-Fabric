package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NinjaSoul extends SoulItem {

    public NinjaSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.NINJA_SOUL_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(player, soulInfo);
        }
        soulInfo.ambushPower = Math.min(soulInfo.ambushPower + 1, soulInfo.maxAmbushPower);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(attacker, SoulStateRegister.NINJA_SOUL_INFO);
        if (soulInfo != null) {
            modifiers.add(new ValueModifier(soulInfo.ambushPower * 0.01f, ValueOperation.ADD_MULTIPLIED_BASE));
            soulInfo.ambushPower = 0;
        }
    }

    public static final class Info extends SoulState {

        public float ambushPower = 0;
        public float maxAmbushPower = 600;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.NINJA_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.NinjaSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("ambushPower", ambushPower);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            ambushPower = tag.getFloat("ambushPower");
        }
    }
}

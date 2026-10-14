package dev.arakiel.fargosoulfabric.common.item.terraSoul.forestPower;

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

public class RoseWoodSoul extends SoulItem {

    public RoseWoodSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSprint(Player player) {
        return true;
    }

    @Override
    public void sprintClient(Player player, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(0.25f, ValueOperation.ADD_MULTIPLIED_BASE));
    }

    @Override
    public void sprintServer(Player player) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.ROSE_WOOD_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        info.sprintTime = 20;
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(target, SoulStateRegister.ROSE_WOOD_SOUL_INFO);
        if (info != null && info.sprintTime > 0) {
            modifiers.add(new ValueModifier(-0.2f, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    public static final class Info extends SoulState {

        public int sprintTime = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.ROSE_WOOD_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.RoseWoodSoulItem)) {
                if (sprintTime > 0) {
                    sprintTime--;
                }
            } else {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putInt("sprintTime", sprintTime);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            sprintTime = tag.getInt("sprintTime");
        }
    }
}
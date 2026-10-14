package dev.arakiel.fargosoulfabric.common.item.terraSoul.forestPower;

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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WoodSoul extends SoulItem {

    public WoodSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void kill(Player player, LivingEntity target, DamageSource source) {
        if (target instanceof Raider) {
            Info info = SoulStates.getSoulState(player, SoulStateRegister.WOOD_SOUL_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(player, info);
            }
            info.discount = Math.min(30, info.discount + 0.25f + player.getRandom().nextFloat() * 0.25f);
        }
    }

    @Override
    public void updateSpecialPrices(Player player, Villager villager, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.WOOD_SOUL_INFO);
        float extra = info != null ? info.discount * 0.01f : 0f;
        modifiers.add(new ValueModifier(-(0.2f + extra), ValueOperation.ADD_MULTIPLIED_TOTAL));
    }

    public static final class Info extends SoulState {

        public float discount = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.WOOD_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.WoodSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("discount", discount);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            discount = tag.getFloat("discount");
        }
    }
}
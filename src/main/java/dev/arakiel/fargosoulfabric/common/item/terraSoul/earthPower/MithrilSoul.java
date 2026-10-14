package dev.arakiel.fargosoulfabric.common.item.terraSoul.earthPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MithrilSoul extends SoulItem {

    public MithrilSoul(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        Info info = SoulStates.getSoulState(player, SoulStateRegister.MITHRIL_SOUL_INFO);
        float bonus = info != null ? info.bonus : 0;
        map.put(Attributes.ATTACK_SPEED, new AttributeModifier(FSF.rl("mithril_speed"), 0.3f + bonus, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return map;
    }

    @Override
    public void tick(Player player) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.MITHRIL_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        if (info.lastAttackTick != 0 && player.tickCount - info.lastAttackTick > 100) {
            info.bonus = 0;
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(attacker, SoulStateRegister.MITHRIL_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(attacker, info);
        }
        if (info.lastAttackTick != 0 && attacker.tickCount - info.lastAttackTick > 100) {
            info.bonus = 0;
        }
        info.bonus = Math.min(0.7f, info.bonus + 0.05f);
        info.lastAttackTick = attacker.tickCount;
    }

    public static final class Info extends SoulState {

        public float bonus = 0;
        public int lastAttackTick = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.MITHRIL_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.MithrilSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("bonus", bonus);
            tag.putInt("lastAttackTick", lastAttackTick);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            bonus = tag.getFloat("bonus");
            lastAttackTick = tag.getInt("lastAttackTick");
        }
    }
}
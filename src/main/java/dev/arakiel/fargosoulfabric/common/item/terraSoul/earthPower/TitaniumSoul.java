package dev.arakiel.fargosoulfabric.common.item.terraSoul.earthPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulState.SoulState;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TitaniumSoul extends SoulItem {

    public TitaniumSoul(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        Info info = SoulStates.getSoulState(player, SoulStateRegister.TITANIUM_SOUL_INFO);
        if (info != null && info.guard > 300) {
            map.put(Attributes.MAX_HEALTH, new AttributeModifier(FSF.rl("titanium_health"), 0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            map.put(Attributes.ARMOR, new AttributeModifier(FSF.rl("titanium_armor"), 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
        return map;
    }

    @Override
    public void tick(Player player) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.TITANIUM_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        if (info.guard > 0) {
            info.guard = Math.max(0, info.guard - 0.25f);
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(-0.15f, ValueOperation.ADD_MULTIPLIED_TOTAL));
        Info info = SoulStates.getSoulState(target, SoulStateRegister.TITANIUM_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(target, info);
        }
        info.guard = Math.min(info.maxGuard, info.guard + container.getOriginalDamage() * 0.2f);
        float ratio = info.guard / info.maxGuard;
        float reduce = 0.35f * ratio;
        if (target.getHealth() < target.getMaxHealth() * 0.5f) {
            reduce *= 2;
        }
        if (reduce > 0) {
            modifiers.add(new ValueModifier(-reduce, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    public static final class Info extends SoulState {

        public float guard = 0;
        public float maxGuard = 600;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.TITANIUM_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.TitaniumSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("guard", guard);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            guard = tag.getFloat("guard");
        }
    }
}
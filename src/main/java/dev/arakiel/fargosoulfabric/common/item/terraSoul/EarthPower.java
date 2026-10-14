package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
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
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister.*;

public class EarthPower extends SoulItem {

    public EarthPower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(CobaltSoulItem, PalladiumSoulItem, MithrilSoulItem, OrichalcumSoulItem, AdamantiteSoulItem, TitaniumSoulItem);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        Info info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.EARTH_POWER_INFO);
        float ratio = info != null ? info.energy / info.maxEnergy : 0;
        if (ratio > 0) {
            map.put(Attributes.ATTACK_SPEED, new AttributeModifier(FargoSoul.rl("earth_speed"), ratio, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            map.put(Attributes.ARMOR, new AttributeModifier(FargoSoul.rl("earth_armor"), ratio, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            map.put(FargoSoulAttributeRegister.HealthRegen, new AttributeModifier(FargoSoul.rl("earth_regen"), ratio * 5, AttributeModifier.Operation.ADD_VALUE));
        }
        return map;
    }

    @Override
    public void tick(Player player) {
        Info info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.EARTH_POWER_INFO);
        if (info == null) {
            info = new Info();
            SoulInfoData.putSoulInfo(player, info);
        }
        if (player.tickCount % 20 == 0 && player.tickCount - info.lastAttackTick > 60) {
            info.energy = Math.min(info.maxEnergy, info.energy + 20);
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulInfoData.getSoulInfo(attacker, FargoSoulSoulInfoRegister.EARTH_POWER_INFO);
        if (info == null) {
            info = new Info();
            SoulInfoData.putSoulInfo(attacker, info);
        }
        float ratio = info.energy / info.maxEnergy;
        if (ratio > 0) {
            modifiers.add(new ValueModifier(ratio, ValueOperation.ADD_MULTIPLIED_BASE));
        }
        info.energy = Math.max(0, info.energy - 10);
        info.lastAttackTick = attacker.tickCount;
    }

    public static final class Info extends SoulInfo {

        public float energy = 0;
        public float maxEnergy = 1000;
        public int lastAttackTick = 0;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.EARTH_POWER_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!SoulItemData.isEquipped(living, FargoSoulItemRegister.EarthPowerItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("energy", energy);
            tag.putInt("lastAttackTick", lastAttackTick);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            energy = tag.getFloat("energy");
            lastAttackTick = tag.getInt("lastAttackTick");
        }
    }
}
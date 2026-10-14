package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FSFItemRegister.*;

public class LifePower extends SoulItem {

    public LifePower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(BeeSoulItem, BeetleSoulItem, PumpkinSoulItem, SpiderSoulItem, TurtleSoulItem);
    }

    @Override
    public boolean canFly(Player player) {
        return true;
    }

    @Override
    public void getMaxFlyTime(Player player, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(1.5f, ValueOperation.ADD_MULTIPLIED_BASE));
    }

    @Override
    public void eat(Player player, ItemStack food) {
        if (food.is(Items.HONEY_BOTTLE)) {
            player.addEffect(new MobEffectInstance(FSFMobEffectRegister.Ambrosia, 200));
        }
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        if (player.hasEffect(FSFMobEffectRegister.Ambrosia)) {
            map.put(FSFAttributeRegister.SummonDamage, new AttributeModifier(FSF.rl("life_ambrosia"), 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
            map.put(FSFAttributeRegister.HealthRegen, new AttributeModifier(FSF.rl("life_regen"), 5, AttributeModifier.Operation.ADD_VALUE));
        }
        return map;
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (attacker.hasEffect(FSFMobEffectRegister.Ambrosia)) {
            modifiers.add(new ValueModifier(0.3f, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (attacker instanceof LivingEntity living) {
            Info info = SoulStates.getSoulState(target, SoulStateRegister.LIFE_POWER_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(target, info);
                living.hurt(target.damageSources().playerAttack(target), container.getOriginalDamage() * 5.0f);
            }
        }
    }

    public static final class Info extends SoulState {

        public int cooldown = 2;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.LIFE_POWER_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (--cooldown < 0) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putInt("cooldown", cooldown);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            cooldown = tag.getInt("cooldown");
        }
    }
}
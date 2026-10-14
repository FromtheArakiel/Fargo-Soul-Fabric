package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.entity.SnowBall;
import dev.arakiel.fargosoulfabric.common.entity.TrackingBlood;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FSFItemRegister.*;

public class ForestPower extends SoulItem {

    public ForestPower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(WoodSoulItem, PineWoodSoulItem, RoseWoodSoulItem, EbonyWoodSoulItem, ShadowWoodSoulItem, PalmWoodSoulItem, PearlWoodSoulItem);
    }

    @Override
    public void tick(Player player) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.FOREST_POWER_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        if (player.tickCount % 40 == 0) {
            Vec3 eye = player.getEyePosition();
            List<LivingEntity> targets = SoulTargets.get(player).getEntitiesInRadius(player.getBoundingBox().getCenter(), 12, null);
            if (!targets.isEmpty()) {
                LivingEntity target = targets.get(player.getRandom().nextInt(targets.size()));
                Vec3 dir = target.getBoundingBox().getCenter().subtract(eye).normalize();
                SnowBall ball = new SnowBall();
                ball.setDamageSourceSupplier(owner -> owner.damageSources().playerAttack(owner));
                ball.setPos(eye);
                ball.setVelocity(dir);
                ball.setDamage(6);
                ball.join(player);
            }
        }
        if (player.tickCount % 20 == 0) {
            List<LivingEntity> enemies = SoulTargets.get(player).getEntitiesInRadius(player.getBoundingBox().getCenter(), 12, null);
            for (LivingEntity enemy : enemies) {
                enemy.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
                enemy.addEffect(new MobEffectInstance(FSFMobEffectRegister.Bleeding, 200));
            }
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (target.hasEffect(MobEffects.WITHER)) {
            modifiers.add(new ValueModifier(8, ValueOperation.ADD_VALUE));
        }
        if (target.hasEffect(FSFMobEffectRegister.Bleeding)) {
            Info info = SoulStates.getSoulState(attacker, SoulStateRegister.FOREST_POWER_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(attacker, info);
            }
            if (info.cooldown <= 0) {
                info.cooldown = 20;
                Vec3 from = target.getBoundingBox().getCenter();
                RandomSource random = attacker.getRandom();
                int count = 3 + random.nextInt(4);
                for (int i = 0; i < count; i++) {
                    TrackingBlood blood = new TrackingBlood();
                    blood.setPos(from);
                    blood.setVelocity(from.offsetRandom(random, 1).subtract(from).normalize().scale(1.5f));
                    blood.join(attacker);
                }
            }
        }
    }

    @Override
    public void criticalHit(Player player, LivingEntity target, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.FOREST_POWER_INFO);
        if (info != null && info.burstTime > 0) {
            modifiers.add(new ValueModifier(0.4f, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void pickup(Player player, ItemStack itemStack) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.FOREST_POWER_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        info.burstTime = 160;
    }

    @Override
    public void updateSpecialPrices(Player player, Villager villager, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(-0.5f, ValueOperation.ADD_MULTIPLIED_TOTAL));
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;
        public int burstTime = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.FOREST_POWER_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.ForestPowerItem)) {
                setRemove(true);
            }
            if (cooldown > 0) {
                cooldown--;
            }
            if (burstTime > 0) {
                burstTime--;
            }
            if (burstTime <= 0 && cooldown <= 0) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putInt("cooldown", cooldown);
            tag.putInt("burstTime", burstTime);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            cooldown = tag.getInt("cooldown");
            burstTime = tag.getInt("burstTime");
        }
    }
}

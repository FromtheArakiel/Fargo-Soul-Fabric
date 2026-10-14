package dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower;

import dev.arakiel.fargosoulfabric.common.FSFConfig;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.SoulKey;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulState.SoulState;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import dev.arakiel.fargosoulfabric.register.*;
import dev.arakiel.fargosoulfabric.common.FSFPlayerSummons;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import dev.arakiel.fargosoulfabric.common.summon.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.particle.generic.GenericParticleBuilder;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class BlazeSoul extends SoulItem {

    public BlazeSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSprint(Player player) {
        Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.BLAZE_SOUL_INFO);
        if (soulInfo != null && soulInfo.blazePower > 300) {
            return true;
        }
        return super.canSprint(player);
    }

    @Override
    public void sprintClient(Player player, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.BLAZE_SOUL_INFO);
        if (soulInfo != null && soulInfo.blazePower > 600) {
            modifiers.add(new ValueModifier(1f, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void sprintCollision(Player player, List<IEntityCollision.HitContext> list, Set<LivingEntity> hits) {
        for (IEntityCollision.HitContext context : list) {
            LivingEntity living = context.entity();
            if (hits.add(living)) {
                float damageAmount = player.getMaxHealth() * 0.12f;
                Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.BLAZE_SOUL_INFO);
                if (soulInfo != null && soulInfo.blazePower > 600) {
                    damageAmount *= 1.2f;
                }
                living.igniteForTicks(200);
                Invulnerability.attack(living)
                        .attacker(player.getUUID())
                        .damageSource(player.damageSources().inFire())
                        .damageAmount(damageAmount)
                        .apply();
                Vec3 point = context.hitPoint();
                fireParticle(player.level(), point, living.getRandom(), 40);
            }
        }
    }

    @Override
    public void tick(Player living) {
        Info soulInfo = SoulStates.getSoulState(living, SoulStateRegister.BLAZE_SOUL_INFO);
        if (soulInfo != null) {
            if (living.tickCount % 20 == 0) {
                if (soulInfo.blazePower >= 0) {
                    living.heal(living.getMaxHealth() * 0.01f);
                }
                if (soulInfo.blazePower > 900) {
                    List<LivingEntity> list = SoulTargets.get(living).getEntitiesInRadius(living.getBoundingBox().getCenter(), 4, null);
                    for (LivingEntity livingEntity : list) {
                        livingEntity.igniteForTicks(100);
                        Invulnerability.attack(livingEntity)
                                .attacker(living.getUUID())
                                .damageSource(living.damageSources().inFire())
                                .damageAmount(living.getMaxHealth() * 0.04f)
                                .apply();
                    }
                }
                if (soulInfo.power) {
                    soulInfo.blazePower -= 20;
                }
            }
            if (soulInfo.blazePower > 300 && !FSFPlayerSummons.get(living).getSummons().get(SummonTypeRegister.SPRINT).isEmpty()) {
                ParticleHelper.create(living.level())
                        .generic(GenericParticleBuilder.create()
                                         .centerColor(0xFF691F)
                                         .edgeColor(0xFE4A00)
                                         .lifetime(5)
                                         .lifetimeRandom(10)
                                         .spin(0.3f)
                                         .spinRandom(0.05F)
                                         .friction(0.75F)
                                         .scale(0.015f)
                                         .scaleRandom(0.005f)
                        )
                        .pos(living.position())
                        .offset(0.15)
                        .count(20)
                        .spread(Math.TAU)
                        .emit();
            }
            if (soulInfo.blazePower <= 0) {
                soulInfo.setRemove(true);
            }
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(attacker, SoulStateRegister.BLAZE_SOUL_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(attacker, soulInfo);
        }
        float originalDamage = container.getOriginalDamage();
        if (!soulInfo.power) {
            soulInfo.blazePower = Math.min(soulInfo.blazePower + originalDamage * 0.15f, soulInfo.maxBlazePower);
        } else {
            if (soulInfo.blazePower > 0) {
                soulInfo.blazePower -= 50;
                modifiers.add(new ValueModifier(3.75f, ValueOperation.ADD_MULTIPLIED_BASE));
                fireParticle(attacker.level(), target.getBoundingBox().getCenter(), target.getRandom(), 80);
            }
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(target, SoulStateRegister.BLAZE_SOUL_INFO);
        if (soulInfo != null) {
            float scale = -((soulInfo.blazePower / soulInfo.maxBlazePower) * (soulInfo.power ? 0.8f : 0.4f));
            modifiers.add(new ValueModifier(scale, ValueOperation.ADD_MULTIPLIED_TOTAL));
            modifiers.add(new ValueModifier(-target.getMaxHealth() * 0.15f, ValueOperation.ADD_VALUE));
        }
    }

    @Override
    public @Nullable ResourceLocation keyAction(Player player, SoulKey key) {
        if (key == SoulKey.Blaze) {
            return FSF.rl("blaze_soul_info_power");
        }
        return super.keyAction(player, key);
    }

    @Override
    public void handleKeyAction(Player player, ResourceLocation location) {
        if (location.equals(FSF.rl("blaze_soul_info_power"))) {
            Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.BLAZE_SOUL_INFO);
            if (soulInfo != null && soulInfo.blazePower > 900) {
                soulInfo.power = true;
                // 日耀之力: a ring of flame around the player that just lit up.
                FSFEffects.aura(player.level(), player, ParticleTypes.FLAME, 60, 0.7D, 0.08D);
            }
        }
    }

    private static void fireParticle(Level level, Vec3 point, RandomSource randomSource,int count) {
        ParticleHelper.create(level)
                .generic(GenericParticleBuilder.create()
                                 .centerColor(0xFF691F)
                                 .edgeColor(0xFE4A00)
                                 .lifetime(5)
                                 .lifetimeRandom(10)
                                 .spin(0.3f)
                                 .spinRandom(0.05F)
                                 .friction(0.75F)
                                 .scale(0.025f)
                                 .scaleRandom(0.005f)
                )
                .pos(point)
                .offset(0.15)
                .velocity(point.offsetRandom(randomSource, 1).subtract(point).normalize())
                .count(count)
                .speed(0.35)
                .spread(Math.TAU)
                .emit();
    }

    public static final class Info extends SoulState {

        public boolean power = false;
        public float blazePower = 0;
        public float maxBlazePower = 1000;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.BLAZE_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.BlazeSoulItem)) {
                setRemove(true);
            }
            if (blazePower <= 0) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putBoolean("power", power);
            tag.putFloat("blazePower", blazePower);
            tag.putFloat("maxBlazePower", maxBlazePower);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            power = tag.getBoolean("power");
            blazePower = tag.getFloat("blazePower");
            maxBlazePower = tag.getFloat("maxBlazePower");
        }
    }
}

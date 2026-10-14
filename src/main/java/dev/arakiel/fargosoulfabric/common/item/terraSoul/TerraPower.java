package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.entity.LightningOrb;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.FSFPlayerSummons;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FSFItemRegister.*;

public class TerraPower extends SoulItem {

    public TerraPower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(CopperSoulItem, IronSoulItem, LeadSoulItem, ObsidianSoulItem, SilverSoulItem, TinSoulItem, TungstenSoulItem);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(attacker, SoulStateRegister.TERRA_POWER_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(attacker, soulInfo);
        }
        if (soulInfo.cooldown <= 0) {
            int orbCount = FSFPlayerSummons.get(attacker).getSummons().get(SummonTypeRegister.LIGHTNING_ORB).size();
            float chance = 0.2f + Math.min(orbCount, 5) * 0.08f;
            RandomSource random = attacker.getRandom();
            if (random.nextFloat() < chance) {
                soulInfo.cooldown = (int) (20 * (1 - Math.min(orbCount, 5) * 0.12f));
                int count = 3 + random.nextInt(4);
                Vec3 eye = attacker.getEyePosition();
                for (int i = 0; i < count; i++) {
                    Vec3 dir = eye.add(target.getBoundingBox().getCenter().subtract(eye)).offsetRandom(random, 0.5f).subtract(eye).normalize();
                    LightningOrb orb = new LightningOrb();
                    orb.setDamageSourceSupplier(owner -> owner.damageSources().lightningBolt());
                    orb.setPos(eye);
                    orb.setVelocity(dir);
                    float bonus = attacker.hasEffect(FSFMobEffectRegister.TerraResonance) ? 1.8f : 1.0f;
                    orb.setDamage(4 * bonus);
                    orb.join(attacker);
                }
            }
        }
        if (attacker.getRandom().nextFloat() < 0.05f) {
            modifiers.add(new ValueModifier(1, ValueOperation.ADD_MULTIPLIED_BASE));
            attacker.addEffect(new MobEffectInstance(FSFMobEffectRegister.TerraResonance, 100));
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(-(4 + target.getMaxHealth() * 0.02f), ValueOperation.ADD_VALUE));
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.TERRA_POWER_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.TerraPowerItem)) {
                if (cooldown > 0) {
                    cooldown--;
                }
            } else {
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

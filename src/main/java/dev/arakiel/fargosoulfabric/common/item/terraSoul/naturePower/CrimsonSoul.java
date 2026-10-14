package dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;
import dev.arakiel.fargosoulfabric.common.FSFConfig;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.entity.TrackingBlood;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulState.SoulState;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CrimsonSoul extends SoulItem {

    public CrimsonSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        if (player.tickCount % 20 == 0) {
            List<LivingEntity> enemies = SoulTargets.get(player).getEntitiesInRadius(player.getBoundingBox().getCenter(), 4, null);
            float amount = player.getMaxHealth() * (enemies.isEmpty() ? 0.03f : 0.01f);
            player.heal(amount);
           if (player.tickCount % 200 == 0 && FSFConfig.auraRings()) {
               FSFEffects.ring(player.level(), player.getBoundingBox().getCenter(), 4.0D, ParticleTypes.CRIMSON_SPORE, 48, 0.02D);
           }
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(attacker, SoulStateRegister.CRIMSON_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(attacker, info);
        }
        if (info.cooldown <= 0) {
            info.cooldown = 5;
            Vec3 from = target.getBoundingBox().getCenter();
            RandomSource random = attacker.getRandom();
            int count = 2 + random.nextInt(3);
            for (int i = 0; i < count; i++) {
                TrackingBlood blood = new TrackingBlood();
                blood.setPos(from);
                blood.setVelocity(from.offsetRandom(random, 1).subtract(from).normalize().scale(1.5f));
                blood.join(attacker);
            }
        }
    }

    @Override
    public void kill(Player player, LivingEntity target, DamageSource source) {
        player.heal(target.getMaxHealth() * 0.2f);
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.CRIMSON_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.CrimsonSoulItem)) {
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

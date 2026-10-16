package dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

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
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RainCloudSoul extends SoulItem {

    public RainCloudSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (container.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        } else if (attacker instanceof LivingEntity living) {
            Info info = SoulStates.getSoulState(target, SoulStateRegister.RAIN_CLOUD_SOUL_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(target, info);
            }
            if (info.cooldown <= 0 && target.getRandom().nextFloat() < 0.3f) {
                info.cooldown = 40;
                Vec3 pos = living.getBoundingBox().getCenter();
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(target.level());
                if (bolt != null) {
                    bolt.moveTo(pos);
                    target.level().addFreshEntity(bolt);
                }
            }
        }
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.RAIN_CLOUD_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.RainCloudSoulItem)) {
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

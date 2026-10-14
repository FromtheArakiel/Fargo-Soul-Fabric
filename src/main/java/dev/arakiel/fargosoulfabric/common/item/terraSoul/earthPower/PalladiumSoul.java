package dev.arakiel.fargosoulfabric.common.item.terraSoul.earthPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.entity.BloodDrop;
import dev.arakiel.fargosoulfabric.common.entity.TrackingBlood;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PalladiumSoul extends SoulItem {

    public PalladiumSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(attacker, SoulStateRegister.PALLADIUM_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(attacker, info);
        }
        if (info.attackCd <= 0 && attacker.getRandom().nextFloat() < 0.2f) {
            info.attackCd = 40;
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

    @Override
    public void tick(Player player) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.PALLADIUM_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        if (info.attackCd > 0) {
            info.attackCd--;
        }
        if (info.surgeTime > 0) {
            info.surgeTime--;
        }
        if (info.lifePower >= info.maxLifePower) {
            info.lifePower = 0;
            info.surgeTime = 200;
            player.heal(player.getMaxHealth());
        }
    }

    @Override
    public void healAmount(Player player, float amount, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(player, SoulStateRegister.PALLADIUM_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(player, info);
        }
        info.lifePower = Math.min(info.maxLifePower, info.lifePower + amount * 0.25f);
        if (info.surgeTime > 0 && amount > 0 && !player.level().isClientSide()) {
            float overflow = player.getHealth() + amount - player.getMaxHealth();
            if (overflow > 0) {
                Vec3 from = player.getBoundingBox().getCenter().add(0, 0.5, 0);
                List<LivingEntity> others = SoulTargets.get(player).getEntitiesInRadius(from, 16, null);
                if (!others.isEmpty()) {
                    RandomSource random = player.getRandom();
                    LivingEntity chase = others.get(random.nextInt(others.size()));
                    BloodDrop drop = new BloodDrop();
                    drop.setDamageSourceSupplier(owner -> owner.damageSources().playerAttack(owner));
                    drop.setPos(from);
                    drop.setVelocity(from.offsetRandom(random, 1).subtract(from).normalize().scale(0.5f));
                    drop.setDamage(overflow);
                    drop.setChaseTarget(chase);
                    drop.join(player);
                }
            }
        }
    }

    public static final class Info extends SoulState {

        public float lifePower = 0;
        public float maxLifePower = 200;
        public int surgeTime = 0;
        public int attackCd = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.PALLADIUM_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.PalladiumSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("lifePower", lifePower);
            tag.putInt("surgeTime", surgeTime);
            tag.putInt("attackCd", attackCd);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            lifePower = tag.getFloat("lifePower");
            surgeTime = tag.getInt("surgeTime");
            attackCd = tag.getInt("attackCd");
        }
    }
}

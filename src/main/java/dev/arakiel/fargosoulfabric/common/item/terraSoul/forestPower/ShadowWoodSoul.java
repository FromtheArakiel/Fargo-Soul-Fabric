package dev.arakiel.fargosoulfabric.common.item.terraSoul.forestPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.entity.BloodDrop;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ShadowWoodSoul extends SoulItem {

    public ShadowWoodSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        if (player.tickCount % 20 == 0) {
            List<LivingEntity> enemies = SoulTargets.get(player).getEntitiesInRadius(player.getBoundingBox().getCenter(), 6, null);
            for (LivingEntity enemy : enemies) {
                enemy.addEffect(new MobEffectInstance(FSFMobEffectRegister.Hemorrhage, 120));
            }
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (target.hasEffect(FSFMobEffectRegister.Hemorrhage)) {
            Info info = SoulStates.getSoulState(attacker, SoulStateRegister.SHADOW_WOOD_SOUL_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(attacker, info);
            }
            if (info.cooldown <= 0) {
                info.cooldown = 10;
                Vec3 from = target.getBoundingBox().getCenter();
                List<LivingEntity> others = SoulTargets.get(attacker).getEntitiesInRadius(target.getBoundingBox().getCenter(), 8, other -> other != target);
                if (!others.isEmpty()) {
                    RandomSource random = attacker.getRandom();
                    int count = 2 + random.nextInt(2);
                    for (int i = 0; i < count; i++) {
                        LivingEntity chase = others.get(random.nextInt(others.size()));
                        BloodDrop drop = new BloodDrop();
                        drop.setDamageSourceSupplier(owner -> owner.damageSources().playerAttack(owner));
                        drop.setPos(from);
                        drop.setVelocity(from.offsetRandom(random, 1).subtract(from).normalize().scale(0.5f));
                        drop.setDamage(2);
                        drop.setChaseTarget(chase);
                        drop.join(attacker);
                    }
                }
            }
        }
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.SHADOW_WOOD_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.ShadowWoodSoulItem)) {
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

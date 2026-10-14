package dev.arakiel.fargosoulfabric.common.item.terraSoul.earthPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.entity.Petal;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OrichalcumSoul extends SoulItem {

    public OrichalcumSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulInfoData.getSoulInfo(attacker, FargoSoulSoulInfoRegister.ORICHALCUM_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulInfoData.putSoulInfo(attacker, info);
        }
        if (info.cooldown <= 0 && container.getOriginalDamage() > 1) {
            info.cooldown = 5;
            RandomSource random = attacker.getRandom();
            int count = 3 + random.nextInt(4);
            for (int i = 0; i < count; i++) {
                Vec3 from = target.getBoundingBox().getCenter().offsetRandom(random, (float) target.getBoundingBox().getSize());
                Vec3 dir = from.offsetRandom(random, 1).subtract(from).normalize().scale(0.5 + random.nextDouble() * 0.5);
                Petal petal = new Petal();
                petal.setDamageSourceSupplier(owner -> owner.damageSources().playerAttack(owner));
                petal.setPos(from);
                petal.setVelocity(dir);
                petal.setDamage(container.getOriginalDamage() * 0.5f);
                petal.setChaseTarget(target);
                petal.join(attacker);
            }
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (effectInstance.is(FargoSoulMobEffectRegister.OrichalcumPoisoning)) {
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }

    public static final class Info extends SoulInfo {

        public int cooldown = 0;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.ORICHALCUM_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (SoulItemData.isEquipped(living, FargoSoulItemRegister.OrichalcumSoulItem)) {
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

package dev.arakiel.fargosoulfabric.common.item.terraSoul.earthPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulState.SoulState;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CobaltSoul extends SoulItem {

    public CobaltSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info info = SoulStates.getSoulState(target, SoulStateRegister.COBALT_SOUL_INFO);
        if (info == null) {
            info = new Info();
            SoulStates.putSoulState(target, info);
        }
        if (info.cooldown <= 0) {
            info.cooldown = 60;
            List<LivingEntity> enemies = SoulTargets.get(target).getEntitiesInRadius(target.getBoundingBox().getCenter(), 6, null);
            for (LivingEntity enemy : enemies) {
                enemy.addEffect(new MobEffectInstance(FSFMobEffectRegister.Oiled, 50));
            }
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (effectInstance.is(FSFMobEffectRegister.Oiled)) {
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.COBALT_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.CobaltSoulItem)) {
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
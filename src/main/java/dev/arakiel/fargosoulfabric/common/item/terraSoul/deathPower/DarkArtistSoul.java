package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

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
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DarkArtistSoul extends SoulItem {

    public DarkArtistSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (target.hasEffect(FSFMobEffectRegister.ShadowFire)) {
            modifiers.add(new ValueModifier(0.4f, ValueOperation.ADD_MULTIPLIED_BASE));
            Info soulInfo = SoulStates.getSoulState(attacker, SoulStateRegister.DARK_ARTIST_SOUL_INFO);
            if (soulInfo == null) {
                soulInfo = new Info();
                SoulStates.putSoulState(attacker, soulInfo);
            }
            soulInfo.darkPower = Math.min(soulInfo.darkPower + container.getOriginalDamage() * 0.2f, soulInfo.maxDarkPower);
            if (soulInfo.darkPower >= soulInfo.maxDarkPower){
                soulInfo.darkPower = 0;
                attacker.addEffect(new MobEffectInstance(FSFMobEffectRegister.ShadowGift, 400));
               FSFEffects.aura(attacker.level(), attacker, ParticleTypes.SOUL, 40, 0.6D, 0.05D);
            }
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (player.hasEffect(FSFMobEffectRegister.ShadowGift) && effectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL && player.getRandom().nextFloat() < 0.2f) {
            player.heal(player.getMaxHealth() * 0.12f);
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }

    public static final class Info extends SoulState {

        public float darkPower = 0;
        public float maxDarkPower = 400;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.DARK_ARTIST_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.DarkArtistSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("darkPower", darkPower);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            darkPower = tag.getFloat("darkPower");
        }
    }
}

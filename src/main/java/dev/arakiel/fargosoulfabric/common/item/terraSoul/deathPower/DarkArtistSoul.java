package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
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
        if (target.hasEffect(FargoSoulMobEffectRegister.ShadowFire)) {
            modifiers.add(new ValueModifier(0.4f, ValueOperation.ADD_MULTIPLIED_BASE));
            Info soulInfo = SoulInfoData.getSoulInfo(attacker, FargoSoulSoulInfoRegister.DARK_ARTIST_SOUL_INFO);
            if (soulInfo == null) {
                soulInfo = new Info();
                SoulInfoData.putSoulInfo(attacker, soulInfo);
            }
            soulInfo.darkPower = Math.min(soulInfo.darkPower + container.getOriginalDamage() * 0.2f, soulInfo.maxDarkPower);
            if (soulInfo.darkPower >= soulInfo.maxDarkPower){
                soulInfo.darkPower = 0;
                attacker.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.ShadowGift, 400));
            }
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (player.hasEffect(FargoSoulMobEffectRegister.ShadowGift) && effectInstance.getEffect().value().getCategory() == MobEffectCategory.HARMFUL && player.getRandom().nextFloat() < 0.2f) {
            player.heal(player.getMaxHealth() * 0.12f);
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }

    public static final class Info extends SoulInfo {

        public float darkPower = 0;
        public float maxDarkPower = 400;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.DARK_ARTIST_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!SoulItemData.isEquipped(living, FargoSoulItemRegister.DarkArtistSoulItem)) {
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

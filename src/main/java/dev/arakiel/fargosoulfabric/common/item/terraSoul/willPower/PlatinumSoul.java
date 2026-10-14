package dev.arakiel.fargosoulfabric.common.item.terraSoul.willPower;

import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlatinumSoul extends SoulItem {

    public PlatinumSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void kill(Player player, LivingEntity target, DamageSource source) {
        if (player.getRandom().nextFloat() < 0.2f) {
            Info info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.PLATINUM_SOUL_INFO);
            if (info == null) {
                info = new Info();
                SoulInfoData.putSoulInfo(player, info);
            }
            info.dropScale = 5;
        }
    }

    @Override
    public float dropFromLootTableScale(Player attacker, LivingEntity target, float scale) {
        Info info = SoulInfoData.getSoulInfo(attacker, FargoSoulSoulInfoRegister.PLATINUM_SOUL_INFO);
        if (info != null && info.dropScale > 1) {
            scale *= info.dropScale;
            info.dropScale = 1;
        }
        return scale;
    }

    public static final class Info extends SoulInfo {

        public float dropScale = 1;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.PLATINUM_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!SoulItemData.isEquipped(living, FargoSoulItemRegister.PlatinumSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("dropScale", dropScale);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            dropScale = tag.getFloat("dropScale");
        }
    }
}
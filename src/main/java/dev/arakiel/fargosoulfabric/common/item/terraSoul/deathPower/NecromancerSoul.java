package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class NecromancerSoul extends SoulItem {

    public NecromancerSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void kill(Player attacker, LivingEntity target, DamageSource source) {
        if (target.getType().is(EntityTypeTags.UNDEAD)) {
            Info soulInfo = SoulInfoData.getSoulInfo(attacker, FargoSoulSoulInfoRegister.NECROMANCER_SOUL_INFO);
            if (soulInfo == null) {
                soulInfo = new Info();
                SoulInfoData.putSoulInfo(attacker, soulInfo);
            }
            soulInfo.necromancerPower = Math.min(soulInfo.necromancerPower + 1, soulInfo.maxNecromancerPower);
            if (target.getType() == EntityType.SKELETON || target.getType() == EntityType.WITHER_SKELETON) {
                soulInfo.necromancerPower = Math.min(soulInfo.necromancerPower + 2, soulInfo.maxNecromancerPower);
                ItemStack skull = target.getType() == EntityType.SKELETON ? Items.SKELETON_SKULL.getDefaultInstance() : Items.WITHER_SKELETON_SKULL.getDefaultInstance();
                target.spawnAtLocation(skull);
            }
        }
    }

    @Override
    public boolean death(Player player, DamageSource source, boolean canceled) {
        if (!canceled) {
            Info soulInfo = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.NECROMANCER_SOUL_INFO);
            if (soulInfo != null && soulInfo.necromancerPower >= soulInfo.maxNecromancerPower) {
                soulInfo.necromancerPower = 0;
                player.setHealth(player.getMaxHealth() * 0.2f);
                return false;
            }
        }
        return super.death(player, source, canceled);
    }

    public static final class Info extends SoulInfo {

        public float necromancerPower = 0;
        public float maxNecromancerPower = 100;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.NECROMANCER_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!SoulItemData.isEquipped(living, FargoSoulItemRegister.NecromancerSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("necromancerPower", necromancerPower);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            necromancerPower = tag.getFloat("necromancerPower");
        }
    }
}

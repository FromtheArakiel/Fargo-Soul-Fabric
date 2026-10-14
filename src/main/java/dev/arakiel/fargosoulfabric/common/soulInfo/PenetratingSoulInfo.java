package dev.arakiel.fargosoulfabric.common.soulInfo;

import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class PenetratingSoulInfo extends SoulInfo {

    public boolean pending = false;
    public int penetrate = 0;
    public int cooldown = 0;

    @Override
    public SoulInfoType<? extends SoulInfo> getType() {
        return FargoSoulSoulInfoRegister.PENETRATING_SOUL_INFO;
    }

    @Override
    public void tick(LivingEntity living) {
        if (SoulItemData.isEquipped(living, FargoSoulItemRegister.PenetratingNinjaSoulItem)) {
            if (penetrate > 0) {
                penetrate--;
            }
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
        tag.putBoolean("pending", pending);
        tag.putInt("penetrate", penetrate);
        tag.putInt("cooldown", cooldown);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        pending = tag.getBoolean("pending");
        penetrate = tag.getInt("penetrate");
        cooldown = tag.getInt("cooldown");
    }
}

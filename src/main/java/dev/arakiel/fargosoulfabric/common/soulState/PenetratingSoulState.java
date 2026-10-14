package dev.arakiel.fargosoulfabric.common.soulState;

import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class PenetratingSoulState extends SoulState {

    public boolean pending = false;
    public int penetrate = 0;
    public int cooldown = 0;

    @Override
    public SoulStateType<? extends SoulState> getType() {
        return SoulStateRegister.PENETRATING_SOUL_INFO;
    }

    @Override
    public void tick(LivingEntity living) {
        if (PlayerSouls.isEquipped(living, FSFItemRegister.PenetratingNinjaSoulItem)) {
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

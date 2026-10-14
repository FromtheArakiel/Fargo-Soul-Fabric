package dev.arakiel.fargosoulfabric.common.soulInfo;

import dev.arakiel.fargosoulfabric.api.NbtSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class SoulInfo implements NbtSerializable<CompoundTag> {

    private boolean remove = false;

    public abstract SoulInfoType<? extends SoulInfo> getType();

    public void tick(LivingEntity living) {

    }

    public void onRemove(LivingEntity living) {

    }

    public void setRemove(boolean remove) {
        this.remove = remove;
    }

    public boolean isRemove() {
        return remove;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
    }
}

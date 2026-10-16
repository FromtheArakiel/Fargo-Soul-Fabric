package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.common.NbtSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

public interface NbtSerializable<T extends Tag> {

    T serializeNBT(HolderLookup.@NotNull Provider provider);

    void deserializeNBT(HolderLookup.@NotNull Provider provider, T tag);
}

package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.common.NbtSerializable;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

/**
 * A value that knows how to write itself to NBT.
 *
 * <p>The provider is handed in so that implementations can resolve registry entries while they
 * deserialize. The state the mod stores never needs it and simply passes it on.
 */
public interface NbtSerializable<T extends Tag> {

    T serializeNBT(HolderLookup.@NotNull Provider provider);

    void deserializeNBT(HolderLookup.@NotNull Provider provider, T tag);
}

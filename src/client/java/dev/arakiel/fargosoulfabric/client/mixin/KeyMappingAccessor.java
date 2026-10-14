package dev.arakiel.fargosoulfabric.client.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * The bound key of a key mapping.
 *
 * <p>Vanilla keeps it private, and the souls compare the key the player pressed with the key they
 * are bound to.
 */
@Mixin(KeyMapping.class)
public interface KeyMappingAccessor {

    @Accessor("key")
    InputConstants.Key getBoundKey();
}

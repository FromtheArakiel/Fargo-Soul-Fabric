package dev.arakiel.fargosoulfabric.client.register;

import com.mojang.blaze3d.platform.InputConstants;
import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.client.mixin.KeyMappingAccessor;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/** The key bindings the souls use. */
public final class FargoSoulKeyRegister {

    private static final Map<SoulKey, KeyMapping> BINDINGS = new EnumMap<>(SoulKey.class);

    private FargoSoulKeyRegister() {
    }

    public static void init() {
        if (!BINDINGS.isEmpty()) {
            return;
        }
        for (SoulKey soulKey : SoulKey.values()) {
            KeyMapping mapping = new KeyMapping(soulKey.displayName(), InputConstants.UNKNOWN.getValue(),
                    Component.translatable("key.categories.fargosoulfabric").getString());
            KeyBindingHelper.registerKeyBinding(mapping);
            BINDINGS.put(soulKey, mapping);
        }
    }

    /**
     * The soul key a raw key press belongs to.
     *
     * @return the soul key, or {@code null} when the press is none of ours
     */
    @Nullable
    public static SoulKey resolve(int keyCode) {
        for (Map.Entry<SoulKey, KeyMapping> entry : BINDINGS.entrySet()) {
            if (((KeyMappingAccessor) entry.getValue()).getBoundKey().getValue() == keyCode) {
                return entry.getKey();
            }
        }
        return null;
    }
}

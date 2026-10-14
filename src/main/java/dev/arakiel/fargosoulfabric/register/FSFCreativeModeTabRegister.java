package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FSF;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

/**
 * The tab the souls are listed in.
 *
 * <p>The items are listed in registration order in one flat grid: the tab is a plain item list, not a
 * set of separate sections.
 */
public final class FSFCreativeModeTabRegister {

    public static final CreativeModeTab FSFTab = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB, FSF.rl(FSF.MODID),
            CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.fargosoulfabric"))
                    .icon(() -> FSFItemRegister.TerraSoulItem.getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        for (Item item : FSF.REGISTRIES.items()) {
                            output.accept(item);
                        }
                    })
                    .build());

    private FSFCreativeModeTabRegister() {
    }

    public static void init() {
        // Registered by the field initializer above.
    }
}

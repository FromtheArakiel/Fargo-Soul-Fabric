package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FargoSoul;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class FargoSoulItemTagsRegister {

    /**
     * The souls, and only the souls.
     *
     * <p>They are deliberately not added to the generic {@code #curiosfabric:curio} tag of the
     * accessory library: that tag marks "universal" accessories, and Curios collapses the slot list
     * of such an item down to the generic curio slot in its tooltip. Listing a soul there would make
     * every soul report {@code 槽位：饰品} instead of its real slot.</p>
     */
    public static final TagKey<Item> Souls = register("souls");

    public static final TagKey<Item> SectionSoul = register("sections/soul");
    public static final TagKey<Item> SectionPower = register("sections/power");
    public static final TagKey<Item> SectionEnchantment = register("sections/enchantment");

    private static TagKey<Item> register(String path) {
        return TagKey.create(Registries.ITEM, FargoSoul.rl(path));
    }
}

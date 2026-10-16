package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FSF;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class FSFItemTagsRegister {

    public static final TagKey<Item> Souls = register("souls");

    public static final TagKey<Item> SectionSoul = register("sections/soul");
    public static final TagKey<Item> SectionPower = register("sections/power");
    public static final TagKey<Item> SectionEnchantment = register("sections/enchantment");

    private static TagKey<Item> register(String path) {
        return TagKey.create(Registries.ITEM, FSF.rl(path));
    }
}

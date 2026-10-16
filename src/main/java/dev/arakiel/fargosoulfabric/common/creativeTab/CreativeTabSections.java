package dev.arakiel.fargosoulfabric.common.creativeTab;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.register.FSFItemTagsRegister;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class CreativeTabSections {

    /** One block: where it goes, the banner it draws and the tag its items come from. */
    public record Section(int order, ResourceLocation texture, TagKey<Item> tag) {
    }

    private static final String BANNER = "textures/item/banner/default_banner.png";

    private static final List<Section> SECTIONS = new ArrayList<>();

    static {
        add(1, FSFItemTagsRegister.SectionSoul);
        add(2, FSFItemTagsRegister.SectionPower);
        add(3, FSFItemTagsRegister.SectionEnchantment);
    }

    private CreativeTabSections() {
    }

    private static void add(int order, TagKey<Item> tag) {
        SECTIONS.add(new Section(order, FSF.rl(BANNER), tag));
        SECTIONS.sort(Comparator.comparingInt(Section::order));
    }

    public static List<Section> sections() {
        return SECTIONS;
    }

    /** The items of one block, in registry order. */
    public static List<ItemStack> itemsOf(Section section) {
        List<ItemStack> items = new ArrayList<>();
        BuiltInRegistries.ITEM.stream()
                .map(Item::getDefaultInstance)
                .filter(stack -> stack.is(section.tag()))
                .forEach(items::add);
        return items;
    }
}

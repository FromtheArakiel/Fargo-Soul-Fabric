package dev.arakiel.fargosoulfabric.client.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.FSFConfig;
import dev.arakiel.fargosoulfabric.common.creativeTab.CreativeTabSections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/** Lays this mod's creative tab out in sections instead of one flat list. */
@Mixin(CreativeModeTab.class)
public class CreativeModeTabMixin {

    @Shadow
    private Collection<ItemStack> displayItems;

    @Shadow
    private Set<ItemStack> displayItemsSearchTab;

    @WrapMethod(method = "buildContents")
    private void fsf$buildSectionedContents(ItemDisplayParameters parameters, Operation<Void> original) {
        CreativeModeTab tab = (CreativeModeTab) (Object) this;
        ResourceLocation id = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(tab);

        if (!FSFConfig.creativeTabSections() || id == null || !FSF.MODID.equals(id.getNamespace())) {
            original.call(parameters);
            return;
        }
        List<ItemStack> items = new LinkedList<>();
        Set<ItemStack> search = new LinkedHashSet<>();

        for (CreativeTabSections.Section section : CreativeTabSections.sections()) {
            List<ItemStack> stacks = new ArrayList<>(CreativeTabSections.itemsOf(section));

            // One empty row for the banner, then whole rows of items: that is what splits the blocks.
            for (int i = 0; i < 9; i++) {
                stacks.addFirst(ItemStack.EMPTY);
            }
            while (stacks.size() % 9 != 0) {
                stacks.add(ItemStack.EMPTY);
            }
            for (ItemStack stack : stacks) {
                items.add(stack);
                if (!stack.isEmpty()) {
                    search.add(stack);
                }
            }
        }
        this.displayItems = items;
        this.displayItemsSearchTab = search;
    }
}

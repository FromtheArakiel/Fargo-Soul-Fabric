package dev.arakiel.fargosoulfabric.client.mixin;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.FSFConfig;
import dev.arakiel.fargosoulfabric.common.creativeTab.CreativeTabSections;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Draws each block's banner into the empty row that block reserved. */
@Mixin(CreativeModeInventoryScreen.class)
public class CreativeModeInventoryScreenMixin {

    @Shadow
    private static CreativeModeTab selectedTab;

    @Shadow
    private float scrollOffs;

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/inventory/EffectRenderingInventoryScreen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            shift = At.Shift.AFTER))
    private void fsf$renderBanners(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick,
                                   CallbackInfo ci) {
        if (!FSFConfig.creativeTabSections()) {
            return;
        }
        ResourceLocation id = selectedTab == null ? null : BuiltInRegistries.CREATIVE_MODE_TAB.getKey(selectedTab);

        if (id == null || !FSF.MODID.equals(id.getNamespace())) {
            return;
        }
        List<CreativeTabSections.Section> sections = CreativeTabSections.sections();
        int totalRows = 0;

        for (CreativeTabSections.Section section : sections) {
            totalRows += 1 + (CreativeTabSections.itemsOf(section).size() + 8) / 9;
        }
        int scrollRow = Math.round(this.scrollOffs * (float) Math.max(0, totalRows - 5));
        int left = ((AbstractContainerScreenAccessor) (Object) this).fsf$leftPos() + 8;
        int top = ((AbstractContainerScreenAccessor) (Object) this).fsf$topPos() + 17;
        int currentRow = 0;

        for (CreativeTabSections.Section section : sections) {
            int visibleRow = currentRow - scrollRow;
            currentRow += 1 + (CreativeTabSections.itemsOf(section).size() + 8) / 9;

            if (visibleRow >= 0 && visibleRow < 5) {
                int y = top + visibleRow * 18;

                // The banner is one 18 pixel frame, repeated across the nine columns of that row.
                for (int column = 0; column < 9; column++) {
                    guiGraphics.blit(section.texture(), left + column * 18, y, 0, 0, 18, 18, 18, 18);
                }
            }
        }
    }
}

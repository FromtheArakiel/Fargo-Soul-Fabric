package dev.arakiel.fargosoulfabric.client.tooltip;

import dev.arakiel.fargosoulfabric.common.item.SummonerWeaponItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class TooltipHandler {
   public TooltipHandler() {
   }

   public static List<Component> getCustomTooltip(ItemStack itemStack, Player player) {
      List<Component> lines = new ArrayList<>();
      Item item = itemStack.getItem();
      ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(item);
      List<MutableComponent> lore = new ArrayList<>();
      String baseKey = "item." + registryName.toLanguageKey() + ".fargosoul_tooltip.";

      for (int index = 1; I18n.exists(baseKey + index); index++) {
         lore.add(Component.translatable(baseKey + index));
      }

      if (!lore.isEmpty()) {
         if (player != null) {
            lines.add(Component.empty());
         }

         for (MutableComponent component : lore) {
            lines.add(component.withStyle(ChatFormatting.DARK_GRAY));
         }
      }

      return lines;
   }

   public static List<Component> getMinionWeaponItemTooltip(ItemStack itemStack, Player player) {
      List<Component> lines = new ArrayList<>();
      if (itemStack.getItem() instanceof SummonerWeaponItem<?> summonerWeaponItem && player != null) {
         lines.addAll(summonerWeaponItem.getTooltips(itemStack, player));
      }

      return lines;
   }
}

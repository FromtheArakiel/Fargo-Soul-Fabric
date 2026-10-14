package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AttachmentEntityType<T extends AttachmentEntity>(@NotNull ResourceLocation location, @NotNull Supplier<T> factory) {

   public Component getDisplayName() {
      String key = "summon." + this.location.getNamespace() + "." + this.location.getPath();
      return Component.translatable(key).withStyle(ChatFormatting.BLUE);
   }
}

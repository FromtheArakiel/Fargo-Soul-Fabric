package dev.arakiel.fargosoulfabric.common.damageNumbers;

import net.minecraft.resources.ResourceLocation;

public record DamageInfoStyle(
   String damageType,
   ResourceLocation texture,
   int textureWidth,
   int textureHeight,
   int glyphSpacing,
   float renderSize,
   int maxLife,
   int color,
   int criticalColor
) {
   public static final int GLYPH_COUNT = 11;


   public int glyphPixelWidth() {
      return this.textureWidth / 11;
   }

   public int glyphPixelHeight() {
      return this.textureHeight;
   }

   public static int parseHexColor(String hex) {
      if (hex != null && hex.length() == 6) {
         try {
            return Integer.parseInt(hex, 16);
         } catch (NumberFormatException var2) {
            throw new IllegalArgumentException("Invalid hex color: " + hex, var2);
         }
      } else {
         throw new IllegalArgumentException("Invalid hex color: " + hex + " (expected 6 hex digits)");
      }
   }
}

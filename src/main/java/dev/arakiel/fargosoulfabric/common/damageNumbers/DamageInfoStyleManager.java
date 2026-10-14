package dev.arakiel.fargosoulfabric.common.damageNumbers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class DamageInfoStyleManager extends SimpleJsonResourceReloadListener
      implements net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   public static final DamageInfoStyleManager INSTANCE = new DamageInfoStyleManager();
   @Nullable
   private DamageInfoStyle defaultStyle;
   private Map<ResourceLocation, DamageInfoStyle> styleMap = new HashMap<>();

   private DamageInfoStyleManager() {
      super(GSON, "damage_info");
   }

   @Override
   public ResourceLocation getFabricId() {
      return ResourceLocation.fromNamespaceAndPath("fargosoulfabric", "damage_info_styles");
   }

   protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager resourceManager, ProfilerFiller profiler) {
      Map<ResourceLocation, DamageInfoStyle> newMap = new HashMap<>();
      DamageInfoStyle newDefault = null;

      for (Entry<ResourceLocation, JsonElement> entry : resources.entrySet()) {
         ResourceLocation fileId = entry.getKey();

         try {
            JsonObject root = GsonHelper.convertToJsonObject(entry.getValue(), "damage_info");
            if (root.has("default")) {
               DamageInfoStyle style = this.parseStyle(GsonHelper.getAsJsonObject(root, "default"));
               if (style != null) {
                  newDefault = style;
               }
            }

            if (root.has("entries")) {
               for (JsonElement elem : GsonHelper.getAsJsonArray(root, "entries")) {
                  JsonObject obj = elem.getAsJsonObject();
                  DamageInfoStyle style = this.parseStyle(obj);
                  if (style != null) {
                     ResourceLocation key = ResourceLocation.parse(style.damageType());
                     newMap.put(key, style);
                  }
               }
            }
         } catch (Exception var15) {
            LOGGER.warn("Failed to parse damage_info file {}: {}", fileId, var15.getMessage());
         }
      }

      if (newDefault == null) {
         LOGGER.warn("No default style defined in damage_info JSON, undefined damage types will be skipped");
      }

      this.defaultStyle = newDefault;
      this.styleMap = newMap;
      LOGGER.info("Loaded {} damage info styles, default: {}", newMap.size(), newDefault != null ? newDefault.damageType() : "null");
   }

   @Nullable
   private DamageInfoStyle parseStyle(JsonObject obj) {
      try {
         String[] requiredFields = new String[]{
            "damage_type", "texture", "texture_width", "texture_height", "glyph_spacing", "render_size", "max_life", "color", "critical_color"
         };

         for (String field : requiredFields) {
            if (!obj.has(field)) {
               LOGGER.warn("Missing required field '{}' in damage_info entry, skipping", field);
               return null;
            }
         }

         String damageType = GsonHelper.getAsString(obj, "damage_type");
         String textureStr = GsonHelper.getAsString(obj, "texture");
         int textureWidth = GsonHelper.getAsInt(obj, "texture_width");
         int textureHeight = GsonHelper.getAsInt(obj, "texture_height");
         int glyphSpacing = GsonHelper.getAsInt(obj, "glyph_spacing");
         float renderSize = GsonHelper.getAsFloat(obj, "render_size");
         int maxLife = GsonHelper.getAsInt(obj, "max_life");
         int color = DamageInfoStyle.parseHexColor(GsonHelper.getAsString(obj, "color"));
         int criticalColor = DamageInfoStyle.parseHexColor(GsonHelper.getAsString(obj, "critical_color"));
         if (textureWidth <= 0 || textureHeight <= 0) {
            LOGGER.warn("Invalid texture dimensions {}x{} for damage_type '{}', skipping", new Object[]{textureWidth, textureHeight, damageType});
            return null;
         } else if (renderSize <= 0.0F) {
            LOGGER.warn("Invalid render_size {} for damage_type '{}', skipping", renderSize, damageType);
            return null;
         } else if (maxLife <= 0) {
            LOGGER.warn("Invalid max_life {} for damage_type '{}', skipping", maxLife, damageType);
            return null;
         } else {
            return new DamageInfoStyle(
               damageType, ResourceLocation.parse(textureStr), textureWidth, textureHeight, glyphSpacing, renderSize, maxLife, color, criticalColor
            );
         }
      } catch (Exception var12) {
         LOGGER.warn("Failed to parse damage_info entry: {}", var12.getMessage());
         return null;
      }
   }

   @Nullable
   public DamageInfoStyle getStyle(ResourceLocation damageTypeId) {
      DamageInfoStyle exact = this.styleMap.get(damageTypeId);
      return exact != null ? exact : this.defaultStyle;
   }

   @Nullable
   public DamageInfoStyle getDefault() {
      return this.defaultStyle;
   }
}

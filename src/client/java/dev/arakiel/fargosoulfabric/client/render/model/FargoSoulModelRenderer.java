package dev.arakiel.fargosoulfabric.client.render.model;

import dev.arakiel.fargosoulfabric.client.render.model.json.JsonModelRenderOptions;
import dev.arakiel.fargosoulfabric.client.render.model.json.JsonModelRenderer;
import dev.arakiel.fargosoulfabric.client.render.model.virtual.VirtualEntityRenderOptions;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public final class FargoSoulModelRenderer {
   private FargoSoulModelRenderer() {
   }

   public static JsonModelRenderOptions json(ModelResourceLocation model) {
      return new JsonModelRenderOptions(model);
   }

   public static JsonModelRenderOptions json(ResourceLocation modelId) {
      return new JsonModelRenderOptions(JsonModelRenderer.standaloneLocation(modelId));
   }

   public static ModelResourceLocation jsonLocation(ResourceLocation modelId) {
      return JsonModelRenderer.standaloneLocation(modelId);
   }

   public static VirtualEntityRenderOptions virtualEntity(EntityType<?> entityType, float partialTick) {
      return new VirtualEntityRenderOptions(entityType, partialTick);
   }

   public static VirtualEntityRenderOptions virtualEntity(ResourceLocation entityTypeId, float partialTick) {
      EntityType<?> entityType = (EntityType<?>)BuiltInRegistries.ENTITY_TYPE.get(entityTypeId);
      return virtualEntity(entityType, partialTick);
   }
}

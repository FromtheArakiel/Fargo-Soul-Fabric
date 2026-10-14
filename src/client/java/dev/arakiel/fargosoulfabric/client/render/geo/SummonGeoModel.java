package dev.arakiel.fargosoulfabric.client.render.geo;

import dev.arakiel.fargosoulfabric.client.render.RenderContext;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

// GeckoLib 4.9 resolves the resources through these two methods; they only carry the deprecation
// marker because a later version moves them to the generic base class.
@SuppressWarnings("deprecation")
public class SummonGeoModel<T extends Summon> extends GeoModel<T> {
   private final Function<RenderContext<T>, ResourceLocation> basePathResolver;
   private ResourceLocation cachedPath;
   private ResourceLocation model;
   private ResourceLocation texture;
   private ResourceLocation animation;

   public SummonGeoModel(Function<RenderContext<T>, ResourceLocation> basePathResolver) {
      this.basePathResolver = basePathResolver;
   }

   public void resolve(RenderContext<T> context) {
      ResourceLocation basePath = this.basePathResolver.apply(context);
      if (basePath != null && !basePath.equals(this.cachedPath)) {
         this.cachedPath = basePath;
         String path = basePath.getPath();
         this.model = ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "geo/" + path + ".geo.json");
         this.texture = ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "textures/" + path + ".png");
         this.animation = ResourceLocation.fromNamespaceAndPath(basePath.getNamespace(), "animations/" + path + ".animation.json");
      }
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture) {
      return RenderType.entityTranslucentCull(texture);
   }

   public ResourceLocation getModelResource(T animatable) {
      return this.model;
   }

   public ResourceLocation getTextureResource(T animatable) {
      return this.texture;
   }

   public ResourceLocation getAnimationResource(T animatable) {
      return this.animation;
   }
}

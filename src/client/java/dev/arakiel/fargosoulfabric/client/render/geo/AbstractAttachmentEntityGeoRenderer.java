package dev.arakiel.fargosoulfabric.client.render.geo;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.client.render.AbstractAttachmentEntityRenderer;
import dev.arakiel.fargosoulfabric.client.render.RenderContext;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;
import software.bernie.geckolib.util.Color;

@SuppressWarnings("this-escape")
public abstract class AbstractAttachmentEntityGeoRenderer<T extends AttachmentEntity> extends AbstractAttachmentEntityRenderer<T> implements GeoRenderer<T> {
   protected final AttachmentEntityGeoModel<T> model;
   protected final AttachmentEntityGeoRenderer<T> renderer;

   protected AbstractAttachmentEntityGeoRenderer(ResourceLocation location) {
      this((Function<RenderContext<T>, ResourceLocation>)(renderContext -> location));
   }

   protected AbstractAttachmentEntityGeoRenderer(Function<RenderContext<T>, ResourceLocation> resolver) {
      this.model = new AttachmentEntityGeoModel<>(resolver);
      this.renderer = new AttachmentEntityGeoRenderer<>(this, this.model);
   }

   @Override
   protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
      T entity = this.context.entity;
      float partialTick = this.context.partialTick;
      this.model.resolve(this.context);
      ResourceLocation textureLocation = this.getTextureLocation(entity);
      if (textureLocation != null) {
         RenderType renderType = this.getRenderType(entity, textureLocation, bufferSource, partialTick);
         this.renderer
            .render(
               poseStack,
               entity,
               bufferSource,
               renderType,
               renderType != null ? bufferSource.getBuffer(renderType) : null,
               this.context.packedLight,
               partialTick
            );
      }
   }

   public Color getRenderColor(T animatable, float partialTick, int packedLight) {
      return this.context.color;
   }

   public RenderType getRenderType(T animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
      return RenderType.entityTranslucentCull(texture);
   }

   public GeoModel<T> getGeoModel() {
      return this.renderer.getGeoModel();
   }

   public T getAnimatable() {
      return this.renderer.getAnimatable();
   }

   public List<GeoRenderLayer<T>> getRenderLayers() {
      return this.renderer.getRenderLayers();
   }

   public void fireCompileRenderLayersEvent() {
      this.renderer.fireCompileRenderLayersEvent();
   }

   public boolean firePreRenderEvent(PoseStack poseStack, BakedGeoModel bakedGeoModel, MultiBufferSource multiBufferSource, float partialTick, int packedLight) {
      return this.renderer.firePreRenderEvent(poseStack, bakedGeoModel, multiBufferSource, partialTick, packedLight);
   }

   public void firePostRenderEvent(PoseStack poseStack, BakedGeoModel bakedGeoModel, MultiBufferSource multiBufferSource, float partialTick, int packedLight) {
      this.renderer.firePostRenderEvent(poseStack, bakedGeoModel, multiBufferSource, partialTick, packedLight);
   }

   public void updateAnimatedTextureFrame(T animatable) {
      this.renderer.updateAnimatedTextureFrame(animatable);
   }
}

package dev.arakiel.fargosoulfabric.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import dev.arakiel.fargosoulfabric.FSF;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import net.minecraft.client.renderer.RenderType.CompositeState;
import net.minecraft.resources.ResourceLocation;

public class FSFRenderTypes extends RenderType {
   private static final RenderType TRAIL = RenderType.entityTranslucent(FSF.rl("textures/trail.png"));
   private static final Function<ResourceLocation, RenderType> NO_DEPTH_TEXTURE = Util.memoize(
      texture -> {
         CompositeState state = CompositeState.builder()
            .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
            .setTextureState(new TextureStateShard(texture, false, false))
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setDepthTestState(NO_DEPTH_TEST)
            .setWriteMaskState(COLOR_WRITE)
            .createCompositeState(true);
         return create("fargosoul_texture_no_depth", DefaultVertexFormat.NEW_ENTITY, Mode.QUADS, 1536, true, true, state);
      }
   );

   public FSFRenderTypes(
      String name, VertexFormat format, Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState
   ) {
      super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
   }

   public static RenderType getTrail() {
      return TRAIL;
   }

   public static RenderType getModel() {
      return Sheets.translucentItemSheet();
   }

   public static RenderType texture(ResourceLocation texture, boolean alwaysVisible) {
      return alwaysVisible ? NO_DEPTH_TEXTURE.apply(texture) : RenderType.entityTranslucent(texture);
   }
}

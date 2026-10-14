package dev.arakiel.fargosoulfabric.client.register;

import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.BloodDropRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.ChlorophyteOrbRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.LightningOrbRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.ShadowOrbRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.TerraBladeRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.TrackingBloodRenderer;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.client.render.AttachmentEntityRenderDispatcher;
import dev.arakiel.fargosoulfabric.client.render.IAttachmentEntityRenderer;
import dev.arakiel.fargosoulfabric.client.render.RenderUtil;
import dev.arakiel.fargosoulfabric.client.render.SimpleRenderer;
import dev.arakiel.fargosoulfabric.client.render.rendererHelper.SphereRendererHelper;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

/** The renderers of the summons. Client only, so it is registered from the client initializer. */
public final class SummonerAttachmentEntityRenderRegister {

    private SummonerAttachmentEntityRenderRegister() {
    }

    public static void init() {
        register(SummonerAttachmentEntityRegister.METEOR,
                () -> simpleRenderer(FargoSoul.rl("textures/item/entity/meteor.png"), 1, 8));
        register(SummonerAttachmentEntityRegister.VORTEX, () -> simpleRenderer(null, 0, 0));
        register(SummonerAttachmentEntityRegister.SPRINT, () -> simpleRenderer(null, 0, 0));
        register(SummonerAttachmentEntityRegister.SHADOW_ORB, ShadowOrbRenderer::new);
        register(SummonerAttachmentEntityRegister.LIGHTNING_ORB, LightningOrbRenderer::new);
        register(SummonerAttachmentEntityRegister.SNOW_BALL,
                () -> simpleRenderer(ResourceLocation.withDefaultNamespace("textures/item/snowball.png"), 0.35f, 0));
        register(SummonerAttachmentEntityRegister.TRACKING_BLOOD, TrackingBloodRenderer::new);
        register(SummonerAttachmentEntityRegister.BLOOD_DROP, BloodDropRenderer::new);
        register(SummonerAttachmentEntityRegister.STAR, () -> new SimpleRenderer<>(
                (entity, poseStack, bufferSource, visualNode, context, partialTick, alpha) ->
                        SphereRendererHelper.builder()
                                .radius(0.1f)
                                .layers(2)
                                .sides(6)
                                .color(0xE4E4A9FF)
                                .alpha(0.85f)
                                .innerRatio(0.5f)
                                .render(poseStack, bufferSource)));
        register(SummonerAttachmentEntityRegister.PETAL, () -> new SimpleRenderer<>(
                (entity, poseStack, bufferSource, visualNode, context, partialTick, alpha) ->
                        SphereRendererHelper.builder()
                                .radius(0.12f)
                                .layers(2)
                                .sides(6)
                                .color(0xFFF2A2C4)
                                .alpha(0.85f)
                                .innerRatio(0.5f)
                                .render(poseStack, bufferSource)));
        register(SummonerAttachmentEntityRegister.GHOST_ORB, () -> new SimpleRenderer<>(
                (entity, poseStack, bufferSource, visualNode, context, partialTick, alpha) ->
                        SphereRendererHelper.builder()
                                .radius(0.1f)
                                .layers(2)
                                .sides(6)
                                .color(0xFFB8D8FF)
                                .alpha(0.85f)
                                .innerRatio(0.5f)
                                .render(poseStack, bufferSource)));
        register(SummonerAttachmentEntityRegister.CHLOROPHYTE_ORB, ChlorophyteOrbRenderer::new);
        register(SummonerAttachmentEntityRegister.TERRA_BLADE, TerraBladeRenderer::new);
    }

    private static <T extends AttachmentEntity> void register(AttachmentEntityType<T> type,
                                                              @Nullable Supplier<IAttachmentEntityRenderer<T>> renderer) {
        AttachmentEntityRenderDispatcher.register(type, renderer != null ? renderer.get() : new SimpleRenderer<>(null));
    }

    private static <T extends AttachmentEntity> SimpleRenderer<T> simpleRenderer(@Nullable ResourceLocation texture,
                                                                                float size, int light) {
        return new SimpleRenderer<>((entity, poseStack, bufferSource, visualNode, context, partialTick, alpha) -> {
            if (texture != null && size > 0) {
                RenderUtil.renderImage(texture, visualNode.pos(), size, size, bufferSource, false,
                        FastColor.ARGB32.color((int) (alpha * 255), 255, 255, 255));
            }
            if (light > 0) {
                DynamicLightDispatcher.addLightSources(visualNode.pos(), light);
            }
        });
    }
}

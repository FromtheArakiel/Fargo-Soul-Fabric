package dev.arakiel.fargosoulfabric.client.register;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.BloodDropRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.ChlorophyteOrbRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.LightningOrbRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.ShadowOrbRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.TerraBladeRenderer;
import dev.arakiel.fargosoulfabric.client.renderer.projectile.TrackingBloodRenderer;
import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.client.render.SummonRenderDispatcher;
import dev.arakiel.fargosoulfabric.client.render.ISummonRenderer;
import dev.arakiel.fargosoulfabric.client.render.RenderUtil;
import dev.arakiel.fargosoulfabric.client.render.SimpleRenderer;
import dev.arakiel.fargosoulfabric.client.render.helper.SphereRendererHelper;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

/** The renderers of the summons. Client only, so it is registered from the client initializer. */
public final class SummonRenderRegister {

    private SummonRenderRegister() {
    }

    public static void init() {
        register(SummonTypeRegister.METEOR,
                () -> simpleRenderer(FSF.rl("textures/item/entity/meteor.png"), 1, 8));
        register(SummonTypeRegister.VORTEX, () -> simpleRenderer(null, 0, 0));
        register(SummonTypeRegister.SPRINT, () -> simpleRenderer(null, 0, 0));
        register(SummonTypeRegister.SHADOW_ORB, ShadowOrbRenderer::new);
        register(SummonTypeRegister.LIGHTNING_ORB, LightningOrbRenderer::new);
        register(SummonTypeRegister.SNOW_BALL,
                () -> simpleRenderer(ResourceLocation.withDefaultNamespace("textures/item/snowball.png"), 0.35f, 0));
        register(SummonTypeRegister.TRACKING_BLOOD, TrackingBloodRenderer::new);
        register(SummonTypeRegister.BLOOD_DROP, BloodDropRenderer::new);
        register(SummonTypeRegister.STAR, () -> new SimpleRenderer<>(
                (entity, poseStack, bufferSource, visualNode, context, partialTick, alpha) ->
                        SphereRendererHelper.builder()
                                .radius(0.1f)
                                .layers(2)
                                .sides(6)
                                .color(0xE4E4A9FF)
                                .alpha(0.85f)
                                .innerRatio(0.5f)
                                .render(poseStack, bufferSource)));
        register(SummonTypeRegister.PETAL, () -> new SimpleRenderer<>(
                (entity, poseStack, bufferSource, visualNode, context, partialTick, alpha) ->
                        SphereRendererHelper.builder()
                                .radius(0.12f)
                                .layers(2)
                                .sides(6)
                                .color(0xFFF2A2C4)
                                .alpha(0.85f)
                                .innerRatio(0.5f)
                                .render(poseStack, bufferSource)));
        register(SummonTypeRegister.GHOST_ORB, () -> new SimpleRenderer<>(
                (entity, poseStack, bufferSource, visualNode, context, partialTick, alpha) ->
                        SphereRendererHelper.builder()
                                .radius(0.1f)
                                .layers(2)
                                .sides(6)
                                .color(0xFFB8D8FF)
                                .alpha(0.85f)
                                .innerRatio(0.5f)
                                .render(poseStack, bufferSource)));
        register(SummonTypeRegister.CHLOROPHYTE_ORB, ChlorophyteOrbRenderer::new);
        register(SummonTypeRegister.TERRA_BLADE, TerraBladeRenderer::new);
    }

    private static <T extends Summon> void register(SummonType<T> type,
                                                              @Nullable Supplier<ISummonRenderer<T>> renderer) {
        SummonRenderDispatcher.register(type, renderer != null ? renderer.get() : new SimpleRenderer<>(null));
    }

    private static <T extends Summon> SimpleRenderer<T> simpleRenderer(@Nullable ResourceLocation texture,
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

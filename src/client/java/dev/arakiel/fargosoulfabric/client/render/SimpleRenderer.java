package dev.arakiel.fargosoulfabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * A renderer that is built from a lambda, for the summons that just draw a sphere, an image or a
 * model without any extra state.
 *
 * @param <T> the summon type
 */
public class SimpleRenderer<T extends Summon> extends AbstractSummonRenderer<T> {

    /** Called once per frame for the summon that is being rendered. */
    @FunctionalInterface
    public interface Callback<T extends Summon> {
        void render(T entity, PoseStack poseStack, MultiBufferSource bufferSource, PathNode visualNode,
                    RenderContext<T> context, float partialTick, float alpha);
    }

    private final Callback<T> callback;

    public SimpleRenderer(Callback<T> callback) {
        this.callback = callback;
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource bufferSource) {
        if (this.callback != null) {
            this.callback.render(this.context.entity, poseStack, bufferSource, this.context.visualNode,
                    this.context, this.context.partialTick, this.getAlpha());
        }
    }
}

package dev.arakiel.fargosoulfabric.client.damageInfo;

import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.arakiel.fargosoulfabric.common.damageInfo.DamageInfo;
import dev.arakiel.fargosoulfabric.common.damageInfo.DamageInfoStyle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Draws the glyphs of a single damage number.
 *
 * <p>This is the rendering half of {@link DamageInfo}: it needs a vertex consumer, so it stays on
 * the client.
 */
public final class DamageInfoGlyphRenderer {

    private DamageInfoGlyphRenderer() {
    }

    /** Emits the quads of {@code info} into {@code consumer}. */
    public static void render(DamageInfo info, VertexConsumer consumer, Quaternionf baseRotation,
                              Vec3 camPos, float partialTick) {
        DamageInfoStyle style = info.getStyle();
        String text = info.getText();
        Vec3 renderPos = info.getRenderPos(partialTick);
        float scale = info.getRenderScale(partialTick);
        int color = info.getRenderColor(partialTick);
        float roll = info.getRenderRoll(partialTick);
        float size = style.renderSize() * scale;
        float spacingWorld = (float) style.glyphSpacing() * style.renderSize() / (float) style.glyphPixelWidth();
        float step = size + spacingWorld * scale;
        float halfSize = size * 0.5F;
        float totalWidth = text.isEmpty() ? 0.0F : (float) (text.length() - 1) * step + size;
        float halfWidth = totalWidth / 2.0F;
        Matrix4f matrix = new Matrix4f()
                .rotate(baseRotation.rotateZ(roll * (float) (Math.PI / 180.0), new Quaternionf()))
                .setTranslation((float) (renderPos.x() - camPos.x()), (float) (renderPos.y() - camPos.y()),
                        (float) (renderPos.z() - camPos.z()));
        int glyphPixelWidth = style.glyphPixelWidth();
        int textureWidth = style.textureWidth();
        Vector3f vertex = new Vector3f();

        for (int i = 0; i < text.length(); i++) {
            int glyph = glyphIndex(text.charAt(i));
            float u0 = (float) (glyph * glyphPixelWidth) / (float) textureWidth;
            float u1 = (float) ((glyph + 1) * glyphPixelWidth) / (float) textureWidth;
            float x0 = (float) i * step - halfWidth;
            float x1 = x0 + size;
            matrix.transformPosition(x0, -halfSize, 0.0F, vertex);
            consumer.addVertex(vertex.x, vertex.y, vertex.z, color, u0, 0.0F, OverlayTexture.NO_OVERLAY,
                    LightTexture.FULL_BRIGHT, 0.0F, 0.0F, 1.0F);
            matrix.transformPosition(x0, halfSize, 0.0F, vertex);
            consumer.addVertex(vertex.x, vertex.y, vertex.z, color, u0, 1.0F, OverlayTexture.NO_OVERLAY,
                    LightTexture.FULL_BRIGHT, 0.0F, 0.0F, 1.0F);
            matrix.transformPosition(x1, halfSize, 0.0F, vertex);
            consumer.addVertex(vertex.x, vertex.y, vertex.z, color, u1, 1.0F, OverlayTexture.NO_OVERLAY,
                    LightTexture.FULL_BRIGHT, 0.0F, 0.0F, 1.0F);
            matrix.transformPosition(x1, -halfSize, 0.0F, vertex);
            consumer.addVertex(vertex.x, vertex.y, vertex.z, color, u1, 0.0F, OverlayTexture.NO_OVERLAY,
                    LightTexture.FULL_BRIGHT, 0.0F, 0.0F, 1.0F);
        }
    }

    private static int glyphIndex(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c == '.') {
            return 10;
        }
        throw new IllegalArgumentException("Unexpected damage glyph: " + c);
    }
}

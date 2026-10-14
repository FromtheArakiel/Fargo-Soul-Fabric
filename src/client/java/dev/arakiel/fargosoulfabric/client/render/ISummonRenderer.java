package dev.arakiel.fargosoulfabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import net.minecraft.client.renderer.MultiBufferSource;

@FunctionalInterface
public interface ISummonRenderer<T extends Summon> {
   void render(T var1, PoseStack var2, MultiBufferSource var3, float var4, int var5, PathNode var6);
}

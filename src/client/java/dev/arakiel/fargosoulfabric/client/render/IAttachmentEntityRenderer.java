package dev.arakiel.fargosoulfabric.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import net.minecraft.client.renderer.MultiBufferSource;

@FunctionalInterface
public interface IAttachmentEntityRenderer<T extends AttachmentEntity> {
   void render(T var1, PoseStack var2, MultiBufferSource var3, float var4, int var5, PathNode var6);
}

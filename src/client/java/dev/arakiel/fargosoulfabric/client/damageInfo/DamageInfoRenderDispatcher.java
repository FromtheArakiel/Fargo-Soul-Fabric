package dev.arakiel.fargosoulfabric.client.damageInfo;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.arakiel.fargosoulfabric.common.attachment.DamageInfoData;
import dev.arakiel.fargosoulfabric.common.damageInfo.DamageInfo;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class DamageInfoRenderDispatcher {
   public DamageInfoRenderDispatcher() {
   }

   public static void render(Level level, Camera camera, MultiBufferSource bufferSource, float partialTick) {
      Minecraft minecraft = Minecraft.getInstance();
      DamageInfoData data = Attachments.get(level, FargoSoulAttachmentRegister.DAMAGE_INFO_DATA);
      Map<ResourceLocation, List<DamageInfo>> infos = data.getActiveInfos();
      if (!infos.isEmpty()) {
         Vec3 camPos = camera.getPosition();
         EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
         Quaternionf baseRotation = dispatcher.cameraOrientation().mul(Axis.XN.rotationDegrees(180.0F), new Quaternionf());

         for (Entry<ResourceLocation, List<DamageInfo>> group : infos.entrySet()) {
            VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucentCull(group.getKey()));

            for (DamageInfo info : group.getValue()) {
               DamageInfoGlyphRenderer.render(info, consumer, baseRotation, camPos, partialTick);
            }
         }
      }
   }
}
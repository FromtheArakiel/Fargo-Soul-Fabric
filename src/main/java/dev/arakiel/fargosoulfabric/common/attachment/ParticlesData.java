package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import dev.arakiel.fargosoulfabric.common.network.BatchedParticlesPayload;
import java.util.ArrayList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ParticlesData {
   private final List<BatchedParticlesPayload.Entry> entries = new ArrayList<>();

   public ParticlesData() {
   }

   public static void tick(ServerLevel level) {
      if (!level.isClientSide()) {
         ParticlesData data = Attachments.get(level, FargoSoulAttachmentRegister.BATCHED_PARTICLES);
         if (!data.entries.isEmpty()) {
            List<BatchedParticlesPayload.Entry> snapshot = new ArrayList<>(data.entries);
            data.entries.clear();
            for (ServerPlayer player : PlayerLookup.world(level)) {
               ServerPlayNetworking.send(player, new BatchedParticlesPayload(snapshot));
            }
         }
      }
   }

   public void add(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
      this.entries.add(new BatchedParticlesPayload.Entry(options, x, y, z, vx, vy, vz));
   }
}
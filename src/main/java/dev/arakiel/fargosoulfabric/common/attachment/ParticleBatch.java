package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.network.ParticleBatchPayload;
import java.util.ArrayList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ParticleBatch {
   private final List<ParticleBatchPayload.Entry> entries = new ArrayList<>();

   public ParticleBatch() {
   }

   public static void tick(ServerLevel level) {
      if (!level.isClientSide()) {
         ParticleBatch data = Attachments.get(level, FSFAttachmentRegister.BATCHED_PARTICLES);
         if (!data.entries.isEmpty()) {
            List<ParticleBatchPayload.Entry> snapshot = new ArrayList<>(data.entries);
            data.entries.clear();
            for (ServerPlayer player : PlayerLookup.world(level)) {
               // Same idea as the damage numbers: a player only receives the particles close enough
               // to be drawn, instead of the whole level's batch every tick.
               List<ParticleBatchPayload.Entry> visible = new ArrayList<>(snapshot.size());
               for (ParticleBatchPayload.Entry entry : snapshot) {
                  double dx = entry.x() - player.getX();
                  double dy = entry.y() - player.getY();
                  double dz = entry.z() - player.getZ();
                  if (dx * dx + dy * dy + dz * dz <= VISIBLE_RANGE_SQUARED) {
                     visible.add(entry);
                  }
               }
               if (!visible.isEmpty()) {
                  ServerPlayNetworking.send(player, new ParticleBatchPayload(visible));
               }
            }
         }
      }
   }

   /** Particles further away than this would not be rendered by the client anyway. */
   private static final double VISIBLE_RANGE_SQUARED = 64.0D * 64.0D;

   public void add(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
      this.entries.add(new ParticleBatchPayload.Entry(options, x, y, z, vx, vy, vz));
   }
}

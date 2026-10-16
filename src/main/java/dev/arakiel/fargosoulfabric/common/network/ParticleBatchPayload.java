package dev.arakiel.fargosoulfabric.common.network;

import dev.arakiel.fargosoulfabric.FSF;
import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.jetbrains.annotations.NotNull;

public record ParticleBatchPayload(List<ParticleBatchPayload.Entry> entries) implements CustomPacketPayload {
   public static final Type<ParticleBatchPayload> TYPE = new Type<>(FSF.rl("particle_batch"));

   /** Upper bound for one packet: the receiver must never allocate a list sized by the sender. */
   public static final int MAX_ENTRIES = 4096;

   public static final StreamCodec<RegistryFriendlyByteBuf, ParticleBatchPayload> STREAM_CODEC = StreamCodec.composite(
      boundedList(ParticleBatchPayload.Entry.STREAM_CODEC, MAX_ENTRIES), ParticleBatchPayload::entries, ParticleBatchPayload::new
   );

   private static <V> StreamCodec<RegistryFriendlyByteBuf, List<V>> boundedList(
           StreamCodec<? super RegistryFriendlyByteBuf, V> element, int max) {
      return StreamCodec.of(
              (buf, list) -> {
                 buf.writeVarInt(list.size());
                 for (V value : list) {
                    element.encode(buf, value);
                 }
              },
              buf -> {
                 int size = buf.readVarInt();
                 if (size < 0 || size > max) {
                    throw new DecoderException(
                            "Particle batch of " + size + " entries exceeds the limit of " + max);
                 }
                 List<V> list = new ArrayList<>(size);
                 for (int i = 0; i < size; i++) {
                    list.add(element.decode(buf));
                 }
                 return list;
              });
   }

   public ParticleBatchPayload(List<ParticleBatchPayload.Entry> entries) {
      this.entries = entries;
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static record Entry(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
      /** Particles of unknown type are written through their own stream codec. */
      @SuppressWarnings("unchecked")
      private static void encodeOptions(RegistryFriendlyByteBuf buf, ParticleType<?> type, ParticleOptions options) {
         StreamCodec<RegistryFriendlyByteBuf, ParticleOptions> codec =
                 (StreamCodec<RegistryFriendlyByteBuf, ParticleOptions>) type.streamCodec();
         codec.encode(buf, options);
      }

      @SuppressWarnings("unchecked")
      private static ParticleOptions decodeOptions(RegistryFriendlyByteBuf buf, ParticleType<?> type) {
         StreamCodec<RegistryFriendlyByteBuf, ParticleOptions> codec =
                 (StreamCodec<RegistryFriendlyByteBuf, ParticleOptions>) type.streamCodec();
         return codec.decode(buf);
      }

      public static final StreamCodec<RegistryFriendlyByteBuf, ParticleBatchPayload.Entry> STREAM_CODEC = StreamCodec.ofMember((entry, buf) -> {
         ParticleType<?> type = entry.options.getType();
         buf.writeVarInt(BuiltInRegistries.PARTICLE_TYPE.getId(type));
         encodeOptions(buf, type, entry.options);
         buf.writeDouble(entry.x);
         buf.writeDouble(entry.y);
         buf.writeDouble(entry.z);
         buf.writeDouble(entry.vx);
         buf.writeDouble(entry.vy);
         buf.writeDouble(entry.vz);
      }, buf -> {
         int id = buf.readVarInt();
         ParticleType<?> type = (ParticleType<?>)BuiltInRegistries.PARTICLE_TYPE.byId(id);

         if (type == null) {
            // The rest of the entry is written with that type's codec, so it cannot be skipped: fail
            // cleanly instead of dereferencing null deep inside the decoder.
            throw new DecoderException("Unknown particle type id " + id);
         }

         ParticleOptions options1 = decodeOptions(buf, type);
         double x1 = buf.readDouble();
         double y1 = buf.readDouble();
         double z1 = buf.readDouble();
         double vx1 = buf.readDouble();
         double vy1 = buf.readDouble();
         double vz1 = buf.readDouble();
         return new ParticleBatchPayload.Entry(options1, x1, y1, z1, vx1, vy1, vz1);
      });

      public Entry(ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
         this.options = options;
         this.x = x;
         this.y = y;
         this.z = z;
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
      }
   }
}

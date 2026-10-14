package dev.arakiel.fargosoulfabric.common.network;

import dev.arakiel.fargosoulfabric.FSF;
import io.netty.handler.codec.DecoderException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import org.jetbrains.annotations.NotNull;

public record DamageNumbersPayload(List<DamageNumbersPayload.Entry> entries) implements CustomPacketPayload {
   public static final Type<DamageNumbersPayload> TYPE = new Type<>(FSF.rl("damage_info"));

   /** Upper bound for one packet: the receiver must never allocate a list sized by the sender. */
   public static final int MAX_ENTRIES = 4096;

   /** Upper bound for the damage type id one entry may name. */
   public static final int MAX_ID_LENGTH = 128;

   public static final StreamCodec<RegistryFriendlyByteBuf, DamageNumbersPayload> STREAM_CODEC = StreamCodec.composite(
      boundedList(DamageNumbersPayload.Entry.STREAM_CODEC, MAX_ENTRIES), DamageNumbersPayload::entries, DamageNumbersPayload::new
   );

   /** Reads a list only when its declared length is sane; see the particle batch for the reason. */
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
                            "Damage info batch of " + size + " entries exceeds the limit of " + max);
                 }
                 List<V> list = new ArrayList<>(size);
                 for (int i = 0; i < size; i++) {
                    list.add(element.decode(buf));
                 }
                 return list;
              });
   }

   public DamageNumbersPayload(List<DamageNumbersPayload.Entry> entries) {
      this.entries = entries;
   }

   @NotNull
   public Type<? extends CustomPacketPayload> type() {
      return TYPE;
   }

   public static record Entry(String damageType, float damageAmount, double x, double y, double z, double vx, double vy, double vz, boolean critical) {
      public static final StreamCodec<RegistryFriendlyByteBuf, DamageNumbersPayload.Entry> STREAM_CODEC = StreamCodec.ofMember(
         (entry, buf) -> {
            buf.writeUtf(entry.damageType, MAX_ID_LENGTH);
            buf.writeFloat(entry.damageAmount);
            buf.writeDouble(entry.x);
            buf.writeDouble(entry.y);
            buf.writeDouble(entry.z);
            buf.writeDouble(entry.vx);
            buf.writeDouble(entry.vy);
            buf.writeDouble(entry.vz);
            buf.writeBoolean(entry.critical);
         },
         buf -> new DamageNumbersPayload.Entry(
               buf.readUtf(MAX_ID_LENGTH),
               buf.readFloat(),
               buf.readDouble(),
               buf.readDouble(),
               buf.readDouble(),
               buf.readDouble(),
               buf.readDouble(),
               buf.readDouble(),
               buf.readBoolean()
            )
      );

      public Entry(String damageType, float damageAmount, double x, double y, double z, double vx, double vy, double vz, boolean critical) {
         this.damageType = damageType;
         this.damageAmount = damageAmount;
         this.x = x;
         this.y = y;
         this.z = z;
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
         this.critical = critical;
      }
   }
}

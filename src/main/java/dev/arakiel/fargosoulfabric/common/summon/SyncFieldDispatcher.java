package dev.arakiel.fargosoulfabric.common.summon;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public final class SyncFieldDispatcher {
   private final List<SyncFieldDispatcher.SyncEntry> entries = new ArrayList<>();

   public SyncFieldDispatcher() {
   }

   public static SyncFieldDispatcher create(Consumer<SyncFieldDispatcher> consumer) {
      SyncFieldDispatcher dispatcher = new SyncFieldDispatcher();
      consumer.accept(dispatcher);
      return dispatcher;
   }

   public <T> void field(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Supplier<T> getter, Consumer<T> setter) {
      this.entries
         .add(new SyncFieldDispatcher.FieldSyncEntry((buf, level) -> codec.encode(buf, getter.get()), (buf, level) -> setter.accept(codec.decode(buf))));
   }

   public <T> void field(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Function<Level, T> getter, BiConsumer<Level, T> setter) {
      this.entries
         .add(
            new SyncFieldDispatcher.FieldSyncEntry(
               (buf, level) -> codec.encode(buf, getter.apply(level)), (buf, level) -> setter.accept(level, codec.decode(buf))
            )
         );
   }

   public <T> void field(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Supplier<T> getter, BiConsumer<Level, T> setter) {
      this.entries
         .add(
            new SyncFieldDispatcher.FieldSyncEntry((buf, level) -> codec.encode(buf, getter.get()), (buf, level) -> setter.accept(level, codec.decode(buf)))
         );
   }

   public <T> void field(StreamCodec<? super RegistryFriendlyByteBuf, T> codec, Function<Level, T> getter, Consumer<T> setter) {
      this.entries
         .add(
            new SyncFieldDispatcher.FieldSyncEntry((buf, level) -> codec.encode(buf, getter.apply(level)), (buf, level) -> setter.accept(codec.decode(buf)))
         );
   }

   public void encode(RegistryFriendlyByteBuf buf, Level level, boolean initialSync) {
      int maskStart = buf.writerIndex();
      int maskSize = this.entries.size() + 7 >>> 3;

      for (int i = 0; i < maskSize; i++) {
         buf.writeByte(0);
      }

      for (int i = 0; i < this.entries.size(); i++) {
         if (this.entries.get(i).encode(buf, level, initialSync)) {
            int maskIndex = maskStart + (i >>> 3);
            buf.setByte(maskIndex, buf.getByte(maskIndex) | 1 << (i & 7));
         }
      }
   }

   public void decode(RegistryFriendlyByteBuf buf, Level level) {
      if (!this.entries.isEmpty()) {
         int maskSize = this.entries.size() + 7 >>> 3;
         byte[] mask = new byte[maskSize];
         buf.readBytes(mask);

         for (int i = 0; i < this.entries.size(); i++) {
            if ((mask[i >>> 3] & 1 << (i & 7)) != 0) {
               this.entries.get(i).decode(buf, level);
            }
         }
      }
   }

   private interface FieldDecoder {
      void decode(RegistryFriendlyByteBuf var1, Level var2);
   }

   private interface FieldEncoder {
      void encode(RegistryFriendlyByteBuf var1, Level var2);
   }

   private static final class FieldSyncEntry implements SyncFieldDispatcher.SyncEntry {
      private final SyncFieldDispatcher.FieldEncoder encoder;
      private final SyncFieldDispatcher.FieldDecoder decoder;
      private byte[] previousValue;

      private FieldSyncEntry(SyncFieldDispatcher.FieldEncoder encoder, SyncFieldDispatcher.FieldDecoder decoder) {
         this.encoder = encoder;
         this.decoder = decoder;
      }

      @Override
      public boolean encode(RegistryFriendlyByteBuf buf, Level level, boolean initialSync) {
         int start = buf.writerIndex();
         this.encoder.encode(buf, level);
         if (!initialSync && matches(buf, start, this.previousValue)) {
            buf.writerIndex(start);
            return false;
         } else {
            byte[] currentValue = new byte[buf.writerIndex() - start];
            buf.getBytes(start, currentValue);
            this.previousValue = currentValue;
            return true;
         }
      }

      private static boolean matches(RegistryFriendlyByteBuf buf, int start, byte[] expected) {
         int length = buf.writerIndex() - start;
         if (expected != null && expected.length == length) {
            for (int i = 0; i < length; i++) {
               if (buf.getByte(start + i) != expected[i]) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public void decode(RegistryFriendlyByteBuf buf, Level level) {
         this.decoder.decode(buf, level);
      }
   }

   private interface SyncEntry {
      boolean encode(RegistryFriendlyByteBuf var1, Level var2, boolean var3);

      void decode(RegistryFriendlyByteBuf var1, Level var2);
   }
}

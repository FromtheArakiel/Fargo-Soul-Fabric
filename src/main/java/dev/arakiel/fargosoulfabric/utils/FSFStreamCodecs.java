package dev.arakiel.fargosoulfabric.utils;

import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface FSFStreamCodecs extends ByteBufCodecs {
   StreamCodec<RegistryFriendlyByteBuf, MinionSlotType> MINION_SLOT_TYPE = ByteBufCodecs.STRING_UTF8.map(MinionSlotType::valueOf, Enum::name).cast();
   StreamCodec<RegistryFriendlyByteBuf, Vec3> VEC_3 = StreamCodec.composite(
      ByteBufCodecs.DOUBLE, Vec3::x, ByteBufCodecs.DOUBLE, Vec3::y, ByteBufCodecs.DOUBLE, Vec3::z, Vec3::new
   );
   StreamCodec<RegistryFriendlyByteBuf, PathNode> PATH_NODE = StreamCodec.composite(
      VEC_3, PathNode::pos, ByteBufCodecs.FLOAT, PathNode::yaw, ByteBufCodecs.FLOAT, PathNode::pitch, ByteBufCodecs.FLOAT, PathNode::roll, PathNode::new
   );
   StreamCodec<RegistryFriendlyByteBuf, Optional<UUID>> OPTIONAL_UUID = optional(UUIDUtil.STREAM_CODEC);

   static <T> StreamCodec<RegistryFriendlyByteBuf, Optional<T>> optional(final StreamCodec<ByteBuf, T> streamCodec) {
      return new StreamCodec<RegistryFriendlyByteBuf, Optional<T>>() {
         public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull Optional<T> optional) {
            buf.writeBoolean(optional.isPresent());
            optional.ifPresent(value -> streamCodec.encode(buf, value));
         }

         @NotNull
         public Optional<T> decode(@NotNull RegistryFriendlyByteBuf buf) {
            return buf.readBoolean() ? Optional.of(streamCodec.decode(buf)) : Optional.empty();
         }
      };
   }
}

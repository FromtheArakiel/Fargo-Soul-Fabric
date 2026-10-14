package dev.arakiel.fargosoulfabric.network;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.SoulKey;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A soul key was pressed; the server decides which soul answers it. */
public record KeyHandlePacket(@Nullable SoulKey key) implements CustomPacketPayload {

    public static final Type<KeyHandlePacket> TYPE = new Type<>(FSF.rl("key_handle"));

    public static final StreamCodec<ByteBuf, KeyHandlePacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> buf.writeByte(packet.key() == null ? -1 : packet.key().ordinal()),
            buf -> {
                int ordinal = buf.readByte();
                SoulKey[] keys = SoulKey.values();
                return new KeyHandlePacket(ordinal >= 0 && ordinal < keys.length ? keys[ordinal] : null);
            });

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(KeyHandlePacket payload, ServerPlayNetworking.Context context) {
        SoulKey key = payload.key();
        Player player = context.player();
        // A packet that carries no known binding, or one from a player that cannot act right now,
        // is dropped instead of being applied.
        if (key == null || !player.isAlive() || player.isSpectator()) {
            return;
        }
        PlayerSouls.forEach(player, soulItem -> {
            ResourceLocation id = soulItem.keyAction(player, key);
            if (id != null) {
                soulItem.handleKeyAction(player, id);
            }
        });
    }
}

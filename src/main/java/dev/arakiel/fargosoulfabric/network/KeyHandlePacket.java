package dev.arakiel.fargosoulfabric.network;

import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tells the server that the player pressed one of the soul keys.
 *
 * <p>Only the binding travels: which soul answers it is decided on the server, where the list of
 * equipped souls is the authoritative one. Resolving it on the client would tie every active soul
 * to the client copy of that list, which can be missing or stale.
 *
 * <p>The binding is read defensively. A client is free to send any number, and an exception while
 * decoding a payload would take the connection down, so an unknown number becomes an empty packet
 * that {@link #handle} ignores.
 */
public record KeyHandlePacket(@Nullable SoulKey key) implements CustomPacketPayload {

    public static final Type<KeyHandlePacket> TYPE = new Type<>(FargoSoul.rl("key_handle"));

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
        SoulItemData.forEach(player, soulItem -> {
            ResourceLocation id = soulItem.keyPressed(player, key);
            if (id != null) {
                soulItem.keyHandle(player, id);
            }
        });
    }
}

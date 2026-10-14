package dev.arakiel.fargosoulfabric.network;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.entity.Sprint;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Tells the server that the player started a soul sprint. */
public record SprintPacket() implements CustomPacketPayload {

    /**
     * The client decides when a sprint starts, so the server has to bound how often it accepts one:
     * every accepted packet spawns an entity and runs the soul hooks.
     */
    private static final int MIN_TICKS_BETWEEN_PACKETS = 10;

    private static final int MAX_TRACKED_PLAYERS = 512;

    private static final Map<UUID, Integer> LAST_ACCEPTED = new HashMap<>();

    public static final Type<SprintPacket> TYPE = new Type<>(FSF.rl("sprint"));

    public static final StreamCodec<ByteBuf, SprintPacket> STREAM_CODEC = StreamCodec.unit(new SprintPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SprintPacket payload, ServerPlayNetworking.Context context) {
        Player player = context.player();
        if (!player.isAlive() || player.isSpectator()) {
            return;
        }
        Integer last = LAST_ACCEPTED.get(player.getUUID());
        if (last != null && player.tickCount - last < MIN_TICKS_BETWEEN_PACKETS) {
            return;
        }
        // Only a player whose equipped souls actually grant a sprint may start one. The client uses
        // the same test before it sends anything, so this only ever drops forged packets.
        boolean[] canSprint = {false};
        PlayerSouls.forEach(player, soulItem -> {
            if (soulItem.canSprint(player)) {
                canSprint[0] = true;
            }
        });
        if (!canSprint[0]) {
            return;
        }
        if (LAST_ACCEPTED.size() > MAX_TRACKED_PLAYERS) {
            LAST_ACCEPTED.clear();
        }
        LAST_ACCEPTED.put(player.getUUID(), player.tickCount);
        PlayerSouls.forEach(player, soulItem -> soulItem.sprintServer(player));
        Sprint sprint = new Sprint();
        sprint.setPos(player.getBoundingBox().getCenter());
        sprint.join(player);
    }
}

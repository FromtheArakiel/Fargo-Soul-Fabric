package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.common.network.DamageNumbersPayload;
import dev.arakiel.fargosoulfabric.common.network.ParticleBatchPayload;
import dev.arakiel.fargosoulfabric.network.KeyHandlePacket;
import dev.arakiel.fargosoulfabric.network.SprintPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/** Both directions of the mod's own traffic. */
public final class FSFNetworkPacketRegister {

    private FSFNetworkPacketRegister() {
    }

    public static void init() {
        PayloadTypeRegistry.playC2S().register(KeyHandlePacket.TYPE, KeyHandlePacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SprintPacket.TYPE, SprintPacket.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(KeyHandlePacket.TYPE, KeyHandlePacket::handle);
        ServerPlayNetworking.registerGlobalReceiver(SprintPacket.TYPE, SprintPacket::handle);
        // Batched to every client in the level: the summon/mark particles and the damage numbers.
        PayloadTypeRegistry.playS2C().register(ParticleBatchPayload.TYPE, ParticleBatchPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(DamageNumbersPayload.TYPE, DamageNumbersPayload.STREAM_CODEC);
    }
}

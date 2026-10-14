package dev.arakiel.fargosoulfabric.client;

import dev.arakiel.fargosoulfabric.common.FSFHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.player.Input;

/**
 * The client only hooks of the summon system.
 *
 * <p>They live in their own class because they reference client classes: a dedicated server must be
 * able to load the rest of the mod without ever touching them.
 */
public final class FSFClientHooks {

    /** Fired while the fog of the client viewport is set up. */
    @FunctionalInterface
    public interface Fog {
        void onFog(Camera camera, FSFHooks.Value nearPlane, FSFHooks.Value farPlane, FSFHooks.Value cancel);
    }

    /** Fired after the client updated its movement input. */
    @FunctionalInterface
    public interface MovementInput {
        void onMovementInput(net.minecraft.world.entity.player.Player player, Input input);
    }

    public static final Event<Fog> FOG = EventFactory.createArrayBacked(Fog.class,
            listeners -> (camera, nearPlane, farPlane, cancel) -> {
                for (Fog listener : listeners) {
                    listener.onFog(camera, nearPlane, farPlane, cancel);
                }
            });

    public static final Event<MovementInput> MOVEMENT_INPUT = EventFactory.createArrayBacked(MovementInput.class,
            listeners -> (player, input) -> {
                for (MovementInput listener : listeners) {
                    listener.onMovementInput(player, input);
                }
            });

    private FSFClientHooks() {
    }
}

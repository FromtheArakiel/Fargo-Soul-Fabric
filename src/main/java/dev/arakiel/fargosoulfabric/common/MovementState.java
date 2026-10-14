package dev.arakiel.fargosoulfabric.common;

/**
 * The movement input of a player.
 *
 * <p>Vanilla keeps it in {@code Input}, which only exists on the client. The item hooks that need
 * it receive this record instead, so they can stay on the common side.
 *
 * @param forward     whether the player is pushing forward
 * @param jumping     whether the jump key is held
 * @param shiftKeyDown whether the sneak key is held
 */
public record MovementState(boolean forward, boolean jumping, boolean shiftKeyDown) {
}

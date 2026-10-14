package dev.arakiel.fargosoulfabric.common.component;

import java.util.function.Supplier;

/**
 * The time source of the animated rarity colours.
 *
 * <p>Some rarities animate their colour, which needs the tick counter of the running client. The
 * client installs the source when it starts up; on a dedicated server the field stays empty and the
 * static colour of the rarity is used.
 */
public final class RarityAnimation {

    private static volatile Supplier<Double> timeSource;

    private RarityAnimation() {
    }

    /** Installs the client time source. Called once, from the client initializer. */
    public static void setTimeSource(Supplier<Double> source) {
        timeSource = source;
    }

    /**
     * The client game time with the partial tick applied.
     *
     * @return the time, or {@code null} when no client is running
     */
    public static Double currentTime() {
        Supplier<Double> source = timeSource;
        return source == null ? null : source.get();
    }
}

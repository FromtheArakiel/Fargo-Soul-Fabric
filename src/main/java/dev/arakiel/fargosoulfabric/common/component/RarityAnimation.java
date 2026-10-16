package dev.arakiel.fargosoulfabric.common.component;

import java.util.function.Supplier;

public final class RarityAnimation {

    private static volatile Supplier<Double> timeSource;

    private RarityAnimation() {
    }

    /** Installs the client time source. Called once, from the client initializer. */
    public static void setTimeSource(Supplier<Double> source) {
        timeSource = source;
    }

    public static Double currentTime() {
        Supplier<Double> source = timeSource;
        return source == null ? null : source.get();
    }
}

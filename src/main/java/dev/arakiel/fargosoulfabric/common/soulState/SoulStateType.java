package dev.arakiel.fargosoulfabric.common.soulState;

import java.util.function.Supplier;

public record SoulStateType<T extends SoulState>(Supplier<T> supplier, boolean isClientSide) {

    public SoulStateType(Supplier<T> supplier) {
        this(supplier, false);
    }
}

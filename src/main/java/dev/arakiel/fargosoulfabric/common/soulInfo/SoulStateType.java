package dev.arakiel.fargosoulfabric.common.soulInfo;

import java.util.function.Supplier;

/**
 * SoulState 类型注册项。
 *
 * @param isClientSide 是否为纯客户端数据：true 表示该类型只存在于客户端本地，
 *                   由客户端自身 tick 管理，不参与服务端存档与同步；
 *                   false 表示服务端权威数据，由服务端 tick 并同步覆写到客户端。
 */
public record SoulStateType<T extends SoulState>(Supplier<T> supplier, boolean isClientSide) {

    public SoulStateType(Supplier<T> supplier) {
        this(supplier, false);
    }
}

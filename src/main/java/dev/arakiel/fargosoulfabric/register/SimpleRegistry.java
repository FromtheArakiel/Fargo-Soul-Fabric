package dev.arakiel.fargosoulfabric.register;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * A small, id based registry.
 *
 * <p>Fabric has no way to create brand new game registries, so the value types that only need to be
 * looked up by name (the soul info types and the attachment entity types) live in here instead. It
 * behaves like a plain {@code Map<ResourceLocation, T>} with the lookups the game code needs.
 *
 * @param <T> type of the registered values
 */
public final class SimpleRegistry<T> {

    private final ResourceKey<Registry<T>> key;

    private final Map<ResourceLocation, T> byId = new LinkedHashMap<>();

    private final Map<T, ResourceLocation> ids = new IdentityHashMap<>();

    public SimpleRegistry(ResourceKey<Registry<T>> key) {
        this.key = key;
    }

    public T register(String namespace, String path, T value) {
        return this.register(ResourceLocation.fromNamespaceAndPath(namespace, path), value);
    }

    public T register(ResourceLocation id, T value) {
        T previous = this.byId.putIfAbsent(id, value);
        if (previous != null) {
            throw new IllegalStateException("Duplicate registration of " + id);
        }
        this.ids.put(value, id);
        return value;
    }

    public T get(ResourceLocation id) {
        return id == null ? null : this.byId.get(id);
    }

    public T getOrThrow(ResourceLocation id) {
        T value = this.get(id);
        if (value == null) {
            throw new IllegalStateException("Unknown id " + id + " in " + this.key.location());
        }
        return value;
    }

    public ResourceLocation getKey(T value) {
        return this.ids.get(value);
    }

    public ResourceKey<Registry<T>> key() {
        return this.key;
    }

    public Collection<T> values() {
        return this.byId.values();
    }

    public Set<ResourceLocation> ids() {
        return this.byId.keySet();
    }
}

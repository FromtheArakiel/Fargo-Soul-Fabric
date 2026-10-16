package dev.arakiel.fargosoulfabric.register;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

import com.mojang.serialization.MapCodec;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.common.component.FSFRarity;
import dev.arakiel.fargosoulfabric.common.component.MinionWeapon;
import dev.arakiel.fargosoulfabric.common.particle.generic.GenericParticleOptions;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

public final class FSFRegisters {

    public static final ResourceKey<Registry<SoulStateType<?>>> SOUL_INFO_TYPE_KEY =
            ResourceKey.createRegistryKey(FSF.rl("soul_info_type"));

    public static final NamedRegistry<SoulStateType<?>> SOUL_INFO_TYPE = new NamedRegistry<>(SOUL_INFO_TYPE_KEY);

    private static final ResourceKey<Registry<SummonType<? extends Summon>>> ATTACHMENT_ENTITY_TYPE_KEY =
            ResourceKey.createRegistryKey(FSF.rl("attachment_entity_types"));

    /** The summon types the renderers and the weapons look up by name. */
    public static final NamedRegistry<SummonType<? extends Summon>> ATTACHMENT_ENTITY_TYPES =
            new NamedRegistry<>(ATTACHMENT_ENTITY_TYPE_KEY);

    public static final DataComponentType<MinionWeapon> MINION_WEAPON = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, FSF.rl("minion_weapon"),
            DataComponentType.<MinionWeapon>builder()
                    .persistent(MinionWeapon.CODEC)
                    .networkSynchronized(MinionWeapon.STREAM_CODEC)
                    .build());

    public static final DataComponentType<FSFRarity> RARITY = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, FSF.rl("rarity"),
            DataComponentType.<FSFRarity>builder()
                    .persistent(FSFRarity.CODEC)
                    .networkSynchronized(FSFRarity.STREAM_CODEC)
                    .build());

    public static final ParticleType<GenericParticleOptions> GENERIC_PARTICLE =
            Registry.register(BuiltInRegistries.PARTICLE_TYPE, FSF.rl("generic"),
                    new ParticleType<GenericParticleOptions>(true) {

                        @Override
                        public @NotNull MapCodec<GenericParticleOptions> codec() {
                            return GenericParticleOptions.CODEC;
                        }

                        @Override
                        public @NotNull StreamCodec<RegistryFriendlyByteBuf, GenericParticleOptions> streamCodec() {
                            return GenericParticleOptions.STREAM_CODEC;
                        }
                    });

    private FSFRegisters() {
    }

    public static void init() {
        // Every registry is a field of this class, so touching it loads them all, in declaration
        // order. The item registrations rely on that: they read the components declared above.
    }

public static class NamedRegistry<T> {

    private final ResourceKey<Registry<T>> key;

    private final Map<ResourceLocation, T> byId = new LinkedHashMap<>();

    private final Map<T, ResourceLocation> ids = new IdentityHashMap<>();

    public NamedRegistry(ResourceKey<Registry<T>> key) {
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
}

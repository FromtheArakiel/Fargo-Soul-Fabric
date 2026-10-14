package dev.arakiel.fargosoulfabric.register;

import com.mojang.serialization.MapCodec;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.common.dataComponent.FargoSoulRarity;
import dev.arakiel.fargosoulfabric.common.dataComponent.MinionWeapon;
import dev.arakiel.fargosoulfabric.common.particle.genericParticle.GenericParticleOptions;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

/**
 * The registries this mod adds.
 *
 * <p>Fabric cannot add game registries, so the soul info types and the summon types live in a
 * {@link SimpleRegistry}, which is all the serialisation needs: look a type up by name, and name a
 * type. The item components and the particle type are real vanilla registries and are registered
 * straight into them.
 */
public final class FargoSoulRegisters {

    public static final ResourceKey<Registry<SoulInfoType<?>>> SOUL_INFO_TYPE_KEY =
            ResourceKey.createRegistryKey(FargoSoul.rl("soul_info_type"));

    public static final SimpleRegistry<SoulInfoType<?>> SOUL_INFO_TYPE = new SimpleRegistry<>(SOUL_INFO_TYPE_KEY);

    private static final ResourceKey<Registry<AttachmentEntityType<? extends AttachmentEntity>>> ATTACHMENT_ENTITY_TYPE_KEY =
            ResourceKey.createRegistryKey(FargoSoul.rl("attachment_entity_types"));

    /** The summon types the renderers and the weapons look up by name. */
    public static final SimpleRegistry<AttachmentEntityType<? extends AttachmentEntity>> ATTACHMENT_ENTITY_TYPES =
            new SimpleRegistry<>(ATTACHMENT_ENTITY_TYPE_KEY);

    public static final DataComponentType<MinionWeapon> MINION_WEAPON = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, FargoSoul.rl("minion_weapon"),
            DataComponentType.<MinionWeapon>builder()
                    .persistent(MinionWeapon.CODEC)
                    .networkSynchronized(MinionWeapon.STREAM_CODEC)
                    .build());

    public static final DataComponentType<FargoSoulRarity> RARITY = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE, FargoSoul.rl("rarity"),
            DataComponentType.<FargoSoulRarity>builder()
                    .persistent(FargoSoulRarity.CODEC)
                    .networkSynchronized(FargoSoulRarity.STREAM_CODEC)
                    .build());

    public static final ParticleType<GenericParticleOptions> GENERIC_PARTICLE =
            Registry.register(BuiltInRegistries.PARTICLE_TYPE, FargoSoul.rl("generic"),
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

    private FargoSoulRegisters() {
    }

    public static void init() {
        // Every registry is a field of this class, so touching it loads them all, in declaration
        // order. The item registrations rely on that: they read the components declared above.
    }
}

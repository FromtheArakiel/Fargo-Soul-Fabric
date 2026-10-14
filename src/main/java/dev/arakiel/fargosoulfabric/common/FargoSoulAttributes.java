package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

/**
 * Adds the summon attributes to the entity types that carry them.
 *
 * <p>Vanilla builds one immutable {@link AttributeSupplier} per entity type, so the patched
 * suppliers are built lazily: the first time the game asks for the attributes of an entity type,
 * the supplier of the game is copied and the extra attributes are added.
 */
public final class FargoSoulAttributes {

    private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier> PATCHED = new WeakHashMap<>();

    private static final Map<EntityType<? extends LivingEntity>, Map<Holder<Attribute>, Double>> EXTRA =
            new java.util.HashMap<>();

    private FargoSoulAttributes() {
    }

    /** Declares one more attribute for an entity type. Called while the mods initialize. */
    public static void add(EntityType<? extends LivingEntity> type, Holder<Attribute> attribute) {
        EXTRA.computeIfAbsent(type, key -> new java.util.LinkedHashMap<>()).put(attribute, 0.0D);
        PATCHED.remove(type);
    }

    /** The supplier the game should use for {@code type}, with the summon attributes added. */
    public static AttributeSupplier patch(EntityType<? extends LivingEntity> type, AttributeSupplier original) {
        if (original == null) {
            return null;
        }
        AttributeSupplier cached = PATCHED.get(type);
        if (cached != null) {
            return cached;
        }
        AttributeSupplier.Builder builder = new AttributeSupplier.Builder();
        for (Holder.Reference<Attribute> attribute : BuiltInRegistries.ATTRIBUTE.holders().toList()) {
            if (original.hasAttribute(attribute)) {
                builder.add(attribute, original.getBaseValue(attribute));
            }
        }
        builder.add(FargoSoulAttributeRegister.HealthRegen, 0.0D);
        EXTRA.getOrDefault(type, java.util.Map.of()).forEach(builder::add);
        if (type == EntityType.PLAYER) {
            builder.add(FargoSoulAttributeRegister.MinionMaxCount, 1.0D);
            builder.add(FargoSoulAttributeRegister.SentryMaxCount, 1.0D);
            builder.add(FargoSoulAttributeRegister.SummonDamage, 1.0D);
            builder.add(FargoSoulAttributeRegister.SummonKnockback, 1.0D);
            builder.add(FargoSoulAttributeRegister.SummonArmorPierce, 0.0D);
            builder.add(FargoSoulAttributeRegister.SummonSearchRange, 1.0D);
        }
        AttributeSupplier patched = builder.build();
        PATCHED.put(type, patched);
        return patched;
    }
}
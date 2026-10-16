package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public final class FSFEntityAttributes {

    private static final Map<EntityType<? extends LivingEntity>, AttributeSupplier> PATCHED = new WeakHashMap<>();

    private static final Map<EntityType<? extends LivingEntity>, Map<Holder<Attribute>, Double>> EXTRA =
            new java.util.HashMap<>();

    private FSFEntityAttributes() {
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
        builder.add(FSFAttributeRegister.HealthRegen, 0.0D);
        EXTRA.getOrDefault(type, java.util.Map.of()).forEach(builder::add);
        if (type == EntityType.PLAYER) {
            builder.add(FSFAttributeRegister.MinionMaxCount, 1.0D);
            builder.add(FSFAttributeRegister.SentryMaxCount, 1.0D);
            builder.add(FSFAttributeRegister.SummonDamage, 1.0D);
            builder.add(FSFAttributeRegister.SummonKnockback, 1.0D);
            builder.add(FSFAttributeRegister.SummonArmorPierce, 0.0D);
            builder.add(FSFAttributeRegister.SummonSearchRange, 1.0D);
        }
        AttributeSupplier patched = builder.build();
        PATCHED.put(type, patched);
        return patched;
    }
}
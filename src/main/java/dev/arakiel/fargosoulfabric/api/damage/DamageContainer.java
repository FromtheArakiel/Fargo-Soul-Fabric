package dev.arakiel.fargosoulfabric.api.damage;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * Carries one hit through the damage pipeline.
 *
 * <p>The hooks fire before vanilla applies armour and the other reductions, so a listener sees the
 * damage as it was dealt and may change it, cancel it or take a share of it away.
 */
public class DamageContainer {

    private final DamageSource source;

    private final LivingEntity entity;

    private final float originalDamage;

    private float newDamage;

    private float blockedDamage;

    private float shieldDamage;

    private boolean canceled;

    private final Map<Reduction, Float> reductions = new EnumMap<>(Reduction.class);

    public DamageContainer(LivingEntity entity, DamageSource source, float originalDamage) {
        this.entity = entity;
        this.source = source;
        this.originalDamage = originalDamage;
        this.newDamage = originalDamage;
    }

    /** The entity the hit is dealt to. */
    public LivingEntity getEntity() {
        return this.entity;
    }

    public DamageSource getSource() {
        return this.source;
    }

    public float getOriginalDamage() {
        return this.originalDamage;
    }

    public float getNewDamage() {
        return this.newDamage;
    }

    public void setNewDamage(float newDamage) {
        this.newDamage = newDamage;
    }

    public void addDamage(float delta) {
        this.newDamage += delta;
    }

    public boolean isCanceled() {
        return this.canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public float getBlockedDamage() {
        return this.blockedDamage;
    }

    public float getShieldDamage() {
        return this.shieldDamage;
    }

    public void setBlockedDamage(float blockedDamage, float shieldDamage) {
        this.blockedDamage = blockedDamage;
        this.shieldDamage = shieldDamage;
        this.setReduction(Reduction.BLOCKED, blockedDamage);
    }

    public void addModifier(Reduction reduction, IReductionFunction function) {
        float reduced = 1.0F - function.getReduction(this);
        float previous = this.getReduction(reduction);
        this.setReduction(reduction, 1.0F - previous * reduced);
    }

    public float getReduction(Reduction reduction) {
        return this.reductions.getOrDefault(reduction, 0.0F);
    }

    public void setReduction(Reduction reduction, float amount) {
        this.reductions.put(reduction, amount);
    }

    /** Total share of the hit the reductions of this container take away. */
    public float getTotalReduction() {
        float remaining = 1.0F;
        for (float reduction : this.reductions.values()) {
            remaining *= 1.0F - reduction;
        }
        return 1.0F - remaining;
    }

    /** The damage left after every reduction registered on this hit. */
    public float getReducedDamage() {
        return this.newDamage * (1.0F - this.getTotalReduction());
    }

    /** The stages a hit passes through, in the order the game applies them. */
    public enum Reduction {
        ARMOR,
        ARMOR_PIERCE,
        ENCHANTMENTS,
        MOB_EFFECTS,
        ABSORPTION,
        BLOCKED,
        INVULNERABILITY
    }
}

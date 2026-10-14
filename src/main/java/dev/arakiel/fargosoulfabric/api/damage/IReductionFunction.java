package dev.arakiel.fargosoulfabric.api.damage;

/** Computes how much of a hit one of the {@link DamageContainer.Reduction} stages removes. */
@FunctionalInterface
public interface IReductionFunction {

    float getReduction(DamageContainer container);
}

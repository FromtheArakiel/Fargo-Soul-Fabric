package dev.arakiel.fargosoulfabric.common.summon;

import net.minecraft.world.damagesource.DamageSource;

@FunctionalInterface
public interface DamageSourceProvider {
   DamageSource getDamageSource();
}

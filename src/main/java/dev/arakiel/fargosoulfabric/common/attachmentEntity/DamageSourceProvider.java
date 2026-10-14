package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import net.minecraft.world.damagesource.DamageSource;

@FunctionalInterface
public interface DamageSourceProvider {
   DamageSource getDamageSource();
}

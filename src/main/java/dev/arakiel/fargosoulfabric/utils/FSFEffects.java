package dev.arakiel.fargosoulfabric.utils;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Sends the short vanilla particle burst that marks a soul skill going off. */
public final class FSFEffects {

    private FSFEffects() {
    }

    /** One puff of {@code particle} at {@code pos}. */
    public static void burst(Level level, Vec3 pos, ParticleOptions particle, int count,
                             double spread, double speed) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, pos.x, pos.y, pos.z, count, spread, spread, spread, speed);
        }
    }

    /** A cloud of {@code particle} around an entity, centred on its body. */
    public static void aura(Level level, Entity entity, ParticleOptions particle, int count,
                            double radius, double speed) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(particle, entity.getX(), entity.getY() + entity.getBbHeight() * 0.5D,
                    entity.getZ(), count, radius, entity.getBbHeight() * 0.5D, radius, speed);
        }
    }
}

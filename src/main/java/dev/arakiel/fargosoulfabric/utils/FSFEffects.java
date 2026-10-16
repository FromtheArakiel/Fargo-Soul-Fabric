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

    /** One circle of {@code particle} at {@code radius} around {@code centre}. */
    public static void ring(Level level, Vec3 centre, double radius, ParticleOptions particle,
                            int points, double spread) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        for (int i = 0; i < points; i++) {
            double angle = Math.TAU * i / points;
            serverLevel.sendParticles(particle, centre.x + Math.cos(angle) * radius,
                    centre.y + 0.1D, centre.z + Math.sin(angle) * radius, 1, spread, 0.02D, spread, 0.0D);
        }
    }

    public static void arc(Level level, Vec3 from, Vec3 to, ParticleOptions particle,
                           int points, double seed) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        Vec3 line = to.subtract(from);
        double length = line.length();

        if (length < 1.0E-4) {
            return;
        }
        Vec3 direction = line.scale(1.0D / length);
        Vec3 side = direction.cross(new Vec3(0.31D, 1.0D, 0.17D));

        if (side.lengthSqr() < 1.0E-6D) {
            side = direction.cross(new Vec3(1.0D, 0.24D, 0.53D));
        }
        side = side.normalize().yRot((float) (seed * Math.TAU)).scale(0.18D + length * 0.05D);
        double phase = seed * Math.TAU + level.getGameTime() * 0.06D;

        for (int i = 0; i <= points; i++) {
            double t = (double) i / points;
            double bow = Math.sin(t * Math.PI) * (1.0D + 0.35D * Math.sin(phase + t * 4.0D));
            Vec3 point = from.add(line.scale(t)).add(side.scale(bow));
            serverLevel.sendParticles(particle, point.x, point.y, point.z, 1, 0.002D, 0.002D, 0.002D, 0.0D);
        }
    }
}

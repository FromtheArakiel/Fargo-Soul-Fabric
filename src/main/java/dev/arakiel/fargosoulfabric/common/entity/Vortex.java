package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import dev.arakiel.fargosoulfabric.common.particle.generic.GenericParticleBuilder;
import dev.arakiel.fargosoulfabric.common.projectile.SummonProjectile;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

@SuppressWarnings("this-escape")
public class Vortex extends SummonProjectile {

    public Vortex() {
        super(SummonTypeRegister.VORTEX);
        setMaxLife(100);
        setDrag(1);
    }

    @Override
    public void tick() {
        Vec3 center = getPos();
        if (!owner.level().isClientSide()) {
            List<LivingEntity> targets = SoulTargets.get(owner).getEntitiesInRadius(center, 6, null);
            for (LivingEntity target : targets) {
                Vec3 normalize = center.subtract(target.getBoundingBox().getCenter()).normalize();
                target.addDeltaMovement(normalize);
                target.hurtMarked = true;
                Invulnerability.attack(target)
                        .attacker(getUuid())
                        .damageSource(getDamageSource())
                        .damageAmount(getDamage())
                        .invincibleTime(4)
                        .apply();
            }
        } else {
            for (int ring = 0; ring < 6; ring++) {
                double radius = 0.5 + ring;
                double angularSpeed = 0.45 - ring * 0.07;
                double height = ring * 0.35;
                spawnRingParticles(center, radius, height, angularSpeed, 48 - ring * 2);
            }
        }
        super.tick();
    }

    private void spawnRingParticles(Vec3 visualCenter, double radius, double height, double angularSpeed, int count) {
        double now = owner.level().getGameTime() * angularSpeed;
        for (int i = 0; i < count; i++) {
            double angle = now + (Math.PI * 2 * i) / count;
            double nextRadius = radius * (0.95 + 0.1 * Math.sin(angle * 3 + i));
            double radialIn = -nextRadius * 0.05;
            Vec3 base = visualCenter.add(nextRadius * Math.cos(angle), height * (0.7 + 0.3 * Math.sin(i * 2.7)), nextRadius * Math.sin(angle));
            double tanX = -Math.sin(angle);
            double tanZ = Math.cos(angle);
            Vec3 dir = new Vec3(tanX * angularSpeed * 0.55 + radialIn * Math.cos(angle), 0.02, tanZ * angularSpeed * 0.55 + radialIn * Math.sin(angle));
            if (dir.lengthSqr() < 1e-6) {
                continue;
            }
            ParticleHelper.create(owner.level())
                    .generic(GenericParticleBuilder.create()
                            .centerColor(0x00F4A8)
                            .edgeColor(0x0DC790)
                            .lifetime(5)
                            .lifetimeRandom(10)
                            .spin(0.4f)
                            .spinRandom(0.2f)
                            .friction(0.7F)
                            .scale(0.02f)
                            .scaleRandom(0.01f))
                    .pos(base)
                    .velocity(dir)
                    .speed(0.5)
                    .emit();
        }
    }
}

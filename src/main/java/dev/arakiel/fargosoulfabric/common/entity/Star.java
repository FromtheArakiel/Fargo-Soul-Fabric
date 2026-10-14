package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import dev.arakiel.fargosoulfabric.common.summon.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.particle.genericParticle.GenericParticleBuilder;
import dev.arakiel.fargosoulfabric.common.projectile.SummonProjectile;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("this-escape")
public class Star extends SummonProjectile implements IEntityCollision<Star> {

    private LivingEntity chaseTarget = null;

    public Star() {
        super(SummonTypeRegister.STAR);
        setDrag(0.8f);
        setMaxLife(100);
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);
    }

    @Override
    public boolean isValidCollisionTarget(Star entity, LivingEntity target) {
        return target == chaseTarget;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        DamageSource source = getDamageSource();
        if (source != null) {
            Vec3 pos = getPos();
            List<LivingEntity> entities = SoulTargets.get(owner).getEntitiesInRadius(pos, 3, null);
            for (LivingEntity entity : entities) {
                Invulnerability.attack(entity)
                        .attacker(getUuid())
                        .damageSource(source)
                        .damageAmount(getDamage())
                        .invincibleTime(5)
                        .apply();
            }
            owner.addEffect(new MobEffectInstance(FSFMobEffectRegister.Starlight, 200));
            ParticleHelper.create(owner.level())
                    .generic(GenericParticleBuilder.create()
                                     .centerColor(0xe7db09)
                                     .edgeColor(0xe0681e)
                                     .lifetime(10)
                                     .lifetimeRandom(20)
                                     .spin(0.3f)
                                     .spinRandom(0.05F)
                                     .friction(0.85F)
                                     .scale(0.025f)
                                     .scaleRandom(0.005f)
                    )
                    .pos(pos)
                    .offset(0.15)
                    .velocity(pos.offsetRandom(owner.getRandom(), 5).subtract(pos).normalize())
                    .count(80)
                    .speed(0.35)
                    .spread(Math.TAU)
                    .emit();
        }
        setCurrentPathNode(getCollisionAfterPathNode(hitContexts.getFirst().hitPoint(),getCurrentPathNode()));
        setRemove();
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            if (chaseTarget != null && chaseTarget.isAlive()) {
                Vec3 targetCenter = chaseTarget.getBoundingBox().getCenter();
                applyForce(targetCenter.subtract(getPos()).normalize().scale(0.6));
            } else {
                setRemove();
            }
        }
        super.tick();
    }

    public int getTrailDuration() {
        return 12;
    }

    public void setChaseTarget(LivingEntity chaseTarget) {
        this.chaseTarget = chaseTarget;
    }
}

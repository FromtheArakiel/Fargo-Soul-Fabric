package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.projectile.SummonProjectile;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("this-escape")
public class TrackingBlood extends SummonProjectile {

    public TrackingBlood() {
        super(SummonTypeRegister.TRACKING_BLOOD);
        setDrag(0.75f);
        setMaxLife(60);
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            Vec3 targetCenter = owner.getBoundingBox().getCenter();
            // One thin bowed thread per ball; the ball's own id seeds its curve, so every thread keeps
            // its own bend on the way from the enemy it was born on back to the player.
            applyForce(targetCenter.subtract(getPos()).normalize().scale(0.25));
            if (targetCenter.distanceTo(getPos()) < owner.getBoundingBox().getSize()) {
                owner.heal(3.0f);
                PathNode pathNode = getCurrentPathNode();
                setCurrentPathNode(new PathNode(targetCenter, pathNode.yaw(), pathNode.pitch(), pathNode.roll()));
                setVelocity(Vec3.ZERO);
                setRemove();
            }
        }
        super.tick();
    }
}

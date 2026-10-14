package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulTargetCache;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.common.attachment.InvincibleData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.projectile.Projectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("this-escape")
public class GhostOrb extends Projectile implements IEntityCollision<GhostOrb> {

    private LivingEntity chaseTarget = null;

    public GhostOrb() {
        super(SummonerAttachmentEntityRegister.GHOST_ORB);
        setDrag(0.8f);
        setMaxLife(80);
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.05, -0.05, -0.05, 0.05, 0.05, 0.05);
    }

    @Override
    public boolean isValidCollisionTarget(GhostOrb entity, LivingEntity target) {
        return SoulTargetCache.isTarget(owner, target);
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        DamageSource source = getDamageSource();
        if (source != null) {
            InvincibleData.attack(hitContexts.getFirst().entity())
                    .attacker(getUuid())
                    .damageSource(source)
                    .damageAmount(getDamage())
                    .invincibleTime(5)
                    .apply();
        }
        setRemove();
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            if (chaseTarget != null && chaseTarget.isAlive()) {
                if (getTickCount() > 5) {
                    Vec3 targetCenter = chaseTarget.getBoundingBox().getCenter();
                    applyForce(targetCenter.subtract(getPos()).normalize().scale(0.6));
                }
            } else {
                setRemove();
            }
        }
        super.tick();
    }

    public int getTrailDuration() {
        return 8;
    }

    public void setChaseTarget(LivingEntity chaseTarget) {
        this.chaseTarget = chaseTarget;
    }
}

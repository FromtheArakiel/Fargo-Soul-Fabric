package dev.arakiel.fargosoulfabric.common.entity;

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
public class BloodDrop extends Projectile implements IEntityCollision<BloodDrop> {

    private LivingEntity chaseTarget = null;

    public BloodDrop() {
        super(SummonerAttachmentEntityRegister.BLOOD_DROP);
        setDrag(0.8f);
        setMaxLife(60);
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.04, -0.04, -0.04, 0.04, 0.04, 0.04);
    }

    @Override
    public boolean isValidCollisionTarget(BloodDrop entity, LivingEntity target) {
        return target == chaseTarget;
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        DamageSource source = getDamageSource();
        HitContext first = hitContexts.getFirst();
        if (source != null) {
            InvincibleData.attack(first.entity())
                    .attacker(getUuid())
                    .damageSource(source)
                    .damageAmount(getDamage())
                    .invincibleTime(5)
                    .apply();
        }
        setCurrentPathNode(getCollisionAfterPathNode(first.hitPoint(), getCurrentPathNode()));
        setRemove();
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            if (chaseTarget != null && chaseTarget.isAlive()) {
                if (getTickCount() > 10) {
                    Vec3 targetCenter = chaseTarget.getBoundingBox().getCenter();
                    applyForce(targetCenter.subtract(getPos()).normalize().scale(0.5));
                }
            } else {
                setRemove();
            }
        }
        super.tick();
    }

    public int getTrailDuration() {
        return 4;
    }

    public void setChaseTarget(LivingEntity chaseTarget) {
        this.chaseTarget = chaseTarget;
    }
}

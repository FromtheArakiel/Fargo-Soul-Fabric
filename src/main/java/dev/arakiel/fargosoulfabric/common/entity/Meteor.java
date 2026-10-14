package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import dev.arakiel.fargosoulfabric.common.summon.IBlockCollision;
import dev.arakiel.fargosoulfabric.common.summon.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.particle.genericParticle.GenericParticleBuilder;
import dev.arakiel.fargosoulfabric.common.projectile.SummonProjectile;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 流星射弹（星坠）：从目标周围上方生成，向目标位置直线移动（无追踪），
 * 命中实体或落地时对命中点半径 3 格内所有目标造成伤害（伤害在创建时按 133% 结算伤害设置）。
 */
@SuppressWarnings("this-escape")
public class Meteor extends SummonProjectile implements IEntityCollision<Meteor>, IBlockCollision<Meteor> {

    public Meteor() {
        super(SummonTypeRegister.METEOR);
        setDrag(1);
        setMaxLife(60);
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
    }

    @Override
    public @NotNull AABB getBlockCollisionBox() {
        return new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);
    }

    @Override
    public boolean isValidCollisionTarget(Meteor entity, LivingEntity target) {
        return SoulTargets.isTarget(owner, target);
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        explode(hitContexts.getFirst().hitPoint());
    }

    @Override
    public void onBlockCollision(CollisionContext context) {
        explode(context.position());
    }

    private void explode(Vec3 pos) {
        DamageSource source = getDamageSource();
        if (source != null) {
            List<LivingEntity> targets = SoulTargets.get(owner).getEntitiesInRadius(pos, 3, null);
            for (LivingEntity target : targets) {
                Invulnerability.attack(target)
                        .attacker(getUuid())
                        .damageSource(source)
                        .damageAmount(getDamage())
                        .apply();
            }
        }
        ParticleHelper.create(owner.level())
                .generic(GenericParticleBuilder.create()
                                 .centerColor(0xea9900)
                                 .edgeColor(0xea5700)
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
        setCurrentPathNode(getCollisionAfterPathNode(pos, getCurrentPathNode()));
        setRemove();
    }
}

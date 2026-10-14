package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulTargetCache;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.common.attachment.InvincibleData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.projectile.Projectile;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("this-escape")
public class SnowBall extends Projectile implements IEntityCollision<SnowBall> {

    public SnowBall() {
        super(SummonerAttachmentEntityRegister.SNOW_BALL);
        setDrag(0.97f);
        setMaxLife(60);
    }

    @Override
    public void tick() {
        Level level = owner.level();
        if (!level.isClientSide()) {
            Vec3 pos = getPos();
            ParticleHelper.create(level)
                    .type(ParticleTypes.ITEM_SNOWBALL)
                    .pos(pos)
                    .offset(0.2)
                    .count(2)
                    .speed(0.5)
                    .spread(Math.TAU)
                    .emit();
        }
        super.tick();
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.1, -0.1, -0.1, 0.1, 0.1, 0.1);
    }

    @Override
    public boolean isValidCollisionTarget(SnowBall entity, LivingEntity target) {
        return SoulTargetCache.isTarget(owner, target);
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        DamageSource source = getDamageSource();
        HitContext first = hitContexts.getFirst();
        LivingEntity living = first.entity();
        living.addEffect(new MobEffectInstance(FargoSoulMobEffectRegister.Chill, 70));
        if (source != null) {
            InvincibleData.attack(living)
                    .attacker(getUuid())
                    .damageSource(source)
                    .damageAmount(getDamage())
                    .invincibleTime(5)
                    .apply();
        }
        setCurrentPathNode(getCollisionAfterPathNode(first.hitPoint(), getCurrentPathNode()));
        setRemove();
    }
}

package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.entity.goal.terrablade.BladeAttackGoal;
import dev.arakiel.fargosoulfabric.common.entity.goal.terrablade.BladeIdleGoal;
import dev.arakiel.fargosoulfabric.common.entity.goal.terrablade.BladePrepGoal;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.FSFPlayerSummons;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import dev.arakiel.fargosoulfabric.common.attachment.RecentTargets;
import dev.arakiel.fargosoulfabric.common.summon.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.summon.SyncFieldDispatcher;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.summon.SummonGoalSelector;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("this-escape")
public class TerraBlade extends Minion implements IEntityCollision<TerraBlade> {

    public boolean attacking = false;
    private boolean blend = false;
    public int trailTimer = 0;
    public float idleBlend = 0f;
    public float idleBlendO = 0f;
    public final Set<LivingEntity> hitTargets = new HashSet<>();
    public boolean spiritPower = false;
    public boolean ancientHolySoul = false;

    public TerraBlade() {
        super(SummonTypeRegister.TERRA_BLADE);
        setDamage(8);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(ByteBufCodecs.BOOL, () -> attacking, value -> attacking = value);
        fields.field(ByteBufCodecs.BOOL, () -> !(getGoalSelector().getCurrentGoal() instanceof BladeIdleGoal), value -> blend = value);
    }

    @Override
    public void registerGoals(SummonGoalSelector goalSelector) {
        goalSelector.addGoal(0, new BladeAttackGoal(this));
        goalSelector.addGoal(1, new BladePrepGoal(this));
        goalSelector.addGoal(2, new BladeIdleGoal(this));
    }

    @Override
    public LivingEntity searchTarget() {
        int distance = this.getSearchDistance();
        if (distance > 0) {
            FSFPlayerSummons helper = FSFPlayerSummons.get(this.owner);
            RecentTargets targetCache = helper.getRecentTargets();
            if (!targetCache.isEmpty()) {
                List<LivingEntity> targets = targetCache.getEntitiesInRadius(this.getPos(), targetCache.getSummonSearchRange(this.getOwner(), (float) distance), (living) -> (attacking || targetCache.isVisibility(this.owner, living)) && this.isTarget(living));
                if (!targets.isEmpty()) {
                    return targetCache.getNewTarget(this, targets, 8, false);
                }
            }
        }
        return null;
    }

    @Override
    public int getSearchDistance() {
        return 32;
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.15, -0.06, -0.5, 0.15, 0.06, 1.05);
    }

    @Override
    public boolean canCollideAttack() {
        return isTarget(getTarget());
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext hit : hitContexts) {
            if (this.hitTargets.add(hit.entity())) {
                Invulnerability.attack(hit.entity())
                        .attacker(getUuid())
                        .damageSource(getDamageSource())
                        .damageAmount(getDamage())
                        .apply();
            }
        }
    }

    @Override
    public boolean isValidCollisionTarget(TerraBlade entity, LivingEntity target) {
        return isTarget(target);
    }

    @Override
    public void tick() {
        if (owner.level().isClientSide()) {
            idleBlendO = idleBlend;
            if (blend) {
                trailTimer = Math.min(trailTimer + 1, 12);
                idleBlend = Math.max(0.0f, idleBlend - 0.25f);
            } else {
                trailTimer = Math.max(trailTimer - 1, 0);
                idleBlend = Math.min(1.0f, idleBlend + 0.1f);
            }
        } else {
            if (spiritPower && !PlayerSouls.isEquipped(getOwner(), FSFItemRegister.SpiritPowerItem)){
                setRemove();
            }
            if (ancientHolySoul && !PlayerSouls.isEquipped(getOwner(), FSFItemRegister.AncientHolySoulItem)){
                setRemove();
            }
        }
        super.tick();
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        PathNode renderNode = super.getRenderNode(partialTick);
        return renderNode.lerp(getInterpolatedIdleState(partialTick), Mth.lerp(partialTick, idleBlendO, idleBlend));
    }

    public PathNode getInterpolatedIdleState(float partialTick) {
        float bodyYaw = Mth.rotLerp(partialTick, owner.yBodyRotO, owner.yBodyRot);
        float headYaw = Mth.rotLerp(partialTick, owner.yHeadRotO, owner.yHeadRot);
        float playerYaw = Mth.wrapDegrees(bodyYaw + Mth.wrapDegrees(headYaw - bodyYaw) * 0.5f);
        float rad = (float) Math.toRadians(-playerYaw + 180);
        float backX = (float) Math.sin(rad);
        float backZ = (float) Math.cos(rad);
        float rightX = (float) Math.cos(rad);
        float rightZ = (float) -Math.sin(rad);
        int order = getOrder();
        double localZ = 0.75 + order * 0.12;
        double floatSpeed = 0.08 + order * 0.01;
        double floatAngle = (owner.tickCount + partialTick) * floatSpeed + order * 1.33;
        Vec3 playerPos = owner.getPosition(partialTick);
        Vec3 targetPos = playerPos.add(localZ * backX + Math.cos(floatAngle) * 0.075 * rightX, owner.getBbHeight() * 0.6 + Math.sin(floatAngle) * 0.075, localZ * backZ + Math.cos(floatAngle) * 0.075 * rightZ);
        return new PathNode(targetPos, playerYaw - 90, 75 - order * 5f, 100);
    }

    @Override
    public int getOrder() {
        return FSFPlayerSummons.get(this.owner).getSummons().get(this.getType()).indexOf(this);
    }

    @Override
    public int getSameSize() {
        return FSFPlayerSummons.get(this.owner).getSummons().get(this.getType()).size();
    }
}

package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargetCache;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachment.InvincibleData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.SyncFieldDispatcher;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.particle.genericParticle.GenericParticleBuilder;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("this-escape")
public class ChlorophyteOrb extends Minion implements IEntityCollision<ChlorophyteOrb> {

    public static final int GREEN = 0;
    public static final int NATURE = 1;

    private int kind = GREEN;
    private int targetId = -1;
    private float orbitRadius = 0;
    private float orbitHeight = 0;
    private float laserLength = 0;

    public ChlorophyteOrb() {
        super(SummonerAttachmentEntityRegister.CHLOROPHYTE_ORB);
        setDamage(6);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(ByteBufCodecs.INT, () -> kind, value -> kind = value);
        fields.field(ByteBufCodecs.INT, () -> targetId, value -> targetId = value);
        fields.field(ByteBufCodecs.FLOAT, () -> orbitRadius, value -> orbitRadius = value);
        fields.field(ByteBufCodecs.FLOAT, () -> orbitHeight, value -> orbitHeight = value);
        fields.field(ByteBufCodecs.FLOAT, () -> laserLength, value -> laserLength = value);
    }

    @Override
    public int getSearchDistance() {
        return 16;
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            boolean equipped = kind == NATURE ? SoulItemData.isEquipped(owner, FargoSoulItemRegister.NaturePowerItem) : SoulItemData.isEquipped(owner, FargoSoulItemRegister.GreenSoulItem);
            if (!equipped) {
                setRemove();
                return;
            }
            if (kind == GREEN) {
                orbitRadius = 0;
                orbitHeight = owner.getBbHeight() + 0.75f;
            } else {
                orbitRadius = owner.getBbWidth() + 0.25f;
                orbitHeight = owner.getBbHeight() / 3;
            }
            PathNode node = getOrbitNode(1f);
            Vec3 pos = node.pos();
            LivingEntity target = getTarget();
            if (target != null) {
                targetId = target.getId();
                Vec3 targetCenter = target.getBoundingBox().getCenter();
                laserLength = (float) Math.min(20, pos.distanceTo(targetCenter));
                Vec3 dir = targetCenter.subtract(pos);
                if (dir.lengthSqr() > 1e-5) {
                    PathNode look = getEulerNode(pos, dir, new Vec3(0, 1, 0));
                    node = new PathNode(pos, look.yaw(), look.pitch(), 0);
                }
            } else {
                targetId = -1;
                laserLength = 0;
            }
            setCurrentPathNode(node);
        }
        super.tick();
    }

    /**
     * 渲染拟合：位置按 owner 插值环绕；姿态朝向玩家（yaw/pitch 指向玩家中心），roll 随时间自旋。
     */
    @Override
    public PathNode getRenderNode(float partialTick) {
        PathNode orbit = getOrbitNode(partialTick);
        Vec3 pos = orbit.pos();
        Vec3 dir = owner.getPosition(partialTick).add(0, owner.getBbHeight() / 2, 0).subtract(pos).normalize();
        if (kind == NATURE) {
            float yaw = (float) Math.toDegrees(Math.atan2(-dir.x, dir.z));
            return new PathNode(pos, yaw, 0, 0);
        } else {
            float spin = (getTickCount() + partialTick) * 12f % 360f;
            return new PathNode(pos, spin, 0, 0);
        }
    }

    private PathNode getOrbitNode(float partialTick) {
        int total = Math.max(1, kindTotal());
        int order = kindOrder();
        double angle = (owner.tickCount + partialTick) * 0.05 + order * Mth.TWO_PI / total;
        double px = Mth.lerp(partialTick, owner.xo, owner.getX());
        double py = Mth.lerp(partialTick, owner.yo, owner.getY());
        double pz = Mth.lerp(partialTick, owner.zo, owner.getZ());
        Vec3 pos = new Vec3(px, py, pz).add(Math.cos(angle) * orbitRadius, orbitHeight, Math.sin(angle) * orbitRadius);
        return new PathNode(pos, 0, 0, 0);
    }

    @Override
    public boolean canCollideAttack() {
        return getTarget() != null;
    }

    @Override
    public @NotNull AABB getHitbox() {
        if (getTarget() != null) {
            double length = Math.min(20, getPos().distanceTo(getTarget().getBoundingBox().getCenter()));
            if (length > 0.5) {
                return new AABB(-0.35, -0.35, 0, 0.35, 0.35, length);
            }
        }
        return new AABB(-0.35, -0.35, -0.35, 0.35, 0.35, 0.35);
    }

    @Override
    public boolean isValidCollisionTarget(ChlorophyteOrb entity, LivingEntity target) {
        return SoulTargetCache.isTarget(owner, target);
    }

    @Override
    public void onCollisionAttack(List<HitContext> hitContexts) {
        for (HitContext context : hitContexts) {
            LivingEntity living = context.entity();
            InvincibleData.get(living).recordHit(owner.getUUID(), 100);
            InvincibleData.attack(living)
                    .attacker(getUuid())
                    .damageSource(getDamageSource())
                    .damageAmount(getDamage())
                    .invincibleTime(6)
                    .apply();
            ParticleHelper.create(owner.level())
                    .generic(GenericParticleBuilder.create()
                                     .centerColor(0x14fb08)
                                     .edgeColor(0x0cb502)
                                     .lifetime(5)
                                     .lifetimeRandom(5)
                                     .spin(0.3f)
                                     .spinRandom(0.05F)
                                     .friction(0.75F)
                                     .scale(0.025f)
                                     .scaleRandom(0.005f)
                    )
                    .pos(living.getBoundingBox().getCenter())
                    .offset(0.15)
                    .velocity(Vec3.ZERO.offsetRandom(living.getRandom(), 1))
                    .count(10)
                    .speed(0.5)
                    .emit();
        }
    }

    private int kindOrder() {
        List<ChlorophyteOrb> list = kindList();
        int i = list.indexOf(this);
        return Math.max(i, 0);
    }

    private int kindTotal() {
        return kindList().size();
    }

    private List<ChlorophyteOrb> kindList() {
        return FargoSoulHelper.get(owner).getEntityData().get(SummonerAttachmentEntityRegister.CHLOROPHYTE_ORB).stream().filter(orb -> orb.kind == this.kind).toList();
    }

    public int getTargetId() {
        return targetId;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }
}

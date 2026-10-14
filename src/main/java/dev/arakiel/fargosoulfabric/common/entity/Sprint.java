package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargetCache;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.IEntityCollision;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import dev.arakiel.fargosoulfabric.common.projectile.Projectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("this-escape")
public class Sprint extends Projectile implements IEntityCollision<Sprint> {

    private final Map<SoulItem, Set<LivingEntity>> hits = new HashMap<>();

    public Sprint() {
        super(SummonerAttachmentEntityRegister.SPRINT);
        setMaxLife(10);
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            setCurrentPathNode(new PathNode(owner.getBoundingBox().getCenter(), 0, 0, 0));
        }
        super.tick();
    }

    @Override
    public void tickPhysics() {
    }

    @Override
    public @NotNull AABB getHitbox() {
        return new AABB(-0.3, 0, -0.3, 0.3, 1.8, 0.3);
    }

    @Override
    public boolean isValidCollisionTarget(Sprint entity, LivingEntity target) {
        return SoulTargetCache.isTarget(owner, target);
    }

    @Override
    public void onCollisionAttack(List<HitContext> list) {
        SoulItemData.forEach(owner, soulItem -> soulItem.sprintCollision(owner, list, hits.computeIfAbsent(soulItem, key -> new HashSet<>())));
    }
}

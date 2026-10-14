package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface IEntityCollision<T extends AttachmentEntity> {
   @NotNull
   AABB getHitbox();

   void onCollisionAttack(List<IEntityCollision.HitContext> var1);

   default boolean renderHitbox() {
      return true;
   }

   default boolean canCollideAttack() {
      return true;
   }

   default boolean isValidCollisionTarget(T entity, LivingEntity target) {
      return entity.getTargetCache().isTarget(target);
   }

   @SuppressWarnings("unchecked")
   default void entityCollision() {
      T entity = (T)this;
      ArrayList<PathNode> historyNodes = entity.getHistoryNodes();
      if (this.canCollideAttack() && !historyNodes.isEmpty()) {
         PathNode current = entity.currentPathNode;
         PathNode prevTick = historyNodes.getFirst();
         AABB localBox = this.getHitbox();
         Vec3 boxSize = new Vec3(localBox.getXsize(), localBox.getYsize(), localBox.getZsize());
         Vec3 boxCenterOffset = localBox.getCenter();
         boolean hasCenterOffset = boxCenterOffset.lengthSqr() > 1.0E-5;
         IEntityCollision.Sweep sweep = this.buildSweep(prevTick, current, boxSize, boxCenterOffset, hasCenterOffset);
         if (sweep == null) {
            return;
         }

         List<LivingEntity> potentialTargets = this.findPotentialTargets(entity, sweep);
         if (potentialTargets.isEmpty()) {
            return;
         }

         Map<LivingEntity, Vec3> hitPoints = new HashMap<>();

         for (LivingEntity target : potentialTargets) {
            Vec3 hitPoint = this.findHitPoint(sweep, target.getBoundingBox(), prevTick.pos());
            if (hitPoint != null) {
               hitPoints.put(target, hitPoint);
            }
         }

         if (!hitPoints.isEmpty()) {
            List<IEntityCollision.HitContext> hitContexts = hitPoints.entrySet()
               .stream()
               .sorted(Comparator.comparingDouble(e -> e.getValue().distanceToSqr(prevTick.pos())))
               .map(e -> new IEntityCollision.HitContext(e.getKey(), e.getValue()))
               .toList();
            this.onCollisionAttack(hitContexts);
         }
      }
   }

   private IEntityCollision.Sweep buildSweep(PathNode prev, PathNode current, Vec3 boxSize, Vec3 boxCenterOffset, boolean hasCenterOffset) {
      List<IEntityCollision.SampledOBB> result = new ArrayList<>();
      double yawDelta = Math.toRadians((double)Math.abs(Mth.wrapDegrees(current.yaw() - prev.yaw())));
      double pitchDelta = Math.toRadians((double)Math.abs(Mth.wrapDegrees(current.pitch() - prev.pitch())));
      double rollDelta = Math.toRadians((double)Math.abs(Mth.wrapDegrees(current.roll() - prev.roll())));
      double maxRadius = boxCenterOffset.length() + boxSize.length() * 0.5;
      double estimatedLength = prev.pos().distanceTo(current.pos()) + maxRadius * (yawDelta + pitchDelta + rollDelta);
      double minDim = Math.min(Math.min(boxSize.x, boxSize.y), boxSize.z);
      double stepSize = Math.max(minDim * 0.5, 0.001);
      int steps = Math.max(2, (int)Math.ceil(estimatedLength / stepSize));
      double minX = Double.POSITIVE_INFINITY;
      double minY = Double.POSITIVE_INFINITY;
      double minZ = Double.POSITIVE_INFINITY;
      double maxX = Double.NEGATIVE_INFINITY;
      double maxY = Double.NEGATIVE_INFINITY;
      double maxZ = Double.NEGATIVE_INFINITY;

      for (int i = 0; i <= steps; i++) {
         float t = (float)i / (float)steps;
         IEntityCollision.SampledOBB sampled = this.createSampledOBB(prev, current, t, boxSize, boxCenterOffset, hasCenterOffset);
         result.add(sampled);
         AABB bounds = sampled.bounds();
         minX = Math.min(minX, bounds.minX);
         minY = Math.min(minY, bounds.minY);
         minZ = Math.min(minZ, bounds.minZ);
         maxX = Math.max(maxX, bounds.maxX);
         maxY = Math.max(maxY, bounds.maxY);
         maxZ = Math.max(maxZ, bounds.maxZ);
      }

      return result.isEmpty() ? null : new IEntityCollision.Sweep(result, new AABB(minX, minY, minZ, maxX, maxY, maxZ));
   }

   private List<LivingEntity> findPotentialTargets(T entity, IEntityCollision.Sweep sweep) {
      return entity.getTargetCache()
         .getEntitiesInRadius(sweep.bounds().getCenter(), sweep.bounds().getSize() * 1.5, target -> this.isValidCollisionTarget(entity, target));
   }

   private IEntityCollision.SampledOBB createSampledOBB(PathNode prev, PathNode current, float t, Vec3 boxSize, Vec3 boxCenterOffset, boolean hasCenterOffset) {
      Vec3 pos = prev.pos().lerp(current.pos(), (double)t);
      float yaw = Mth.rotLerp(t, prev.yaw(), current.yaw());
      float pitch = Mth.rotLerp(t, prev.pitch(), current.pitch());
      float roll = Mth.rotLerp(t, prev.roll(), current.roll());
      Vec3 hitCenter = pos;
      if (hasCenterOffset) {
         hitCenter = pos.add(this.rotateOffset(boxCenterOffset, yaw, pitch, roll));
      }

      OBB obb = new OBB(hitCenter, boxSize, yaw, pitch, roll);
      return new IEntityCollision.SampledOBB(obb, obb.getBoundingBox(), t);
   }

   private Vec3 rotateOffset(Vec3 offset, float yaw, float pitch, float roll) {
      Quaternionf rotation = new Quaternionf()
         .rotateY((float)Math.toRadians((double)(-yaw)))
         .rotateX((float)Math.toRadians((double)pitch))
         .rotateZ((float)Math.toRadians((double)roll));
      Vector3f rotated = new Vector3f((float)offset.x, (float)offset.y, (float)offset.z).rotate(rotation);
      return new Vec3((double)rotated.x, (double)rotated.y, (double)rotated.z);
   }

   private Vec3 findHitPoint(IEntityCollision.Sweep sweep, AABB targetBox, Vec3 prevPos) {
      Vec3 closestHitPoint = null;
      double closestDist = Double.MAX_VALUE;

      for (IEntityCollision.SampledOBB sampled : sweep.samples()) {
         if (overlaps(sampled.bounds(), targetBox) && sampled.obb().intersects(targetBox)) {
            Vec3 hitPoint = this.getClosestPointOnAABB(sampled.obb(), targetBox);
            double dist = hitPoint.distanceToSqr(prevPos);
            if (dist < closestDist) {
               closestDist = dist;
               closestHitPoint = hitPoint;
            }
         }
      }

      return closestHitPoint;
   }

   private Vec3 getClosestPointOnAABB(OBB obb, AABB box) {
      return new Vec3(
         Mth.clamp((double)obb.center.x, box.minX, box.maxX),
         Mth.clamp((double)obb.center.y, box.minY, box.maxY),
         Mth.clamp((double)obb.center.z, box.minZ, box.maxZ)
      );
   }

   private static boolean overlaps(AABB first, AABB second) {
      return first.minX <= second.maxX
         && first.maxX >= second.minX
         && first.minY <= second.maxY
         && first.maxY >= second.minY
         && first.minZ <= second.maxZ
         && first.maxZ >= second.minZ;
   }

   public static record HitContext(LivingEntity entity, Vec3 hitPoint) {
   }

   public static record SampledOBB(OBB obb, AABB bounds, float t) {
   }

   public static record Sweep(List<IEntityCollision.SampledOBB> samples, AABB bounds) {
   }
}

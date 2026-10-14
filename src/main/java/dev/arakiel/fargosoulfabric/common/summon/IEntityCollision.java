package dev.arakiel.fargosoulfabric.common.summon;

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

public interface IEntityCollision<T extends Summon> {
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
      return entity.getRecentTargets().isTarget(target);
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
      return entity.getRecentTargets()
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


public static class OBB {
   public final Vector3f center;
   public final Vector3f extents;
   public final Vector3f axisX;
   public final Vector3f axisY;
   public final Vector3f axisZ;

   public OBB(Vec3 centerPos, Vec3 size, float yaw, float pitch, float roll) {
      this.center = new Vector3f((float)centerPos.x, (float)centerPos.y, (float)centerPos.z);
      this.extents = new Vector3f((float)size.x / 2.0F, (float)size.y / 2.0F, (float)size.z / 2.0F);
      Quaternionf q = new Quaternionf()
         .rotateY((float)Math.toRadians((double)(-yaw)))
         .rotateX((float)Math.toRadians((double)pitch))
         .rotateZ((float)Math.toRadians((double)roll));
      this.axisX = new Vector3f(1.0F, 0.0F, 0.0F).rotate(q);
      this.axisY = new Vector3f(0.0F, 1.0F, 0.0F).rotate(q);
      this.axisZ = new Vector3f(0.0F, 0.0F, 1.0F).rotate(q);
   }

   public AABB getBoundingBox() {
      float dx = Math.abs(this.axisX.x * this.extents.x) + Math.abs(this.axisY.x * this.extents.y) + Math.abs(this.axisZ.x * this.extents.z);
      float dy = Math.abs(this.axisX.y * this.extents.x) + Math.abs(this.axisY.y * this.extents.y) + Math.abs(this.axisZ.y * this.extents.z);
      float dz = Math.abs(this.axisX.z * this.extents.x) + Math.abs(this.axisY.z * this.extents.y) + Math.abs(this.axisZ.z * this.extents.z);
      return new AABB(
         (double)(this.center.x - dx),
         (double)(this.center.y - dy),
         (double)(this.center.z - dz),
         (double)(this.center.x + dx),
         (double)(this.center.y + dy),
         (double)(this.center.z + dz)
      );
   }

   public boolean intersects(AABB aabb) {
      Vector3f aC = new Vector3f((float)aabb.getCenter().x, (float)aabb.getCenter().y, (float)aabb.getCenter().z);
      Vector3f aE = new Vector3f((float)(aabb.getXsize() / 2.0), (float)(aabb.getYsize() / 2.0), (float)(aabb.getZsize() / 2.0));
      Vector3f t = new Vector3f(this.center).sub(aC);
      float[][] R = new float[3][3];
      float[][] absR = new float[3][3];
      Vector3f[] v = new Vector3f[]{this.axisX, this.axisY, this.axisZ};

      for (int i = 0; i < 3; i++) {
         R[0][i] = v[i].x;
         R[1][i] = v[i].y;
         R[2][i] = v[i].z;
         absR[0][i] = Math.abs(R[0][i]) + 1.0E-5F;
         absR[1][i] = Math.abs(R[1][i]) + 1.0E-5F;
         absR[2][i] = Math.abs(R[2][i]) + 1.0E-5F;
      }

      float[] tA = new float[]{t.x, t.y, t.z};
      float[] eA = new float[]{aE.x, aE.y, aE.z};
      float[] eB = new float[]{this.extents.x, this.extents.y, this.extents.z};

      for (int i = 0; i < 3; i++) {
         if (Math.abs(tA[i]) > eA[i] + eB[0] * absR[i][0] + eB[1] * absR[i][1] + eB[2] * absR[i][2]) {
            return false;
         }
      }

      for (int ix = 0; ix < 3; ix++) {
         if (Math.abs(tA[0] * R[0][ix] + tA[1] * R[1][ix] + tA[2] * R[2][ix]) > eA[0] * absR[0][ix] + eA[1] * absR[1][ix] + eA[2] * absR[2][ix] + eB[ix]) {
            return false;
         }
      }

      if (Math.abs(tA[2] * R[1][0] - tA[1] * R[2][0]) > eA[1] * absR[2][0] + eA[2] * absR[1][0] + eB[1] * absR[0][2] + eB[2] * absR[0][1]) {
         return false;
      } else if (Math.abs(tA[2] * R[1][1] - tA[1] * R[2][1]) > eA[1] * absR[2][1] + eA[2] * absR[1][1] + eB[0] * absR[0][2] + eB[2] * absR[0][0]) {
         return false;
      } else if (Math.abs(tA[2] * R[1][2] - tA[1] * R[2][2]) > eA[1] * absR[2][2] + eA[2] * absR[1][2] + eB[0] * absR[0][1] + eB[1] * absR[0][0]) {
         return false;
      } else if (Math.abs(tA[0] * R[2][0] - tA[2] * R[0][0]) > eA[0] * absR[2][0] + eA[2] * absR[0][0] + eB[1] * absR[1][2] + eB[2] * absR[1][1]) {
         return false;
      } else if (Math.abs(tA[0] * R[2][1] - tA[2] * R[0][1]) > eA[0] * absR[2][1] + eA[2] * absR[0][1] + eB[0] * absR[1][2] + eB[2] * absR[1][0]) {
         return false;
      } else if (Math.abs(tA[0] * R[2][2] - tA[2] * R[0][2]) > eA[0] * absR[2][2] + eA[2] * absR[0][2] + eB[0] * absR[1][1] + eB[1] * absR[1][0]) {
         return false;
      } else if (Math.abs(tA[1] * R[0][0] - tA[0] * R[1][0]) > eA[0] * absR[1][0] + eA[1] * absR[0][0] + eB[1] * absR[2][2] + eB[2] * absR[2][1]) {
         return false;
      } else {
         return Math.abs(tA[1] * R[0][1] - tA[0] * R[1][1]) > eA[0] * absR[1][1] + eA[1] * absR[0][1] + eB[0] * absR[2][2] + eB[2] * absR[2][0]
            ? false
            : !(Math.abs(tA[1] * R[0][2] - tA[0] * R[1][2]) > eA[0] * absR[1][2] + eA[1] * absR[0][2] + eB[0] * absR[2][1] + eB[1] * absR[2][0]);
      }
   }
}
}


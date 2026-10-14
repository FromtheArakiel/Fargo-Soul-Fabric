package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public record PathNode(Vec3 pos, float yaw, float pitch, float roll) {
   public PathNode(Vec3 pos, Vec3 direction) {
      this(pos, yawFromDirection(direction), pitchFromDirection(direction), 0.0F);
   }


   public static float yawFromDirection(Vec3 direction) {
      return (float)Math.toDegrees(Math.atan2(-direction.x, direction.z));
   }

   public static float pitchFromDirection(Vec3 direction) {
      if (direction.lengthSqr() < 1.0E-12) {
         return 0.0F;
      } else {
         Vec3 normalized = direction.normalize();
         return (float)Math.toDegrees(Math.asin(-normalized.y));
      }
   }

   public Quaternionf toQuaternion() {
      return new Quaternionf()
         .rotateY((float)Math.toRadians((double)(-this.yaw)))
         .rotateX((float)Math.toRadians((double)this.pitch))
         .rotateZ((float)Math.toRadians((double)this.roll));
   }

   public PathNode lerp(PathNode to, float partialTick) {
      Vec3 pos = this.pos().lerp(to.pos(), (double)partialTick);
      Quaternionf from = this.toQuaternion();
      Quaternionf target = to.toQuaternion();
      if (from.x * target.x + from.y * target.y + from.z * target.z + from.w * target.w < 0.0F) {
         target.set(-target.x, -target.y, -target.z, -target.w);
      }

      return fromQuaternion(pos, from.slerp(target, partialTick));
   }

   private static PathNode fromQuaternion(Vec3 pos, Quaternionf rotation) {
      Vector3f forward = rotation.transform(new Vector3f(0.0F, 0.0F, 1.0F));
      Vector3f up = rotation.transform(new Vector3f(0.0F, 1.0F, 0.0F));
      float yaw = (float)Math.toDegrees(Math.atan2((double)(-forward.x), (double)forward.z));
      float pitch = (float)Math.toDegrees(Math.asin((double)Mth.clamp(-forward.y, -1.0F, 1.0F)));
      double yawRad = Math.toRadians((double)yaw);
      double pitchRad = Math.toRadians((double)pitch);
      Vector3f localY = new Vector3f((float)(-Math.sin(yawRad) * Math.sin(pitchRad)), (float)Math.cos(pitchRad), (float)(Math.cos(yawRad) * Math.sin(pitchRad)));
      Vector3f cross = localY.cross(up, new Vector3f());
      float roll = (float)Math.toDegrees(Math.atan2((double)cross.dot(forward), (double)localY.dot(up)));
      return new PathNode(pos, yaw, pitch, roll);
   }

   public PathNode modifyPos(Vec3 pos) {
      return new PathNode(pos, this.yaw, this.pitch, this.roll);
   }

   public PathNode modifyEuler(float yaw, float pitch, float roll) {
      return new PathNode(this.pos, yaw, pitch, roll);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (!(obj instanceof PathNode other)) {
         return false;
      }
      return this.pos.equals(other.pos) && this.yaw == other.yaw
              && this.pitch == other.pitch && this.roll == other.roll;
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(this.pos, this.yaw, this.pitch, this.roll);
   }

}

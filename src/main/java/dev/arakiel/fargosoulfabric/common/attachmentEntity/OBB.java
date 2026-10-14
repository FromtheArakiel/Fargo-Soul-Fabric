package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class OBB {
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

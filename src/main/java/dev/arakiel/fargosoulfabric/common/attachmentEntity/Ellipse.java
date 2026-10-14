package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Ellipse {
   private final Vec3 pointA;
   private final Vec3 pointB;
   private final Vec3 planeNormal;
   private final float curvature;
   private final Vec3 center;
   private final Vec3 major;
   private final Vec3 minor;
   private final Vec3 majorDir;
   private final Vec3 minorDir;

   public Ellipse(Vec3 pointA, Vec3 pointB, Vec3 planeNormal, float curvature) {
      this.pointA = pointA;
      this.pointB = pointB;
      this.planeNormal = planeNormal;
      this.curvature = curvature;
      this.major = pointB.subtract(pointA).scale(0.5);
      this.center = pointA.add(this.major);
      this.majorDir = this.major.normalize();
      Vec3 mDir = planeNormal.cross(this.majorDir).normalize();
      if (mDir.lengthSqr() < 1.0E-5) {
         mDir = this.majorDir.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
      }

      this.minorDir = mDir;
      this.minor = this.minorDir.scale(this.major.length() * (double)curvature);
   }

   @NotNull
   public static Vec3 randomPlaneNormal(RandomSource random, Vec3 pointA, Vec3 pointB) {
      Vec3 majorDir = pointB.subtract(pointA).normalize();
      float angle = random.nextFloat() * (float) (Math.PI * 2);
      Quaternionf q = new Quaternionf().rotateAxis(angle, (float)majorDir.x, (float)majorDir.y, (float)majorDir.z);
      Vec3 initNormal = majorDir.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
      if (initNormal.lengthSqr() < 1.0E-5) {
         initNormal = majorDir.cross(new Vec3(1.0, 0.0, 0.0)).normalize();
      }

      Vector3f rotated = new Vector3f((float)initNormal.x, (float)initNormal.y, (float)initNormal.z).rotate(q);
      return new Vec3((double)rotated.x(), (double)rotated.y(), (double)rotated.z()).normalize();
   }

   public Vec3 getPoint(float progress) {
      float biasedT = progress - 0.08F * Mth.sin(progress * (float) (Math.PI * 2));
      float theta = biasedT * (float) (Math.PI * 2);
      return this.center.add(this.major.scale(Math.cos((double)theta))).add(this.minor.scale(Math.sin((double)theta)));
   }

   public Vec3 getTangent(float progress) {
      float biasedT = progress - 0.08F * Mth.sin(progress * (float) (Math.PI * 2));
      float theta = biasedT * (float) (Math.PI * 2);
      return this.major.scale(-Math.sin((double)theta)).add(this.minor.scale(Math.cos((double)theta))).normalize();
   }

   public Vec3 getCenter() {
      return this.center;
   }

   public Vec3 getMajorDir() {
      return this.majorDir;
   }

   public Vec3 getMinorDir() {
      return this.minorDir;
   }

   public Vec3 getPlaneNormal() {
      return this.planeNormal;
   }

   public float getCurvature() {
      return this.curvature;
   }

   public Vec3 getPointA() {
      return this.pointA;
   }

   public Vec3 getPointB() {
      return this.pointB;
   }

   public Vec3 getFocusNearA() {
      double c = Math.sqrt(Math.max(0.0, this.major.lengthSqr() - this.minor.lengthSqr()));
      return this.center.add(this.majorDir.scale(-c));
   }

   public Vec3 getFocusNearB() {
      double c = Math.sqrt(Math.max(0.0, this.major.lengthSqr() - this.minor.lengthSqr()));
      return this.center.add(this.majorDir.scale(c));
   }
}

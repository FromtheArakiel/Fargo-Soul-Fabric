package dev.arakiel.fargosoulfabric.common.minion;

import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.common.summon.IMomentumSummon;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.summon.PlannedPath;
import java.util.Collections;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class MomentumMinion extends Minion implements IMomentumSummon {
   private float desiredYaw = 0.0F;
   private float desiredPitch = 0.0F;
   private float desiredRoll = 0.0F;
   private Vec3 velocity = Vec3.ZERO;
   private boolean physics = true;
   private float drag = 0.92F;
   private float gravity = -0.08F;

   public MomentumMinion(SummonType<?> type) {
      super(type);
   }

   @Override
   public void tick() {
      super.tick();
      this.tickDirection();
      this.tickPhysics();
   }

   private void tickDirection() {
      PathNode pathNode = this.getCurrentPathNode();
      float yaw = pathNode.yaw();
      float pitch = pathNode.pitch();
      float roll = pathNode.roll();
      float rotationSpeed = 36.0F;
      yaw += Mth.clamp(Mth.wrapDegrees(this.desiredYaw - yaw), -rotationSpeed, rotationSpeed);
      pitch += Mth.clamp(Mth.wrapDegrees(this.desiredPitch - pitch), -rotationSpeed, rotationSpeed);
      roll += Mth.clamp(Mth.wrapDegrees(this.desiredRoll - roll), -rotationSpeed, rotationSpeed);
      this.setCurrentPathNode(pathNode.modifyEuler(yaw, pitch, roll));
   }

   public void tickPhysics() {
      if (this.physics) {
         this.velocity = this.velocity.add(0.0, (double)this.gravity, 0.0).scale((double)this.drag);
         Vec3 newPos = this.getPos().add(this.velocity);
         this.setPath(new PlannedPath("physics", Collections.singletonList(new PathNode(newPos, this.getYaw(), this.getPitch(), this.getRoll()))));
      }
   }

   public boolean isPhysics() {
      return this.physics;
   }

   public void setPhysics(boolean physics) {
      this.physics = physics;
   }

   @NotNull
   @Override
   public Vec3 getVelocity() {
      return this.velocity;
   }

   @Override
   public void setVelocity(@NotNull Vec3 velocity) {
      this.velocity = velocity;
   }

   @Override
   public void addVelocity(@NotNull Vec3 velocity) {
      this.velocity = this.velocity.add(velocity);
   }

   @Override
   public float getDrag() {
      return this.drag;
   }

   @Override
   public void setDrag(float drag) {
      this.drag = Math.clamp(drag, 0.0F, 1.0F);
   }

   @Override
   public float getGravity() {
      return this.gravity;
   }

   @Override
   public void setGravity(float gravity) {
      this.gravity = gravity;
   }

   public void lookAtPos(Vec3 targetPos) {
      this.lookAtDirection(targetPos.subtract(this.getPos()).normalize());
   }

   public void lookAtDirection(Vec3 direction) {
      float targetYaw = (float)Math.toDegrees(Math.atan2(-direction.x, direction.z));
      float targetPitch = (float)Math.toDegrees(Math.asin(-direction.y));
      this.setDesiredRotation(targetYaw, targetPitch, this.getRoll());
   }

   public void setDesiredRotation(float yaw, float pitch, float roll) {
      this.desiredYaw = yaw;
      this.desiredPitch = pitch;
      this.desiredRoll = roll;
   }

   @Deprecated
   public Vec3 getWanderPos(Vec3 lastWanderPos, Vec3 targetPos, float distance, float height) {
      if (!lastWanderPos.equals(Vec3.ZERO)
         && !(this.getRandom().nextDouble() < 0.025)
         && !(lastWanderPos.distanceToSqr(targetPos) > (double)(distance * distance))) {
         return lastWanderPos;
      } else {
         Vec3 newPos = targetPos.add(targetPos.offsetRandom(this.getRandom(), 1.0F).subtract(targetPos).normalize().scale((double)distance));

         while (newPos.y() < targetPos.y() + (double)height) {
            newPos = newPos.add(0.0, 1.0, 0.0);
         }

         return newPos;
      }
   }
}

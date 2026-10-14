package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import dev.arakiel.fargosoulfabric.utils.FargoSoulStreamCodecs;
import java.util.Collections;
import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class MomentumAttachmentEntity extends AttachmentEntity implements IMomentumAttachmentEntity {
   private Vec3 velocity = Vec3.ZERO;
   private boolean physics = true;
   private float drag = 0.92F;
   private float gravity = -0.08F;

   public MomentumAttachmentEntity(AttachmentEntityType<?> type) {
      super(type);
   }

   @Override
   protected void registerSyncFields(SyncFieldDispatcher fields) {
      super.registerSyncFields(fields);
      fields.field(FargoSoulStreamCodecs.VEC_3, this::getVelocity, this::setVelocity);
   }

   @Override
   public void tick() {
      super.tick();
      this.tickPhysics();
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

   /** Adds a force to the movement of this entity, in blocks per tick. */
   public void applyForce(Vec3 force) {
      this.addVelocity(force);
   }

   /**
    * The path node the entity continues with after it hit something.
    *
    * <p>The movement stops at the hit point, which keeps the entity on the surface it touched
    * instead of passing through it.
    */
   public PathNode getCollisionAfterPathNode(Vec3 hitPoint, PathNode current) {
      return current.modifyPos(hitPoint);
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
}

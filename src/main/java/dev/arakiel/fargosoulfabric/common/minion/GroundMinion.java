package dev.arakiel.fargosoulfabric.common.minion;

import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.common.summon.IBlockCollision;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.summon.SyncFieldDispatcher;
import dev.arakiel.fargosoulfabric.utils.FSFStreamCodecs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class GroundMinion extends MomentumMinion implements IBlockCollision<GroundMinion> {
   private boolean flying = false;
   private boolean isWalking = false;
   private boolean onGround = false;

   public GroundMinion(SummonType<?> type) {
      super(type);
   }

   @Override
   protected void registerSyncFields(SyncFieldDispatcher fields) {
      super.registerSyncFields(fields);
      fields.field(FSFStreamCodecs.BOOL, this::isWalking, this::setWalking);
      fields.field(FSFStreamCodecs.BOOL, this::isFlying, this::setFlying);
   }

   @Override
   public void tick() {
      this.setWalking(false);
      this.setPhysics(true);
      this.setFlying(false);
      super.tick();
      this.onGround = false;
   }

   public void moveTo(Vec3 pos, float speed) {
      Vec3 horizontal = new Vec3(pos.x - this.getPos().x, 0.0, pos.z - this.getPos().z);
      if (horizontal.lengthSqr() > 1.0E-6) {
         this.setWalking(true);
         this.addVelocity(horizontal.normalize().scale((double)speed));
         this.lookAtPos(pos);
      }
   }

   public void flyTo(Vec3 pos, float speed) {
      this.setFlying(true);
      Vec3 subtract = pos.subtract(this.getPos()).normalize().scale((double)speed);
      if (pos.distanceTo(this.getPos()) < 1.0) {
         this.setVelocity(this.getVelocity().scale(0.8F));
      }

      this.addVelocity(subtract.add(0.0, (double)(-this.getGravity()), 0.0));
   }

   @Override
   public void lookAtDirection(Vec3 direction) {
      this.setDesiredRotation((float)Math.toDegrees(Math.atan2(-direction.x, direction.z)), 0.0F, this.getRoll());
   }

   @Override
   public boolean canCollideWithBlocks() {
      return this.isPhysics();
   }

   @Override
   public double getStepHeight() {
      return 1.0;
   }

   @Override
   public void onBlockCollision(IBlockCollision.CollisionContext context) {
      this.onGround = context.bottomSupported();
   }

   public boolean isWalking() {
      return this.isWalking;
   }

   public void setWalking(boolean walking) {
      this.isWalking = walking;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void setFlying(boolean flying) {
      this.flying = flying;
   }

   public boolean isFlying() {
      return this.flying;
   }

   @NotNull
   @Override
   public AABB getBlockCollisionBox() {
      return new AABB(-0.3, 0.0, -0.3, 0.3, 0.8, 0.3);
   }

   @Override
   public void blockCollision() {
      ArrayList<PathNode> history = this.getHistoryNodes();
      if (!this.isFlying() && this.canCollideWithBlocks() && !history.isEmpty()) {
         Vec3 from = history.getFirst().pos();
         Vec3 motion = this.getCurrentPathNode().pos().subtract(from);
         Level level = this.getLevel();
         Vec3 correctedMotion = Entity.collideBoundingBox(null, motion, this.getBlockCollisionBox().move(from), level, List.of());
         boolean collisionX = correctedMotion.x != motion.x;
         boolean collisionY = correctedMotion.y != motion.y;
         boolean collisionZ = correctedMotion.z != motion.z;
         double stepHeight = this.getStepHeight();
         boolean stepped = false;
         if (stepHeight > 0.0 && this.isWalking() && motion.y <= 0.0 && (collisionX || collisionZ)) {
            Vec3 horizontalMotion = new Vec3(motion.x, 0.0, motion.z);
            Vec3 steppedMotion = Entity.collideBoundingBox(
               null, horizontalMotion, this.getBlockCollisionBox().move(from.add(0.0, stepHeight, 0.0)), level, List.of()
            );
            if (steppedMotion.x == motion.x && steppedMotion.z == motion.z) {
               Vec3 steppedPos = from.add(0.0, stepHeight, 0.0).add(steppedMotion);
               if (Entity.collideBoundingBox(null, new Vec3(0.0, -0.05, 0.0), this.getBlockCollisionBox().move(steppedPos), level, List.of()).y != -0.05) {
                  correctedMotion = new Vec3(motion.x, stepHeight, motion.z);
                  collisionX = false;
                  collisionY = false;
                  collisionZ = false;
                  stepped = true;
               }
            }
         }

         Vec3 correctedPos = from.add(correctedMotion);
         this.currentPathNode = new PathNode(correctedPos, this.currentPathNode.yaw(), this.currentPathNode.pitch(), this.currentPathNode.roll());
         boolean onGround = !stepped && motion.y < 0.0 && collisionY;
         if (onGround) {
            Vec3 velocity = this.getVelocity();
            this.setVelocity(new Vec3(velocity.x() * 0.5, velocity.y(), velocity.z() * 0.5));
         }

         float elasticity = this.getElasticity();
         Vec3 velocity = this.getVelocity();
         this.setVelocity(
            new Vec3(
               collisionX ? this.resolveVelocity(velocity.x, motion.x, elasticity) : velocity.x,
               collisionY ? this.resolveVelocity(velocity.y, motion.y, elasticity) : velocity.y,
               collisionZ ? this.resolveVelocity(velocity.z, motion.z, elasticity) : velocity.z
            )
         );
         if (collisionX || collisionY || collisionZ) {
            this.onBlockCollision(new IBlockCollision.CollisionContext(correctedPos, collisionX, collisionY, collisionZ, onGround));
         }
      }
   }
}

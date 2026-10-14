package dev.arakiel.fargosoulfabric.common.summon;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public interface IBlockCollision<T extends Summon & IMomentumSummon> {
   @NotNull
   AABB getBlockCollisionBox();

   default boolean canCollideWithBlocks() {
      return true;
   }

   default void onBlockCollision(IBlockCollision.CollisionContext context) {
   }

   default float getElasticity() {
      return 0.0F;
   }

   default double getStepHeight() {
      return 0.0;
   }

   default double resolveVelocity(double velocity, double motion, float elasticity) {
      double result = velocity;
      if (velocity * motion > 0.0) {
         result = Math.abs(velocity) * (double)elasticity < 0.01 ? 0.0 : -velocity * (double)elasticity;
      }

      return result;
   }

   @SuppressWarnings("unchecked")
   default void blockCollision() {
      T entity = (T)this;
      ArrayList<PathNode> history = entity.getHistoryNodes();
      if (this.canCollideWithBlocks() && !history.isEmpty()) {
         Vec3 from = history.getFirst().pos();
         Vec3 motion = entity.getCurrentPathNode().pos().subtract(from);
         Level level = entity.getLevel();
         Vec3 correctedMotion = Entity.collideBoundingBox(null, motion, this.getBlockCollisionBox().move(from), level, List.of());
         Vec3 correctedPos = from.add(correctedMotion);
         entity.currentPathNode = new PathNode(correctedPos, entity.currentPathNode.yaw(), entity.currentPathNode.pitch(), entity.currentPathNode.roll());
         boolean collisionX = correctedMotion.x != motion.x;
         boolean collisionY = correctedMotion.y != motion.y;
         boolean collisionZ = correctedMotion.z != motion.z;
         boolean onGround = motion.y < 0.0 && collisionY;
         if (onGround) {
            Vec3 velocity = entity.getVelocity();
            entity.setVelocity(new Vec3(velocity.x() * 0.8, velocity.y(), velocity.z() * 0.8));
         }

         float elasticity = this.getElasticity();
         Vec3 velocity = entity.getVelocity();
         entity.setVelocity(
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

   public static record CollisionContext(Vec3 position, boolean collisionX, boolean collisionY, boolean collisionZ, boolean bottomSupported) {
      public CollisionContext(Vec3 position, boolean collisionX, boolean collisionY, boolean collisionZ, boolean bottomSupported) {
         this.position = position;
         this.collisionX = collisionX;
         this.collisionY = collisionY;
         this.collisionZ = collisionZ;
         this.bottomSupported = bottomSupported;
      }
   }
}

package dev.arakiel.fargosoulfabric.common.projectile;

import dev.arakiel.fargosoulfabric.register.FSFDamageSources;
import dev.arakiel.fargosoulfabric.common.summon.SummonDamageSource;
import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.common.summon.MomentumSummon;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.register.FSFDamageSources;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class SummonProjectile extends MomentumSummon {
   protected int maxTickCount = 200;
   private java.util.function.Function<net.minecraft.world.entity.player.Player, DamageSource> damageSourceSupplier;

   public SummonProjectile(SummonType<?> type) {
      super(type);
   }

   @NotNull
   @Override
   public DamageSource getDamageSource() {
      if (this.damageSourceSupplier != null && this.owner != null) {
         return this.damageSourceSupplier.apply(this.owner);
      }
      return new SummonDamageSource(
         FSFDamageSources.getDamageTypeHolder(FSFDamageSources.Summon, this.getLevel()), null, this.owner, this.getPos(), this
      );
   }

   /** Makes this projectile use the damage source the given function builds. */
   public void setDamageSourceSupplier(
         java.util.function.Function<net.minecraft.world.entity.player.Player, DamageSource> supplier) {
      this.damageSourceSupplier = supplier;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getMaxTickCount() > 0 && this.getTickCount() >= this.getMaxTickCount()) {
         this.setRemove();
      }
   }

   @Override
   public void tickPhysics() {
      super.tickPhysics();
      if (this.isPhysics()) {
         Vec3 velocity = this.getVelocity();
         float yaw = (float)Math.toDegrees(Math.atan2(-velocity.x, velocity.z));
         float pitch = (float)Math.toDegrees(Math.atan2(-velocity.y, Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z)));
         PathNode pathNode = this.getCurrentPathNode();
         this.setCurrentPathNode(pathNode.modifyEuler(yaw, pitch, pathNode.roll()));
      }
   }

   @Override
   public void onLevelChange() {
      this.setRemove();
   }

   public void setMaxTickCount(int maxTickCount) {
      this.maxTickCount = maxTickCount;
   }

   public int getMaxTickCount() {
      return this.maxTickCount;
   }

   /** Lifetime of the projectile, in ticks. */
   public void setMaxLife(int maxLife) {
      this.setMaxTickCount(maxLife);
   }

   public int getMaxLife() {
      return this.getMaxTickCount();
   }
}

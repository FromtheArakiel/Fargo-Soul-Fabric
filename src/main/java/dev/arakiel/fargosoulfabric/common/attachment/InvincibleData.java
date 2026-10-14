package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulDamageSources;
import dev.arakiel.fargosoulfabric.api.Attachments;
import dev.arakiel.fargosoulfabric.register.FargoSoulDamageSources;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InvincibleData {
   private final Long2IntOpenHashMap hurtHistory = new Long2IntOpenHashMap();
   private final Long2IntOpenHashMap partialInvincibleFrames = new Long2IntOpenHashMap();
   private int globalInvincibleFrames = 0;

   public static void handler(LivingEntity entity, DamageSource damageSource) {
      Level level = entity.level();
      if (!level.isClientSide() && damageSource.getEntity() instanceof LivingEntity attacker) {
         get(entity).recordHit(attacker.getUUID(), 100);
      }
   }

   public InvincibleData() {
   }

   public void tick() {
      if (!this.hurtHistory.isEmpty()) {
         this.hurtHistory.replaceAll((k, v) -> v - 1);
         this.hurtHistory.values().removeIf(v -> v <= 0);
      }

      if (!this.partialInvincibleFrames.isEmpty()) {
         this.partialInvincibleFrames.replaceAll((k, v) -> v - 1);
         this.partialInvincibleFrames.values().removeIf(v -> v <= 0);
      }

      if (this.globalInvincibleFrames > 0) {
         this.globalInvincibleFrames--;
      }
   }

   public static InvincibleData get(LivingEntity living) {
      return Attachments.get(living, FargoSoulAttachmentRegister.INVINCIBLE_DATA);
   }

   public boolean hasAttack(UUID uuid) {
      return this.hurtHistory.containsKey(uuid.getMostSignificantBits());
   }

   public void recordHit(@NotNull UUID uuid, int ticks) {
      this.hurtHistory.put(uuid.getMostSignificantBits(), ticks);
   }

   public static InvincibleData.AttackBuilder attack(LivingEntity target) {
      return new InvincibleData.AttackBuilder(target);
   }

   public static final class AttackBuilder {
      @NotNull
      private final LivingEntity target;
      @Nullable
      private UUID uuid = null;
      private int invincibleTime = 0;
      @Nullable
      private DamageSource damageSource = null;
      private float damageAmount = 0.0F;
      @NotNull
      private InvincibleData.Type type = InvincibleData.Type.PARTIAL;
      @Nullable
      private MobEffectInstance mobEffectInstance = null;

      private AttackBuilder(@NotNull LivingEntity target) {
         this.target = target;
      }

      public InvincibleData.AttackBuilder attacker(@Nullable UUID uuid) {
         this.uuid = uuid;
         return this;
      }

      public InvincibleData.AttackBuilder damageSource(@Nullable DamageSource damageSource) {
         this.damageSource = damageSource;
         return this;
      }

      public InvincibleData.AttackBuilder damageAmount(float damageAmount) {
         this.damageAmount = damageAmount;
         return this;
      }

      public InvincibleData.AttackBuilder invincibleTime(int ticks) {
         this.invincibleTime = ticks;
         return this;
      }

      public InvincibleData.AttackBuilder global() {
         this.type = InvincibleData.Type.GLOBAL;
         return this;
      }

      public InvincibleData.AttackBuilder effect(@Nullable MobEffectInstance mobEffectInstance) {
         this.mobEffectInstance = mobEffectInstance;
         return this;
      }

      public boolean apply() {
         if (this.target.isAlive() && this.damageAmount > 0.0F) {
            InvincibleData invincibleData = InvincibleData.get(this.target);
            boolean canDamage = this.uuid == null;
            if (!canDamage) {
               long uuidKey = this.uuid.getMostSignificantBits();

               canDamage = switch (this.type) {
                  case PARTIAL -> !invincibleData.partialInvincibleFrames.containsKey(uuidKey);
                  case GLOBAL -> invincibleData.globalInvincibleFrames <= 0;
               };
            }

            if (canDamage) {
               Level level = this.target.level();
               if (this.damageSource == null) {
                  this.damageSource = FargoSoulDamageSources.getDamageSource(DamageTypes.GENERIC, level);
               }

               int invulnerableTime = this.target.invulnerableTime;
               this.target.invulnerableTime = 0;
               boolean hurt = this.target.hurt(this.damageSource, this.damageAmount);
               this.target.invulnerableTime = invulnerableTime;
               if (hurt) {
                  if (this.mobEffectInstance != null) {
                     this.target.addEffect(this.mobEffectInstance);
                  }

                  if (this.invincibleTime > 0) {
                     if (this.type == InvincibleData.Type.PARTIAL && this.uuid != null) {
                        invincibleData.partialInvincibleFrames.put(this.uuid.getMostSignificantBits(), this.invincibleTime);
                     } else {
                        invincibleData.globalInvincibleFrames = this.invincibleTime;
                     }
                  }
               }

               return hurt;
            }
         }

         return false;
      }
   }

   public static enum Type {
      PARTIAL,
      GLOBAL;

      private Type() {
      }
   }
}

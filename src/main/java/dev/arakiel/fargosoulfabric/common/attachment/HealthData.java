package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FargoSoulDamageSources;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import dev.arakiel.fargosoulfabric.register.FargoSoulDamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class HealthData implements Attachments.Bound {
   private LivingEntity owner;
   private float amount = 0.0F;

   public HealthData() {
      this.owner = null;
   }

   public HealthData(AttachmentTarget holder) {
      if (holder instanceof LivingEntity living) {
         this.owner = living;
      } else {
         throw new IllegalArgumentException(holder + " is not a valid HealthData");
      }
   }

   @Override
   public void bind(AttachmentTarget target) {
      if (this.owner == null && target instanceof LivingEntity holder) {
         this.owner = holder;
      }
   }

   public void tick() {
      AttributeInstance instance = this.owner.getAttribute(FargoSoulAttributeRegister.HealthRegen);
      if (instance != null && instance.getValue() != 0.0) {
         this.amount = this.amount + (float)(instance.getValue() / 20.0);
         if (this.owner.tickCount % 10 == 0) {
            if (this.amount > 1.0F) {
               float heal = this.amount - 1.0F;
               this.owner.heal(heal);
               this.amount -= heal;
            } else if (this.amount < -1.0F) {
               float damage = -1.0F - this.amount;
               ImmunityData.get(this.owner).attack(FargoSoulDamageSources.getDamageSource(DamageTypes.GENERIC, this.owner.level()), damage);
               this.amount += damage;
            }
         }
      }
   }
}

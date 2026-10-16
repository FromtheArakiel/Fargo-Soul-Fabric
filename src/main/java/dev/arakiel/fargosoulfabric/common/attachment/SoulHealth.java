package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FSFDamageSources;
import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import dev.arakiel.fargosoulfabric.register.FSFDamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class SoulHealth implements Attachments.Bound {
   private LivingEntity owner;
   private float amount = 0.0F;

   public SoulHealth() {
      this.owner = null;
   }

   public SoulHealth(AttachmentTarget holder) {
      if (holder instanceof LivingEntity living) {
         this.owner = living;
      } else {
         throw new IllegalArgumentException(holder + " is not a valid SoulHealth");
      }
   }

   @Override
   public void bind(AttachmentTarget target) {
      if (this.owner == null && target instanceof LivingEntity holder) {
         this.owner = holder;
      }
   }

   public void tick() {
      AttributeInstance instance = this.owner.getAttribute(FSFAttributeRegister.HealthRegen);
      if (this.owner instanceof net.minecraft.world.entity.player.Player soulOwner) {
         PlayerSouls souls = Attachments.get(soulOwner,
                 dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister.SOUL_ITEM_DATA);
         if (souls.getCache().isEmpty()) {
            souls.update(soulOwner);
         }
      }
      if (instance != null && instance.getValue() != 0.0) {
         this.amount = this.amount + (float)(instance.getValue() / 20.0);
         if (this.owner.tickCount % 10 == 0) {
            if (this.amount > 1.0F) {
               float heal = this.amount - 1.0F;
               this.owner.heal(heal);
               this.amount -= heal;
            } else if (this.amount < -1.0F) {
               float damage = -1.0F - this.amount;
               this.owner.hurt(this.owner.damageSources().generic(), damage);
               this.amount += damage;
            }
         }
      }
   }
}

package dev.arakiel.fargosoulfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityDamageSource;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

/** Lets the summons, and the armor piercing attribute, cut through the armor of a target. */
@Mixin({CombatRules.class})
public class CombatRulesMixin {
   public CombatRulesMixin() {
   }

   @WrapMethod(
      method = {"getDamageAfterAbsorb"}
   )
   private static float fargoSoul$modifyArmorValue(LivingEntity victim, float damage, DamageSource source,
                                                   float totalArmor, float armorToughness,
                                                   Operation<Float> original) {
      if (source instanceof AttachmentEntityDamageSource damageSource) {
         totalArmor -= damageSource.getArmorPierce();
         Player owner = damageSource.getOwner();
         if (owner != null) {
            AttributeInstance instance = owner.getAttribute(FargoSoulAttributeRegister.SummonArmorPierce);
            if (instance != null) {
               totalArmor -= (float)instance.getValue();
            }
         }
      }
      if (source.getEntity() instanceof LivingEntity attacker) {
         AttributeInstance instance = attacker.getAttribute(FargoSoulAttributeRegister.ArmorPierce);
         if (instance != null) {
            totalArmor -= (float)instance.getValue();
         }
      }

      return original.call(victim, damage, source, Math.max(totalArmor, 0.0F), armorToughness);
   }
}
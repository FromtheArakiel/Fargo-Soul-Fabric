package dev.arakiel.fargosoulfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.arakiel.fargosoulfabric.common.summon.SummonDamageSource;
import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
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
   private static float fsf$modifyArmorValue(LivingEntity victim, float damage, DamageSource source,
                                                   float totalArmor, float armorToughness,
                                                   Operation<Float> original) {
      if (source instanceof SummonDamageSource damageSource) {
         totalArmor -= damageSource.getArmorPierce();
         Player owner = damageSource.getOwner();
         if (owner != null) {
            AttributeInstance instance = owner.getAttribute(FSFAttributeRegister.SummonArmorPierce);
            if (instance != null) {
               totalArmor -= (float)instance.getValue();
            }
         }
      }
      if (source.getEntity() instanceof LivingEntity attacker) {
         AttributeInstance instance = attacker.getAttribute(FSFAttributeRegister.ArmorPierce);
         if (instance != null) {
            totalArmor -= (float)instance.getValue();
         }
      }

      return original.call(victim, damage, source, Math.max(totalArmor, 0.0F), armorToughness);
   }
}
package dev.arakiel.fargosoulfabric.utils;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class AttributeUtils {
   public AttributeUtils() {
   }

   public static void condition(
      LivingEntity livingEntity, Holder<Attribute> attribute, ResourceLocation resourceLocation, double amount, Operation operation, boolean condition
   ) {
      if (condition) {
         AttributeInstance attributeModifier = livingEntity.getAttribute(attribute);
         if (attributeModifier instanceof AttributeInstance) {
            AttributeModifier attributeModifierx = attributeModifier.getModifier(resourceLocation);
            if (attributeModifierx == null
               || attributeModifierx instanceof AttributeModifier && (attributeModifierx.amount() != amount || attributeModifierx.operation() != operation)) {
               addAttributeModifier(livingEntity, attribute, resourceLocation, amount, operation);
            }
         }
      } else {
         removeAttributeModifier(livingEntity, attribute, resourceLocation);
      }
   }

   public static void addAttributeModifier(
      LivingEntity livingEntity, Holder<Attribute> attribute, ResourceLocation resourceLocation, double amount, Operation operation
   ) {
      AttributeModifier modifier = new AttributeModifier(resourceLocation, amount, operation);
      AttributeInstance var8 = livingEntity.getAttribute(attribute);
      if (var8 instanceof AttributeInstance) {
         if (var8.getModifier(resourceLocation) != null) {
            var8.removeModifier(resourceLocation);
         }

         var8.addPermanentModifier(modifier);
      }
   }

   public static void removeAttributeModifier(LivingEntity livingEntity, Holder<Attribute> attribute, ResourceLocation resourceLocation) {
      AttributeInstance var4 = livingEntity.getAttribute(attribute);
      if (var4 instanceof AttributeInstance && var4.getModifier(resourceLocation) != null) {
         var4.removeModifier(resourceLocation);
      }
   }
}

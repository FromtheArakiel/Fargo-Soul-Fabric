package dev.arakiel.fargosoulfabric.common.builder;

import dev.arakiel.fargosoulfabric.FSF;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.component.ItemAttributeModifiers.Builder;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class AttributeArmorItemBuilder {
   private final Holder<ArmorMaterial> material;
   private final Type type;
   private final Properties properties;
   private final Builder modifiers = ItemAttributeModifiers.builder();

   public AttributeArmorItemBuilder(Holder<ArmorMaterial> material, Type type) {
      this.material = material;
      this.type = type;
      this.properties = new Properties()
         .durability(-1)
         .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
         .component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
   }

   public static AttributeArmorItemBuilder builder(Holder<ArmorMaterial> material, Type type) {
      return new AttributeArmorItemBuilder(material, type);
   }

   public AttributeArmorItemBuilder modifier(Holder<Attribute> attribute, AttributeModifier modifier) {
      this.modifiers.add(attribute, modifier, EquipmentSlotGroup.bySlot(this.type.getSlot()));
      return this;
   }

   public AttributeArmorItemBuilder modifier(Holder<Attribute> attribute, double value, Operation operation) {
      return this.modifier(attribute, new AttributeModifier(FSF.rl("armor_" + this.type.getName()), value, operation));
   }

   public AttributeArmorItemBuilder properties(Consumer<Properties> customizer) {
      customizer.accept(this.properties);
      return this;
   }

   public ArmorItem build() {
      return new ArmorItem(this.material, this.type, this.properties.attributes(this.modifiers.build()));
   }
}

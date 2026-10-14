package dev.arakiel.fargosoulfabric.register;

import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.NotNull;

public class SimpleMobEffectBuilder {
   private final MobEffectCategory category;
   private final int color;
   private final List<SimpleMobEffectBuilder.AttributeModifierEntry> attributeModifiers = new ArrayList<>();
   private final List<SimpleMobEffectBuilder.AttributeCurveEntry> attributeCurveModifiers = new ArrayList<>();
   private BiConsumer<LivingEntity, Integer> applyEffectTick = null;
   private BiFunction<Integer, Integer, Boolean> shouldApplyEffectTickThisTick = null;

   public SimpleMobEffectBuilder(MobEffectCategory category, int color) {
      this.category = category;
      this.color = color;
   }

   public SimpleMobEffectBuilder applyEffectTick(BiConsumer<LivingEntity, Integer> callback) {
      this.applyEffectTick = callback;
      return this;
   }

   public SimpleMobEffectBuilder shouldApplyEffectTickThisTick(BiFunction<Integer, Integer, Boolean> callback) {
      this.shouldApplyEffectTickThisTick = callback;
      return this;
   }

   public SimpleMobEffectBuilder addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, double amount, Operation operation) {
      this.attributeModifiers.add(new SimpleMobEffectBuilder.AttributeModifierEntry(attribute, id, amount, operation));
      return this;
   }

   public SimpleMobEffectBuilder addAttributeModifier(Holder<Attribute> attribute, ResourceLocation id, Int2DoubleFunction curve, Operation operation) {
      this.attributeCurveModifiers.add(new SimpleMobEffectBuilder.AttributeCurveEntry(attribute, id, operation, curve));
      return this;
   }

   public MobEffect build() {
      MobEffect effect = new MobEffect(this.category, this.color) {
         public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
            if (SimpleMobEffectBuilder.this.applyEffectTick != null) {
               SimpleMobEffectBuilder.this.applyEffectTick.accept(entity, amplifier);
               return true;
            } else {
               return super.applyEffectTick(entity, amplifier);
            }
         }

         public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
            return SimpleMobEffectBuilder.this.shouldApplyEffectTickThisTick != null
               ? SimpleMobEffectBuilder.this.shouldApplyEffectTickThisTick.apply(duration, amplifier)
               : super.shouldApplyEffectTickThisTick(duration, amplifier);
         }

         @Override
         public void createModifiers(int amplifier,
                                     java.util.function.BiConsumer<Holder<Attribute>,
                                             net.minecraft.world.entity.ai.attributes.AttributeModifier> consumer) {
            super.createModifiers(amplifier, consumer);
            for (SimpleMobEffectBuilder.AttributeCurveEntry entry : SimpleMobEffectBuilder.this.attributeCurveModifiers) {
               consumer.accept(entry.attribute(),
                       new net.minecraft.world.entity.ai.attributes.AttributeModifier(entry.id(),
                               entry.curve().apply(amplifier), entry.operation()));
            }
         }
      };

      for (SimpleMobEffectBuilder.AttributeModifierEntry entry : this.attributeModifiers) {
         effect.addAttributeModifier(entry.attribute, entry.id, entry.amount, entry.operation);
      }

      return effect;
   }

   private static record AttributeCurveEntry(Holder<Attribute> attribute, ResourceLocation id, Operation operation, Int2DoubleFunction curve) {
      private AttributeCurveEntry(Holder<Attribute> attribute, ResourceLocation id, Operation operation, Int2DoubleFunction curve) {
         this.attribute = attribute;
         this.id = id;
         this.operation = operation;
         this.curve = curve;
      }
   }

   private static record AttributeModifierEntry(Holder<Attribute> attribute, ResourceLocation id, double amount, Operation operation) {
      private AttributeModifierEntry(Holder<Attribute> attribute, ResourceLocation id, double amount, Operation operation) {
         this.attribute = attribute;
         this.id = id;
         this.amount = amount;
         this.operation = operation;
      }
   }
}

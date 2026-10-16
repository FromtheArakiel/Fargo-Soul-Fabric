package dev.arakiel.fargosoulfabric.register;

import it.unimi.dsi.fastutil.ints.Int2DoubleFunction;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import org.jetbrains.annotations.NotNull;

import dev.arakiel.fargosoulfabric.FSF;
import java.util.function.Function;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.List;

public class FSFMobEffectRegister {

    public static final Holder<MobEffect> DeathMark =
            register("death_mark", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0xB51F1F)
                    .addAttributeModifier(Attributes.MAX_HEALTH, location, -0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.ARMOR, location, -0.4f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, location, 0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .build()
            );

    /** 水晶碎甲：降低目标 20 点护甲 */
    public static final Holder<MobEffect> CrystalArmorBreak =
            register("crystal_armor_break", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x7FE0FF)
                    .addAttributeModifier(Attributes.ARMOR, location, -20.0f, AttributeModifier.Operation.ADD_VALUE)
                    .build()
            );

    /** 先发至人：下次攻击造成的伤害提升 60% */
    public static final Holder<MobEffect> PreemptiveStrike =
            register("preemptive_strike", location -> new SimpleMobEffectBuilder(MobEffectCategory.BENEFICIAL, 0xFFD700)
                    .build()
            );

    /** 暗影焰：周期性造成魔法伤害 */
    public static final Holder<MobEffect> ShadowFire =
            register("shadow_fire", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x4B0082)
                    .addAttributeModifier(FSFAttributeRegister.HealthRegen, location, amplifier -> -(amplifier + 1) * 1.5, AttributeModifier.Operation.ADD_VALUE)
                    .build()
            );

    /** 暗影之赐：最大生命 +40%，移速 +15%，攻击 +40% */
    public static final Holder<MobEffect> ShadowGift =
            register("shadow_gift", location -> new SimpleMobEffectBuilder(MobEffectCategory.BENEFICIAL, 0x4B0082)
                    .addAttributeModifier(Attributes.MAX_HEALTH, location, 0.4f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, location, 0.15f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, location, 0.4f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .build()
            );

    /** 铅中毒：每秒受到毒伤，并传染给周围目标 */
    public static final Holder<MobEffect> LeadPoisoning =
            register("lead_poisoning", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x5A5A5A)
                    .addAttributeModifier(FSFAttributeRegister.HealthRegen, location, amplifier -> -(amplifier + 1) * 1.0, AttributeModifier.Operation.ADD_VALUE)
                    .shouldApplyEffectTickThisTick((duration, amplifier) -> true)
                    .applyEffectTick((entity, amplifier) -> {
                        MobEffectInstance effect = entity.getEffect(FSFMobEffectRegister.LeadPoisoning);
                        if (effect != null && effect.getDuration() > 20 && !entity.level().isClientSide()) {
                            List<LivingEntity> list = entity.level().getEntitiesOfClass(LivingEntity.class, entity.getBoundingBox().inflate(4), living -> living != entity && !living.hasEffect(effect.getEffect()));
                            for (LivingEntity living : list) {
                                living.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() / 2, amplifier));
                            }
                        }
                    })
                    .build()
            );

    /** 惊人一刻：攻击力 +150% */
    public static final Holder<MobEffect> AmazingMoment =
            register("amazing_moment", location -> new SimpleMobEffectBuilder(MobEffectCategory.BENEFICIAL, 0xFFD700)
                    .build()
            );

    /** 泰拉共鸣：释放雷电球概率提升，雷电球伤害提升 */
    public static final Holder<MobEffect> TerraResonance =
            register("terra_resonance", location -> new SimpleMobEffectBuilder(MobEffectCategory.BENEFICIAL, 0x38ffec)
                    .build()
            );

    /** 流血：恢复的生命值减少 50% */
    public static final Holder<MobEffect> Bleeding =
            register("bleeding", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0xC22A2A)
                    .build()
            );

    /** 血如泉涌：恢复的生命值减少 70% */
    public static final Holder<MobEffect> Hemorrhage =
            register("hemorrhage", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0xD22A2A)
                    .build()
            );

    /** 涂油：受到的火焰伤害提升 200% */
    public static final Holder<MobEffect> Oiled =
            register("oil", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x6B4A2B)
                    .build()
            );

    /** 寒冷：移动速度减少 30% */
    public static final Holder<MobEffect> Chill =
            register("chilly", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x9BC9FF)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, location, -0.3f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .build()
            );

    /** 星之光辉：暴击伤害提升 40% */
    public static final Holder<MobEffect> Starlight =
            register("starlight", location -> new SimpleMobEffectBuilder(MobEffectCategory.BENEFICIAL, 0xFFE87A)
                    .build()
            );

    /** 盔甲破损：护甲值减少 40% */
    public static final Holder<MobEffect> ArmorBreak =
            register("armor_break", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x6E6E6E)
                    .addAttributeModifier(Attributes.ARMOR, location, -0.4f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .build()
            );

    /** 灵雾迷障：移动速度减少 20%，护甲值减少 10% */
    public static final Holder<MobEffect> TekeMist =
            register("teke_mist", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x9FA8DA)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, location, -0.2f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .addAttributeModifier(Attributes.ARMOR, location, -0.1f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)
                    .build()
            );

    /** 山铜中毒：每秒受到毒伤 */
    public static final Holder<MobEffect> OrichalcumPoisoning =
            register("orichalcum_poisoning", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0x9FE2BF)
                    .addAttributeModifier(FSFAttributeRegister.HealthRegen, location, amplifier -> -(amplifier + 1) * 1.0, AttributeModifier.Operation.ADD_VALUE)
                    .build()
            );

    /** 迈达斯：金魔石标记，被击杀时掉落金锭 */
    public static final Holder<MobEffect> Midas =
            register("midas", location -> new SimpleMobEffectBuilder(MobEffectCategory.HARMFUL, 0xFFD700)
                    .build()
            );

    /** 仙馔密酒：增益标记 */
    public static final Holder<MobEffect> Ambrosia =
            register("ambrosia", location -> new SimpleMobEffectBuilder(MobEffectCategory.BENEFICIAL, 0xFFD75F)
                    .build()
            );

    public static void init() {
        // Registered by the field initializers above.
    }

    private static Holder<MobEffect> register(String name, Function<ResourceLocation, MobEffect> factory) {
        ResourceLocation id = FSF.rl(name);
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, id, factory.apply(id));
    }

public static class SimpleMobEffectBuilder {
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

         @Override
         public void addAttributeModifiers(net.minecraft.world.entity.ai.attributes.AttributeMap map, int amplifier) {
            super.addAttributeModifiers(map, amplifier);
            for (SimpleMobEffectBuilder.AttributeCurveEntry entry : SimpleMobEffectBuilder.this.attributeCurveModifiers) {
               net.minecraft.world.entity.ai.attributes.AttributeInstance instance = map.getInstance(entry.attribute());
               if (instance != null) {
                  instance.removeModifier(entry.id());
                  instance.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                          entry.id(), entry.curve().apply(amplifier), entry.operation()));
               }
            }
         }

         @Override
         public void removeAttributeModifiers(net.minecraft.world.entity.ai.attributes.AttributeMap map) {
            super.removeAttributeModifiers(map);
            for (SimpleMobEffectBuilder.AttributeCurveEntry entry : SimpleMobEffectBuilder.this.attributeCurveModifiers) {
               net.minecraft.world.entity.ai.attributes.AttributeInstance instance = map.getInstance(entry.attribute());
               if (instance != null) {
                  instance.removeModifier(entry.id());
               }
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
}

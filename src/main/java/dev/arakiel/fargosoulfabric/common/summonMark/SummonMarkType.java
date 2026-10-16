package dev.arakiel.fargosoulfabric.common.summonMark;

import com.mojang.datafixers.util.Function5;
import dev.arakiel.fargosoulfabric.common.attachment.SummonMarks;
import dev.arakiel.fargosoulfabric.common.summon.SummonDamageSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record SummonMarkType(
   ResourceLocation location,
   float additionalDamage,
   float additionalArmorPierce,
   float criticalHitRate,
   @Nullable Consumer4<SummonMarks, SummonMarkInstance, LivingEntity, Player> tickConsumer,
   @Nullable Function5<SummonMarks, SummonMarkInstance, LivingEntity, SummonDamageSource, Float, Float> damagePre,
   @Nullable Consumer5<SummonMarks, SummonMarkInstance, LivingEntity, SummonDamageSource, Float> damagePost,
   @Nullable Consumer4<SummonMarks, SummonMarkInstance, LivingEntity, SummonDamageSource> killConsumer
) {
   public SummonMarkType(
      ResourceLocation location,
      float additionalDamage,
      float additionalArmorPierce,
      float criticalHitRate,
      @Nullable Consumer4<SummonMarks, SummonMarkInstance, LivingEntity, Player> tickConsumer,
      @Nullable Function5<SummonMarks, SummonMarkInstance, LivingEntity, SummonDamageSource, Float, Float> damagePre,
      @Nullable Consumer5<SummonMarks, SummonMarkInstance, LivingEntity, SummonDamageSource, Float> damagePost,
      @Nullable Consumer4<SummonMarks, SummonMarkInstance, LivingEntity, SummonDamageSource> killConsumer
   ) {
      this.location = location;
      this.additionalDamage = additionalDamage;
      this.additionalArmorPierce = additionalArmorPierce;
      this.criticalHitRate = criticalHitRate;
      this.tickConsumer = tickConsumer;
      this.damagePre = damagePre;
      this.damagePost = damagePost;
      this.killConsumer = killConsumer;
   }

   public SummonMarkInstance createInstance(Player player, LivingEntity target) {
      return new SummonMarkInstance(this, 200);
   }

   public List<Component> getTooltips() {
      List<Component> tooltips = new ArrayList<>();
      if (this.additionalDamage > 0.0F) {
         tooltips.add(
            Component.literal(String.format(Locale.ROOT, "%.1f ", this.additionalDamage))
               .withStyle(ChatFormatting.BLUE)
               .append(Component.translatable("mark.additional_damage").withStyle(ChatFormatting.GRAY))
         );
      }

      if (this.additionalArmorPierce > 0.0F) {
         tooltips.add(
            Component.literal(String.format(Locale.ROOT, "%.1f ", this.additionalArmorPierce))
               .withStyle(ChatFormatting.BLUE)
               .append(Component.translatable("mark.additional_armor_pierce").withStyle(ChatFormatting.GRAY))
         );
      }

      if (this.criticalHitRate > 0.0F) {
         tooltips.add(
            Component.literal(String.format(Locale.ROOT, "%.0f%% ", this.criticalHitRate * 100.0F))
               .withStyle(ChatFormatting.BLUE)
               .append(Component.translatable("mark.critical_hit_rate").withStyle(ChatFormatting.GRAY))
         );
      }

      return tooltips;
   }

   public void tick(SummonMarks tracker, SummonMarkInstance instance, LivingEntity target, Player owner) {
      if (this.tickConsumer != null) {
         this.tickConsumer.accept(tracker, instance, target, owner);
      }
   }

   public float damagePre(SummonMarks tracker, SummonMarkInstance instance, LivingEntity target, SummonDamageSource source, float damage) {
      return this.damagePre != null ? this.damagePre.apply(tracker, instance, target, source, damage) : damage;
   }

   public void damagePost(SummonMarks tracker, SummonMarkInstance instance, LivingEntity target, SummonDamageSource source, float damage) {
      if (this.damagePost != null) {
         this.damagePost.accept(tracker, instance, target, source, damage);
      }
   }

   public void kill(SummonMarks tracker, SummonMarkInstance instance, LivingEntity target, SummonDamageSource source) {
      if (this.killConsumer != null) {
         this.killConsumer.accept(tracker, instance, target, source);
      }
   }

@FunctionalInterface
public interface Consumer4<T1, T2, T3, T4> {
   void accept(T1 var1, T2 var2, T3 var3, T4 var4);
}

@FunctionalInterface
public interface Consumer5<T1, T2, T3, T4, T5> {
   void accept(T1 var1, T2 var2, T3 var3, T4 var4, T5 var5);
}
}

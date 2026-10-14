package dev.arakiel.fargosoulfabric.common.item;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulRegisters;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import dev.arakiel.fargosoulfabric.common.dataComponent.MinionWeapon;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import dev.arakiel.fargosoulfabric.common.sound.Playable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonerWeaponItem<T extends Minion> extends Item {
   private final Supplier<AttachmentEntityType<T>> typeSupplier;
   private final TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonConsumer;
   private final TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeConsumer;

   public SummonerWeaponItem(
      Properties properties,
      Supplier<AttachmentEntityType<T>> typeSupplier,
      @Nullable TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonAction,
      TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeConsumer
   ) {
      super(properties);
      this.typeSupplier = typeSupplier;
      this.summonConsumer = summonAction != null ? summonAction : (weapon, player, itemStack) -> {
         T minion = weapon.createMinion(player, itemStack);
         FargoSoulHelper fargoSoulHelper = FargoSoulHelper.get(player);
         MinionSlotType slotType = weapon.getSlotType(itemStack);
         if (fargoSoulHelper.canSummon(slotType, minion.getSlotCost())) {
            AABB box = player.getBoundingBox();
            Vec3 pos = box.getCenter();
            minion.init(new PathNode(pos.offsetRandom(player.getRandom(), 2.0F), 0.0F, 0.0F, 0.0F));
            fargoSoulHelper.add(minion);
         }
      };
      this.removeConsumer = removeConsumer != null
         ? removeConsumer
         : (weapon, player, itemStack) -> {
            AttachmentEntityData entityData = FargoSoulHelper.get(player).getEntityData();
            entityData.getGroups()
               .getOrDefault(this.getEntityType(), List.of())
               .stream()
               .filter(entity -> entity instanceof Minion minion)
               .map(entity -> (Minion)entity)
               .filter(minion -> minion.getOwner() == player && !minion.isRemove())
               .forEach(AttachmentEntity::setRemove);
         };
   }

   @NotNull
   public InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
      ItemCooldowns cooldowns = player.getCooldowns();
      ItemStack itemStack = player.getItemInHand(hand);
      if (!cooldowns.isOnCooldown(itemStack.getItem()) && !level.isClientSide()) {
         cooldowns.addCooldown(itemStack.getItem(), 4);
         player.swing(hand, true);
         if (!player.isShiftKeyDown()) {
            this.summon(player, itemStack);
         } else {
            this.remove(player, itemStack);
         }

         Playable.play(this.getSoundEvent(itemStack), level, player.position(), player.getSoundSource());
      }

      return super.use(level, player, hand);
   }

   @NotNull
   public AttachmentEntityType<T> getEntityType() {
      return this.typeSupplier.get();
   }

   @NotNull
   public MinionSlotType getSlotType(ItemStack itemStack) {
      return (itemStack.getOrDefault(FargoSoulRegisters.MINION_WEAPON, MinionWeapon.Empty)).type();
   }

   public float getSummonDamage(@Nullable Player player, @NotNull ItemStack itemStack) {
      float damage = (itemStack.getOrDefault(FargoSoulRegisters.MINION_WEAPON, MinionWeapon.Empty)).damage();
      if (player != null) {
         AttributeInstance attribute = player.getAttribute(FargoSoulAttributeRegister.SummonDamage);
         if (attribute != null) {
            damage = (float)((double)damage * attribute.getValue());
         }
      }

      return damage;
   }

   public float getSummonKnockback(@Nullable Player player, @NotNull ItemStack itemStack) {
      float knockback = (itemStack.getOrDefault(FargoSoulRegisters.MINION_WEAPON, MinionWeapon.Empty)).knockback();
      if (player != null) {
         AttributeInstance attribute = player.getAttribute(FargoSoulAttributeRegister.SummonKnockback);
         if (attribute != null) {
            knockback = (float)((double)knockback * attribute.getValue());
         }
      }

      return knockback;
   }

   public float getSummonArmorPierce(@Nullable Player player, @NotNull ItemStack itemStack) {
      return (itemStack.getOrDefault(FargoSoulRegisters.MINION_WEAPON, MinionWeapon.Empty)).armorPierce();
   }

   @Nullable
   public SoundEvent getSoundEvent(ItemStack itemStack) {
      Holder<SoundEvent> event = (itemStack.getOrDefault(FargoSoulRegisters.MINION_WEAPON, MinionWeapon.Empty)).soundEvent();
      return event != null ? event.value() : null;
   }

   @NotNull
   public T createMinion(@NotNull Player player, @NotNull ItemStack itemStack) {
      AttachmentEntityType<T> type = this.getEntityType();
      T minion = type.factory().get();
      minion.setOwner(player);
      minion.setSlotType(this.getSlotType(itemStack));
      minion.setDamage(this.getSummonDamage(player, itemStack));
      minion.setKnockback(this.getSummonKnockback(player, itemStack));
      minion.setArmorPierce(this.getSummonArmorPierce(player, itemStack));
      return minion;
   }

   public void summon(@NotNull Player player, @NotNull ItemStack itemStack) {
      this.summonConsumer.accept(this, player, itemStack);
   }

   public void remove(@NotNull Player player, @NotNull ItemStack itemStack) {
      this.removeConsumer.accept(this, player, itemStack);
   }

   @NotNull
   public List<Component> getTooltips(ItemStack itemStack, Player player) {
      List<Component> toolTips = new ArrayList<>();
      AttachmentEntityType<?> type = this.getEntityType();
      ResourceLocation location = FargoSoulRegisters.ATTACHMENT_ENTITY_TYPES.getKey(type);
      if (location == null) {
         return toolTips;
      } else {
         float damage = this.getSummonDamage(player, itemStack);
         if (damage > 0.0F) {
            toolTips.add(
               Component.literal(String.format(Locale.ROOT, "%.1f ", damage))
                  .withStyle(ChatFormatting.BLUE)
                  .append(Component.translatable("item.fargosoul.tooltip.damage").withStyle(ChatFormatting.GRAY))
            );
         }

         float knockback = this.getSummonKnockback(player, itemStack);
         if (knockback > 0.0F) {
            toolTips.add(
               Component.literal(String.format(Locale.ROOT, "%.1f ", knockback))
                  .withStyle(ChatFormatting.BLUE)
                  .append(Component.translatable("item.fargosoul.tooltip.knockback").withStyle(ChatFormatting.GRAY))
            );
         }

         float armor_pierce = this.getSummonArmorPierce(player, itemStack);
         if (armor_pierce > 0.0F) {
            toolTips.add(
               Component.literal(String.format(Locale.ROOT, "%.1f ", armor_pierce))
                  .withStyle(ChatFormatting.BLUE)
                  .append(Component.translatable("item.fargosoul.tooltip.armor_pierce").withStyle(ChatFormatting.GRAY))
            );
         }

         toolTips.add(
            Component.translatable(
                  "item.fargosoul.tooltip.summon", new Object[]{Component.translatable("summon." + location.getNamespace() + "." + location.getPath())}
               )
               .withStyle(ChatFormatting.GRAY)
         );
         FargoSoulHelper fargoSoulHelper = FargoSoulHelper.get(player);
         MinionSlotType slotType = this.getSlotType(itemStack);
         switch (slotType) {
            case Minion:
               toolTips.add(
                  Component.translatable(
                        "item.fargosoul.tooltip.minion_slots",
                        new Object[]{
                           Component.literal(String.valueOf(fargoSoulHelper.getUsedSlots(MinionSlotType.Minion))).withStyle(ChatFormatting.BLUE),
                           Component.literal(String.valueOf(fargoSoulHelper.getMaxCount(MinionSlotType.Minion))).withStyle(ChatFormatting.BLUE)
                        }
                     )
                     .withStyle(ChatFormatting.GRAY)
               );
               break;
            case Sentry:
               toolTips.add(
                  Component.translatable(
                        "item.fargosoul.tooltip.sentry_slots",
                        new Object[]{
                           Component.literal(String.valueOf(fargoSoulHelper.getUsedSlots(MinionSlotType.Sentry))).withStyle(ChatFormatting.BLUE),
                           Component.literal(String.valueOf(fargoSoulHelper.getMaxCount(MinionSlotType.Sentry))).withStyle(ChatFormatting.BLUE)
                        }
                     )
                     .withStyle(ChatFormatting.GRAY)
               );
         }

         toolTips.add(Component.translatable("item.fargosoul.tooltip.remove_all").withStyle(ChatFormatting.GRAY));
         return toolTips;
      }
   }
}
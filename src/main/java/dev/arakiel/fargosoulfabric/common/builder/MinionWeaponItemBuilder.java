package dev.arakiel.fargosoulfabric.common.builder;

import dev.arakiel.fargosoulfabric.register.FargoSoulRegisters;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.common.dataComponent.MinionWeapon;
import dev.arakiel.fargosoulfabric.common.item.SummonerWeaponItem;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

public class MinionWeaponItemBuilder<T extends Minion> {
   private final Supplier<AttachmentEntityType<T>> typeSupplier;
   private float damage = 0.0F;
   private float knockback = 0.0F;
   private float armorPierce = 0.0F;
   private MinionSlotType slotType = MinionSlotType.Minion;
   private Holder<SoundEvent> soundEventHolder = null;
   private TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonConsumer = null;
   private TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeConsumer = null;
   private Consumer<Properties> properties = null;

   public MinionWeaponItemBuilder(ResourceLocation identifier, @NotNull Supplier<AttachmentEntityType<T>> typeSupplier) {
      this.typeSupplier = typeSupplier;
   }

   public MinionWeaponItemBuilder<T> damage(float damage) {
      this.damage = damage;
      return this;
   }

   public MinionWeaponItemBuilder<T> knockback(float knockback) {
      this.knockback = knockback;
      return this;
   }

   public MinionWeaponItemBuilder<T> armorPierce(float armorPierce) {
      this.armorPierce = armorPierce;
      return this;
   }

   public MinionWeaponItemBuilder<T> slotType(MinionSlotType slotType) {
      this.slotType = slotType;
      return this;
   }

   public MinionWeaponItemBuilder<T> sound(Holder<SoundEvent> soundEventHolder) {
      this.soundEventHolder = soundEventHolder;
      return this;
   }

   public MinionWeaponItemBuilder<T> summon(TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> summonConsumer) {
      this.summonConsumer = summonConsumer;
      return this;
   }

   public MinionWeaponItemBuilder<T> remove(TriConsumer<SummonerWeaponItem<T>, Player, ItemStack> removeConsumer) {
      this.removeConsumer = removeConsumer;
      return this;
   }

   public MinionWeaponItemBuilder<T> properties(Consumer<Properties> properties) {
      this.properties = properties;
      return this;
   }

   public SummonerWeaponItem<T> build() {
      Properties proper = new Properties().stacksTo(1);
      if (this.properties != null) {
         this.properties.accept(proper);
      }

      proper.component(
         FargoSoulRegisters.MINION_WEAPON, new MinionWeapon(this.damage, this.knockback, this.armorPierce, this.slotType, this.soundEventHolder)
      );
      return new SummonerWeaponItem<>(proper, this.typeSupplier, this.summonConsumer, this.removeConsumer);
   }
}
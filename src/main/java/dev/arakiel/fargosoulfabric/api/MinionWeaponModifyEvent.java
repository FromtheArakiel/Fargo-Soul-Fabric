package dev.arakiel.fargosoulfabric.api;

import dev.arakiel.fargosoulfabric.common.dataComponent.MinionWeapon;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MinionWeaponModifyEvent {
   public final ItemLike item;
   public float damage;
   public float knockback;
   public float armorPierce;
   @NotNull
   public MinionSlotType type;
   @Nullable
   public Holder<SoundEvent> soundEvent;

   public MinionWeaponModifyEvent(ItemLike item, MinionWeapon weapon) {
      this.item = item;
      this.damage = weapon.damage();
      this.knockback = weapon.knockback();
      this.armorPierce = weapon.armorPierce();
      this.type = weapon.type();
      this.soundEvent = weapon.soundEvent();
   }

   public MinionWeapon toMinionWeapon() {
      return new MinionWeapon(this.damage, this.knockback, this.armorPierce, this.type, this.soundEvent);
   }
}

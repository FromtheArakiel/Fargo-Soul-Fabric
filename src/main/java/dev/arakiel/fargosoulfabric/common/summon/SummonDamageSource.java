package dev.arakiel.fargosoulfabric.common.summon;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonDamageSource extends DamageSource {
   private final Summon summon;
   private float armorPierce;

   public SummonDamageSource(
      Holder<DamageType> type,
      @Nullable Entity directEntity,
      @Nullable Entity causingEntity,
      @Nullable Vec3 damageSourcePosition,
      @NotNull Summon summon
   ) {
      super(type, directEntity, causingEntity);
      this.summon = summon;
      this.armorPierce = summon.armorPierce;
   }

   public Summon getSummon() {
      return this.summon;
   }

   public Player getOwner() {
      return this.summon.getOwner();
   }

   public float getArmorPierce() {
      return this.armorPierce;
   }

   public void setArmorPierce(float armorPierce) {
      this.armorPierce = armorPierce;
   }
}

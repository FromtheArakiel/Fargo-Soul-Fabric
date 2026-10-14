package dev.arakiel.fargosoulfabric.mixin;

import dev.arakiel.fargosoulfabric.common.damageInfo.IDamageSourceCritical;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({DamageSource.class})
public class DamageSourceMixin implements IDamageSourceCritical {
   @Unique
   private boolean fargoSoul$isCritical = false;

   public DamageSourceMixin() {
   }

   @Override
   public boolean fargoSoul$isCritical() {
      return this.fargoSoul$isCritical;
   }

   @Override
   public void fargoSoul$setCritical(boolean isCritical) {
      this.fargoSoul$isCritical = isCritical;
   }
}

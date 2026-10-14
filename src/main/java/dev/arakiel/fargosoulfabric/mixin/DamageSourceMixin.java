package dev.arakiel.fargosoulfabric.mixin;

import dev.arakiel.fargosoulfabric.common.damageInfo.IDamageSourceCritical;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({DamageSource.class})
public class DamageSourceMixin implements IDamageSourceCritical {
   @Unique
   private boolean fsf$isCritical = false;

   public DamageSourceMixin() {
   }

   @Override
   public boolean fsf$isCritical() {
      return this.fsf$isCritical;
   }

   @Override
   public void fsf$setCritical(boolean isCritical) {
      this.fsf$isCritical = isCritical;
   }
}

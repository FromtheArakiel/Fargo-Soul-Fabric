package dev.arakiel.fargosoulfabric.common.damageNumbers;

import dev.arakiel.fargosoulfabric.utils.EasingCurve;
import java.util.Locale;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class DamageInfo {
   private final DamageInfoStyle style;
   private final float drag;
   private final boolean critical;
   private final float roll;
   private final String text;
   private int lastLife;
   private int life;
   private Vec3 lastPos;
   private Vec3 pos;
   private Vec3 velocity;

   public DamageInfo(DamageInfoStyle style, float damageAmount, Vec3 pos, Vec3 velocity, boolean critical) {
      this.style = style;
      this.pos = pos;
      this.lastPos = pos;
      this.velocity = velocity;
      this.drag = 0.75F;
      this.critical = critical;
      this.roll = (float)RandomSource.create((long)velocity.hashCode()).nextInt(-30, 30);
      this.text = formatDamage(damageAmount);
   }

   private static String formatDamage(float damageAmount) {
      if (damageAmount < 1.0F) {
         return String.format(Locale.ROOT, "%.2f", damageAmount);
      } else {
         return damageAmount < 10.0F
                 ? String.format(Locale.ROOT, "%.1f", damageAmount)
                 : String.valueOf((int)damageAmount);
      }
   }

   public boolean tick() {
      this.lastLife = this.life++;
      this.lastPos = this.pos;
      this.velocity = this.velocity.scale((double)this.drag);
      this.pos = this.pos.add(this.velocity);
      return this.isRemove();
   }

   public ResourceLocation getTexture() {
      return this.style.texture();
   }

   /** The style the number is drawn with. */
   public DamageInfoStyle getStyle() {
      return this.style;
   }

   /** The text of the number, already formatted. */
   public String getText() {
      return this.text;
   }

   public int getRenderColor(float partialTick) {
      float progress = Mth.lerp(partialTick, (float)this.lastLife, (float)this.life) / (float)this.style.maxLife();
      int baseColor = this.critical ? this.style.criticalColor() : this.style.color();
      float flicker = (Mth.sin(progress * (float)this.style.maxLife() * (float) Math.PI / 5.0F) + 1.0F) * 0.5F;
      float weight = flicker * (1.0F - progress);
      int r = baseColor >> 16 & 0xFF;
      int g = baseColor >> 8 & 0xFF;
      int b = baseColor & 0xFF;
      r = Mth.lerpInt(weight, r, Math.min(255, (int)((float)r * 1.5F)));
      g = Mth.lerpInt(weight, g, Math.min(255, (int)((float)g * 1.5F)));
      b = Mth.lerpInt(weight, b, Math.min(255, (int)((float)b * 1.5F)));
      float easedProgress = EasingCurve.EASE_IN_OUT_QUAD.apply(progress);
      int a;
      if (easedProgress < 0.2F) {
         a = Mth.lerpInt(easedProgress / 0.2F, 51, 255);
      } else if (easedProgress > 0.9F) {
         a = Mth.lerpInt(Math.min(1.0F, (easedProgress - 0.9F) / 0.05F), 255, 0);
      } else {
         a = 255;
      }

      return a << 24 | r << 16 | g << 8 | b;
   }

   public float getRenderRoll(float partialTick) {
      float progress = Mth.lerp(partialTick, (float)this.lastLife, (float)this.life) / (float)this.style.maxLife();
      return Mth.lerp(progress, this.roll, 0.0F);
   }

   public float getRenderScale(float partialTick) {
      float progress = Mth.lerp(partialTick, (float)this.lastLife, (float)this.life) / (float)this.style.maxLife();
      progress = EasingCurve.EASE_IN_OUT_QUAD.apply(progress);
      if ((double)progress < 0.1) {
         return Mth.lerp(Math.min(progress / 0.05F, 1.0F), 0.5F, 1.0F);
      } else {
         return progress > 0.9F ? Mth.lerp((progress - 0.9F) / 0.1F, 1.0F, 0.0F) : 1.0F;
      }
   }

   public Vec3 getRenderPos(float partialTick) {
      return this.lastPos.lerp(this.pos, (double)partialTick);
   }

   public boolean isRemove() {
      return this.life >= this.style.maxLife() - 1;
   }
}

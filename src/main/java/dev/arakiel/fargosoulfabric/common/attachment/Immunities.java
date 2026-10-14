package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import dev.arakiel.fargosoulfabric.common.Attachments;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.UUID;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class Immunities implements Attachments.Bound {
   private LivingEntity owner;
   private final Object2IntOpenHashMap<UUID> data = new Object2IntOpenHashMap<>();

   public Immunities() {
      this.owner = null;
   }

   public Immunities(AttachmentTarget holder) {
      if (holder instanceof LivingEntity living) {
         this.owner = living;
      } else {
         throw new IllegalArgumentException("Immunities only supports LivingEntity");
      }
   }

   public static Immunities get(LivingEntity target) {
      return Attachments.get(target, FSFAttachmentRegister.IMMUNITY_DATA);
   }

   @Override
   public void bind(AttachmentTarget target) {
      if (this.owner == null && target instanceof LivingEntity holder) {
         this.owner = holder;
      }
   }

   public void tick() {
      if (!this.data.isEmpty()) {
         this.data.replaceAll((uuid, integer) -> integer - 1);
         this.data.values().removeIf(v -> v <= 0);
      }
   }

   public void record(UUID uuid, int time) {
      this.data.put(uuid, time);
   }

   public boolean attack(DamageSource source, float amount) {
      int invulnerableTime = this.owner.invulnerableTime;
      this.owner.invulnerableTime = 0;
      boolean hurt = this.owner.hurt(source, amount);
      this.owner.invulnerableTime = invulnerableTime;
      return hurt;
   }

   public boolean isActive(UUID uuid) {
      return this.data.containsKey(uuid);
   }
}
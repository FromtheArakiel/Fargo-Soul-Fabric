package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import dev.arakiel.fargosoulfabric.common.summon.SummonDamageSource;
import dev.arakiel.fargosoulfabric.common.summonMark.SummonMarkInstance;
import dev.arakiel.fargosoulfabric.common.summonMark.SummonMarkType;
import dev.arakiel.fargosoulfabric.utils.FSFStreamCodecs;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonMarks implements Attachments.Bound {
   private Player owner;
   private final Map<SummonMarkType, SummonMarkInstance> marks = new HashMap<>();
   private LivingEntity target = null;
   private UUID targetUuid = null;

   public SummonMarks() {
      this.owner = null;
   }

   public SummonMarks(AttachmentTarget owner) {
      if (owner instanceof Player player) {
         this.owner = player;
      } else {
         throw new IllegalArgumentException(owner + " is not a valid SummonMarks");
      }
   }

   @Override
   public void bind(AttachmentTarget target) {
      if (this.owner == null && target instanceof Player holder) {
         this.owner = holder;
      }
   }

   public void tick() {
      LivingEntity current = this.getTarget();
      if (current != null && current.isAlive()) {
         this.marks.values().removeIf(instance -> {
            instance.getType().tick(this, instance, current, this.owner);
            instance.setDuration(instance.getDuration() - 1);
            return instance.getDuration() <= 0;
         });
      } else {
         this.marks.clear();
      }

      if (this.marks.isEmpty()) {
         this.target = null;
         this.targetUuid = null;
      }

      Attachments.sync(this.owner, FSFAttachmentRegister.SUMMON_MARK_DATA);
   }

   public void tracker(LivingEntity target, SummonMarkInstance instance) {
      if (this.owner != target) {
         if (!this.isTarget(target)) {
            this.marks.clear();
         }

         this.setTarget(target);
         this.marks.put(instance.getType(), instance);
      }
   }

   @Nullable
   public SummonMarkInstance getInstance(SummonMarkType type) {
      return this.marks.get(type);
   }

   public float getDamageModifier(SummonDamageSource source, float damage) {
      if (!this.marks.isEmpty()) {
         float additionalDamage = 0.0F;
         float additionalArmorPierce = 0.0F;
         float criticalHitRate = 0.0F;
         float criticalDamage = 2.0F;

         for (SummonMarkType summonMarkType : this.marks.keySet()) {
            if (summonMarkType.additionalDamage() > additionalDamage) {
               additionalDamage = summonMarkType.additionalDamage();
            }

            if (summonMarkType.additionalArmorPierce() > additionalArmorPierce) {
               additionalArmorPierce = summonMarkType.additionalArmorPierce();
            }

            if (summonMarkType.criticalHitRate() > criticalHitRate) {
               criticalHitRate = summonMarkType.criticalHitRate();
            }
         }

         damage += additionalDamage;
         source.setArmorPierce(source.getArmorPierce() + additionalArmorPierce);
         if (criticalHitRate > this.owner.getRandom().nextFloat()) {
            damage *= criticalDamage;
         }
      }

      return damage;
   }

   public Player getOwner() {
      return this.owner;
   }

   public Set<SummonMarkType> getTypes() {
      return this.marks.keySet();
   }

   public List<SummonMarkInstance> getSummonMarkInstances() {
      return this.marks.values().stream().toList();
   }

   public LivingEntity getTarget() {
      if (this.target == null && this.targetUuid != null && this.owner != null
              && this.owner.level() instanceof net.minecraft.server.level.ServerLevel serverLevel
              && serverLevel.getEntity(this.targetUuid) instanceof LivingEntity living) {
         this.target = living;
      }
      return this.target;
   }

   /** The marked target is identified by its UUID, so the client can match it without the entity. */
   public void setTarget(@Nullable LivingEntity target) {
      this.target = target;
      this.targetUuid = target != null ? target.getUUID() : null;
   }

   public boolean isTarget(LivingEntity living) {
      return living != null && this.targetUuid != null && this.targetUuid.equals(living.getUUID());
   }

   /** Sends the marked target to the clients. */
   public static final class SyncHandler implements StreamCodec<RegistryFriendlyByteBuf, SummonMarks> {
      public SyncHandler() {
      }

      @Override
      public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull SummonMarks data) {
         FSFStreamCodecs.OPTIONAL_UUID.encode(buf, Optional.ofNullable(data.targetUuid));
      }

      @Override
      public @NotNull SummonMarks decode(@NotNull RegistryFriendlyByteBuf buf) {
         SummonMarks data = new SummonMarks();
         Optional<UUID> optional = FSFStreamCodecs.OPTIONAL_UUID.decode(buf);
         data.targetUuid = optional.orElse(null);
         return data;
      }
   }
}
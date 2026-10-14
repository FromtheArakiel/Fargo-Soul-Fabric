package dev.arakiel.fargosoulfabric.common.minion;

import dev.arakiel.fargosoulfabric.register.FargoSoulDamageSources;
import dev.arakiel.fargosoulfabric.common.attachment.TargetCache;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityDamageSource;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityGoalSelector;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.SyncFieldDispatcher;
import dev.arakiel.fargosoulfabric.register.FargoSoulDamageSources;
import dev.arakiel.fargosoulfabric.utils.FargoSoulStreamCodecs;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class Minion extends AttachmentEntity {
   protected LivingEntity target;
   protected boolean targetChange = false;
   protected MinionSlotType slotType = MinionSlotType.None;
   protected int slotCost = 1;
   protected int order = 0;
   protected int sameSize = 1;

   public Minion(AttachmentEntityType<?> type) {
      super(type);
   }

   public abstract int getSearchDistance();

   @Override
   protected void registerSyncFields(SyncFieldDispatcher fields) {
      super.registerSyncFields(fields);
      fields.field(
         FargoSoulStreamCodecs.INT,
         () -> this.target != null ? this.target.getId() : -1,
         (level, id) -> this.target = level.getEntity(id) instanceof LivingEntity living ? living : null
      );
      fields.field(FargoSoulStreamCodecs.MINION_SLOT_TYPE, this::getSlotType, this::setSlotType);
      fields.field(FargoSoulStreamCodecs.INT, this::getSlotCost, this::setSlotCost);
      fields.field(FargoSoulStreamCodecs.INT, this::getOrder, this::setOrder);
      fields.field(FargoSoulStreamCodecs.INT, this::getSameSize, this::setSameSize);
   }

   @NotNull
   @Override
   public DamageSource getDamageSource() {
      return new AttachmentEntityDamageSource(
         FargoSoulDamageSources.getDamageTypeHolder(FargoSoulDamageSources.Summon, this.getLevel()), null, this.owner, this.getPos(), this
      );
   }

   @Override
   public boolean isAlive() {
      return this.owner != null && this.owner.isAlive();
   }

   @Override
   public void onLevelChange() {
      this.init(this.getCurrentPathNode().modifyPos(this.getOwner().getBoundingBox().getCenter()));
      if (this.slotType == MinionSlotType.Sentry) {
         this.setRemove();
      }
   }

   public long getSameHash() {
      return (long)Objects.hash(this.getType()) * 43L;
   }

   public int getSlotCost() {
      return this.slotCost;
   }

   public int getOrder() {
      return this.order;
   }

   public void setOrder(int order) {
      this.order = order;
   }

   public void setSameSize(int sameSize) {
      this.sameSize = sameSize;
   }

   public void setSlotCost(int slotCost) {
      this.slotCost = slotCost;
   }

   public int getSameSize() {
      return this.sameSize;
   }

   @Override
   public void tick() {
      super.tick();
      this.setTargetChange(false);
      this.setTarget(this.searchTarget());
      this.goalSelector.tick();
   }

   public LivingEntity searchTarget() {
      int distance = this.getSearchDistance();
      if (distance > 0 && this.owner != null) {
         TargetCache targetCache = this.getTargetCache();
         List<LivingEntity> targets = targetCache.getEntitiesInRadius(
            this.owner.getBoundingBox().getCenter(), (double)distance, living -> targetCache.isVisibility(this.owner, living) && targetCache.isTarget(living)
         );
         if (!targets.isEmpty()) {
            return targetCache.getNewTarget(this, targets, 0.0F, true);
         }
      }

      return null;
   }

   public LivingEntity getTarget() {
      return this.target;
   }

   public void setTarget(LivingEntity target) {
      if (this.target != null && this.target != target) {
         this.setTargetChange(true);
      }

      this.target = target;
   }

   public boolean isTargetChange() {
      return this.targetChange;
   }

   public void setTargetChange(boolean targetChange) {
      this.targetChange = targetChange;
   }

   public AttachmentEntityGoalSelector getGoalSelector() {
      return this.goalSelector;
   }

   public MinionSlotType getSlotType() {
      return this.slotType;
   }

   public void setSlotType(MinionSlotType slotType) {
      this.slotType = slotType;
   }

   public SoundSource getSoundSource() {
      return this.owner != null ? this.owner.getSoundSource() : SoundSource.MASTER;
   }
}

package dev.arakiel.fargosoulfabric.common.attachmentEntity;

public abstract class AttachmentEntityGoal<T extends AttachmentEntity> {
   protected final T minion;

   public AttachmentEntityGoal(T minion) {
      this.minion = minion;
   }

   public abstract boolean canUse();

   public boolean canContinueToUse() {
      return this.canUse();
   }

   public boolean isInterruptable() {
      return true;
   }

   public void start() {
   }

   public void tick() {
   }

   public void stop() {
   }
}

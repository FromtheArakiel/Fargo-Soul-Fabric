package dev.arakiel.fargosoulfabric.common.summon;

public abstract class SummonGoal<T extends Summon> {
   protected final T minion;

   public SummonGoal(T minion) {
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

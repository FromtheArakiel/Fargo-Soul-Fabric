package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import java.util.TreeSet;

public class AttachmentEntityGoalSelector {
   private final TreeSet<AttachmentEntityGoalSelector.PrioritizedGoal> availableGoals = new TreeSet<>();
   private AttachmentEntityGoalSelector.PrioritizedGoal currentGoal;

   public AttachmentEntityGoalSelector() {
   }

   public AttachmentEntityGoal<?> getCurrentGoal() {
      return this.currentGoal != null ? this.currentGoal.goal : null;
   }

   public void addGoal(int priority, AttachmentEntityGoal<?> goal) {
      this.availableGoals.add(new AttachmentEntityGoalSelector.PrioritizedGoal(priority, goal));
   }

   public void removeGoal(AttachmentEntityGoal<?> goal) {
      this.availableGoals.removeIf(p -> p.goal == goal);
      if (this.currentGoal != null && this.currentGoal.goal == goal) {
         this.currentGoal.goal.stop();
         this.currentGoal = null;
      }
   }

   public void tick() {
      if (this.currentGoal != null && !this.currentGoal.goal.canContinueToUse()) {
         this.currentGoal.goal.stop();
         this.currentGoal = null;
      }

      for (AttachmentEntityGoalSelector.PrioritizedGoal potential : this.availableGoals) {
         if (potential.goal.canUse()) {
            if (this.currentGoal == null) {
               potential.goal.start();
               this.currentGoal = potential;
            } else if (potential != this.currentGoal && potential.priority < this.currentGoal.priority && this.currentGoal.goal.isInterruptable()) {
               this.currentGoal.goal.stop();
               potential.goal.start();
               this.currentGoal = potential;
            }
            break;
         }
      }

      if (this.currentGoal != null) {
         this.currentGoal.goal.tick();
      }
   }

   private static record PrioritizedGoal(int priority, AttachmentEntityGoal<?> goal) implements Comparable<AttachmentEntityGoalSelector.PrioritizedGoal> {

      public int compareTo(AttachmentEntityGoalSelector.PrioritizedGoal o) {
         int cmp = Integer.compare(this.priority, o.priority);
         return cmp != 0 ? cmp : Integer.compare(System.identityHashCode(this), System.identityHashCode(o));
      }
   }
}

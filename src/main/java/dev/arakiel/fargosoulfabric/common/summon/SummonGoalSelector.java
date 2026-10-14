package dev.arakiel.fargosoulfabric.common.summon;

import java.util.TreeSet;

public class SummonGoalSelector {
   private final TreeSet<SummonGoalSelector.PrioritizedGoal> availableGoals = new TreeSet<>();
   private SummonGoalSelector.PrioritizedGoal currentGoal;

   public SummonGoalSelector() {
   }

   public SummonGoal<?> getCurrentGoal() {
      return this.currentGoal != null ? this.currentGoal.goal : null;
   }

   public void addGoal(int priority, SummonGoal<?> goal) {
      this.availableGoals.add(new SummonGoalSelector.PrioritizedGoal(priority, goal));
   }

   public void removeGoal(SummonGoal<?> goal) {
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

      for (SummonGoalSelector.PrioritizedGoal potential : this.availableGoals) {
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

   private static record PrioritizedGoal(int priority, SummonGoal<?> goal) implements Comparable<SummonGoalSelector.PrioritizedGoal> {

      public int compareTo(SummonGoalSelector.PrioritizedGoal o) {
         int cmp = Integer.compare(this.priority, o.priority);
         return cmp != 0 ? cmp : Integer.compare(System.identityHashCode(this), System.identityHashCode(o));
      }
   }
}

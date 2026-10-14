package dev.arakiel.fargosoulfabric.common.attachmentEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannedPath {
   private final String identifier;
   private final List<PathNode> nodes;
   private final Map<Integer, Runnable> plannedRunnable = new HashMap<>();
   private int currentIndex = 0;

   public PlannedPath(String identifier, List<PathNode> nodes) {
      this.identifier = identifier;
      this.nodes = nodes;
   }

   public PlannedPath(List<PathNode> nodes) {
      this("path", nodes);
   }



   public String getIdentifier() {
      return this.identifier;
   }

   public List<PathNode> getNodes() {
      return this.nodes;
   }

   public int getCurrentIndex() {
      return this.currentIndex;
   }

   public void run(int currentIndex, Runnable runnable) {
      this.plannedRunnable.put(currentIndex, runnable);
   }

   public PathNode advance() {
      if (this.isFinished()) {
         return null;
      } else {
         Runnable runnable = this.plannedRunnable.get(this.currentIndex);
         if (runnable != null) {
            runnable.run();
         }

         return this.nodes.get(this.currentIndex++);
      }
   }

   public boolean isFinished() {
      return this.currentIndex >= this.nodes.size();
   }
}

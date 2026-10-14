package dev.arakiel.fargosoulfabric.client.dynamicLight;

import dev.arakiel.fargosoulfabric.client.mixin.LevelRendererAccessor;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class DynamicLightDispatcher {
   public static final DynamicLightDispatcher INSTANCE = new DynamicLightDispatcher(7.75);
   private final double maxRadius;
   private final double maxRadiusSquared;
   private final int maxStrength;
   private final double falloff;
   private ObjectOpenHashSet<DynamicLightDispatcher.LightSource> current;
   private ObjectOpenHashSet<DynamicLightDispatcher.LightSource> previous;
   private final LongOpenHashSet dirtySections;
   private final Long2ObjectOpenHashMap<DynamicLightDispatcher.LightSource[]> emptySnapshot;
   private volatile Long2ObjectOpenHashMap<DynamicLightDispatcher.LightSource[]> snapshot;
   private boolean changed;

   private DynamicLightDispatcher(double maxRadius) {
      this.maxRadius = maxRadius;
      this.maxRadiusSquared = maxRadius * maxRadius;
      this.maxStrength = 240;
      this.falloff = (double)this.maxStrength / maxRadius;
      this.current = new ObjectOpenHashSet<>();
      this.previous = new ObjectOpenHashSet<>();
      this.dirtySections = new LongOpenHashSet();
      this.emptySnapshot = new Long2ObjectOpenHashMap<>();
      this.snapshot = this.emptySnapshot;
   }

   public void addLightSource(Vec3 position, int level) {
      this.add(new DynamicLightDispatcher.LightSource(position, Mth.clamp(level, 0, this.maxStrength / 16) * 16));
   }

   public void addLightSource(Vec3 position, float strength) {
      this.add(new DynamicLightDispatcher.LightSource(position, Mth.clamp(Math.round(strength * (float)this.maxStrength), 0, this.maxStrength)));
   }

   public void addLightSource(DynamicLightDispatcher.LightSource source) {
      this.add(source);
   }

   /** Declares a light at {@code position} for this frame; called by the summon renderers. */
   public static void addLightSources(Vec3 position, int level) {
      INSTANCE.addLightSource(position, level);
   }

   private void add(DynamicLightDispatcher.LightSource source) {
      if (source.strength() > 0 && this.current.add(source) && !this.previous.contains(source)) {
         this.markImpactSections(source);
         this.changed = true;
      }
   }

   public void update(LevelRendererAccessor levelRenderer) {
      ObjectIterator<DynamicLightDispatcher.LightSource> swap = this.previous.iterator();

      while (swap.hasNext()) {
         DynamicLightDispatcher.LightSource light = swap.next();
         if (!this.current.contains(light)) {
            this.markImpactSections(light);
            this.changed = true;
         }
      }

      ObjectOpenHashSet<DynamicLightDispatcher.LightSource> swapx = this.previous;
      this.previous = this.current;
      this.current = swapx;
      this.current.clear();
      this.flushDirtySections(levelRenderer);
      if (this.changed) {
         this.snapshot = this.buildSnapshot();
         this.changed = false;
      }
   }

   public int getDynamicLight(BlockAndTintGetter level, BlockState state, BlockPos blockPos, int originalLight) {
      if (this.snapshot.isEmpty()) {
         return originalLight;
      } else {
         int light = originalLight;
         if (!state.isSolidRender(level, blockPos)) {
            double strength = this.getDynamicLightLevel((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5);
            if (strength > (double)(LightTexture.block(originalLight) * 16)) {
               light = withDynamicLight(originalLight, strength);
            }
         }

         return light;
      }
   }

   public int getDynamicLight(Vec3 eyePos, int originalLight) {
      int light = originalLight;
      double strength = this.getDynamicLightLevel(eyePos.x, eyePos.y, eyePos.z);
      if (strength > (double)(LightTexture.block(originalLight) * 16)) {
         light = withDynamicLight(originalLight, strength);
      }

      return light;
   }

   private double getDynamicLightLevel(double x, double y, double z) {
      Long2ObjectOpenHashMap<DynamicLightDispatcher.LightSource[]> sources = this.snapshot;
      if (sources.isEmpty()) {
         return 0.0;
      } else {
         double result = 0.0;
         int sectionX = Mth.floor(x) >> 4;
         int sectionY = Mth.floor(y) >> 4;
         int sectionZ = Mth.floor(z) >> 4;
         double offsetX = x - (double)(sectionX << 4);
         double offsetY = y - (double)(sectionY << 4);
         double offsetZ = z - (double)(sectionZ << 4);
         int x0 = offsetX <= this.maxRadius ? sectionX - 1 : sectionX;
         int x1 = offsetX >= 16.0 - this.maxRadius ? sectionX + 1 : sectionX;
         int y0 = offsetY <= this.maxRadius ? sectionY - 1 : sectionY;
         int y1 = offsetY >= 16.0 - this.maxRadius ? sectionY + 1 : sectionY;
         int z0 = offsetZ <= this.maxRadius ? sectionZ - 1 : sectionZ;
         int z1 = offsetZ >= 16.0 - this.maxRadius ? sectionZ + 1 : sectionZ;

         for (int bx = x0; bx <= x1; bx++) {
            for (int by = y0; by <= y1; by++) {
               for (int bz = z0; bz <= z1; bz++) {
                  DynamicLightDispatcher.LightSource[] group = sources.get(SectionPos.asLong(bx, by, bz));
                  if (group != null) {
                     for (DynamicLightDispatcher.LightSource lightSource : group) {
                        Vec3 pos = lightSource.position();
                        double dx = x - pos.x;
                        double dy = y - pos.y;
                        double dz = z - pos.z;
                        double distSq = dx * dx + dy * dy + dz * dz;
                        if (distSq <= this.maxRadiusSquared) {
                           double strength = (double)lightSource.strength() - Math.sqrt(distSq) * this.falloff;
                           if (strength > result) {
                              result = strength;
                           }
                        }
                     }
                  }
               }
            }
         }

         return Mth.clamp(result, 0.0, (double)this.maxStrength);
      }
   }

   private Long2ObjectOpenHashMap<DynamicLightDispatcher.LightSource[]> buildSnapshot() {
      if (this.previous.isEmpty()) {
         return this.emptySnapshot;
      } else {
         Long2ObjectOpenHashMap<List<DynamicLightDispatcher.LightSource>> groups = new Long2ObjectOpenHashMap<>();

         for (DynamicLightDispatcher.LightSource light : this.previous) {
            groups.computeIfAbsent(sectionOf(light), s -> new ArrayList<>()).add(light);
         }

         Long2ObjectOpenHashMap<DynamicLightDispatcher.LightSource[]> snapshot =
                 new Long2ObjectOpenHashMap<>(groups.size() * 2);

         for (Entry<List<DynamicLightDispatcher.LightSource>> group : groups.long2ObjectEntrySet()) {
            snapshot.put(group.getLongKey(), group.getValue().toArray(new DynamicLightDispatcher.LightSource[0]));
         }

         return snapshot;
      }
   }

   private void markImpactSections(DynamicLightDispatcher.LightSource source) {
      Vec3 position = source.position();
      double radius = (double)Mth.clamp(source.strength(), 0, this.maxStrength) / this.falloff;
      int minX = SectionPos.blockToSectionCoord(position.x() - radius);
      int maxX = SectionPos.blockToSectionCoord(position.x() + radius);
      int minY = SectionPos.blockToSectionCoord(position.y() - radius);
      int maxY = SectionPos.blockToSectionCoord(position.y() + radius);
      int minZ = SectionPos.blockToSectionCoord(position.z() - radius);
      int maxZ = SectionPos.blockToSectionCoord(position.z() + radius);

      for (int cx = minX; cx <= maxX; cx++) {
         for (int cy = minY; cy <= maxY; cy++) {
            for (int cz = minZ; cz <= maxZ; cz++) {
               this.dirtySections.add(SectionPos.asLong(cx, cy, cz));
            }
         }
      }
   }

   private void flushDirtySections(LevelRendererAccessor levelRenderer) {
      LongIterator it = this.dirtySections.iterator();

      while (it.hasNext()) {
         long section = it.nextLong();
         levelRenderer.callSetSectionDirty(SectionPos.x(section), SectionPos.y(section), SectionPos.z(section), false);
      }

      this.dirtySections.clear();
   }

   private static long sectionOf(DynamicLightDispatcher.LightSource source) {
      Vec3 pos = source.position();
      return SectionPos.asLong(SectionPos.blockToSectionCoord(pos.x()), SectionPos.blockToSectionCoord(pos.y()), SectionPos.blockToSectionCoord(pos.z()));
   }

   private static int withDynamicLight(int originalLight, double strength) {
      int smooth = Mth.clamp((int)strength + 8, 8, 248);
      return originalLight & -1048576 | smooth;
   }

   public static final class LightSource {
      private final Vec3 position;
      private final int strength;
      private final int hash;

      public LightSource(Vec3 position, int strength) {
         this.position = position;
         this.strength = strength;
         this.hash = Long.hashCode(
            Double.doubleToLongBits(position.x) * -7046029254386353131L
               ^ Double.doubleToLongBits(position.y) * -4417276706812531889L
               ^ Double.doubleToLongBits(position.z) * 1609587929392839161L
               ^ (long)strength * 2870177450012600261L
         );
      }

      public Vec3 position() {
         return this.position;
      }

      public int strength() {
         return this.strength;
      }

      @Override
      public int hashCode() {
         return this.hash;
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else {
            return !(object instanceof DynamicLightDispatcher.LightSource other)
               ? false
               : this.strength == other.strength
                  && this.position.x == other.position.x
                  && this.position.y == other.position.y
                  && this.position.z == other.position.z;
         }
      }
   }
}

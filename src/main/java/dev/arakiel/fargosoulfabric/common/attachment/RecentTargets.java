package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class RecentTargets implements Attachments.Bound {
   private final Int2BooleanOpenHashMap visibilityCache = new Int2BooleanOpenHashMap();
   private final Int2BooleanOpenHashMap targetCache = new Int2BooleanOpenHashMap();
   private final Int2IntOpenHashMap hurterHistory = new Int2IntOpenHashMap();
   private final Int2FloatOpenHashMap distanceCache = new Int2FloatOpenHashMap();
   private final Long2ObjectOpenHashMap<List<LivingEntity>> spatialGroups = new Long2ObjectOpenHashMap<>();
   private final Long2ObjectOpenHashMap<LevelChunk> chunkCache = new Long2ObjectOpenHashMap<>();
   private final MutableBlockPos mutablePos = new MutableBlockPos();
   private ServerPlayer owner;
   private ServerLevel serverLevel;

   public RecentTargets() {
      this.owner = null;
   }

   public RecentTargets(AttachmentTarget owner) {
      if (owner instanceof ServerPlayer player) {
         this.owner = player;
      } else {
         throw new IllegalArgumentException(owner + " is not a valid RecentTargets");
      }
   }

   @Override
   public void bind(AttachmentTarget target) {
      if (this.owner == null && target instanceof ServerPlayer holder) {
         this.owner = holder;
      }
   }

   public void tick() {
      this.serverLevel = this.owner.serverLevel();
      this.visibilityCache.clear();
      this.targetCache.clear();
      this.hurterHistory.replaceAll((key, value) -> value - 1);
      this.hurterHistory.values().removeIf(value -> value <= 0);
      this.distanceCache.clear();
      this.spatialGroups.clear();
      this.chunkCache.clear();
   }

   public void record(LivingEntity target, int time) {
      this.hurterHistory.put(target.getUUID().hashCode(), time);
   }

   /** Whether anything is cached at all, the summons use it to skip their search. */
   public boolean isEmpty() {
      return this.spatialGroups.isEmpty();
   }

   public boolean isTarget(@Nullable LivingEntity target) {
      return target != null && this.owner != target && target.isAlive() ? this.targetCache.computeIfAbsent(target.getUUID().hashCode(), k -> {
         if (target instanceof Enemy) {
            return true;
         } else {
            if (target instanceof Targeting targeting && targeting.getTarget() == this.owner) {
               return true;
            }

            return this.hurterHistory.containsKey(target.getUUID().hashCode());
         }
      }) : false;
   }

   private static long cellKey(int x, int y, int z) {
      return ((long)x & 67108863L) << 38 | ((long)y & 67108863L) << 12 | (long)z & 67108863L;
   }

   public List<LivingEntity> getEntitiesInRadius(Vec3 pos, double radius, @Nullable Predicate<LivingEntity> filter) {
      List<LivingEntity> result = new ArrayList<>();
      if (!(radius <= 0.0) && this.serverLevel != null) {
         double radiusSq = radius * radius;
         int minCellX = Mth.floor(pos.x - radius) >> 4;
         int maxCellX = Mth.floor(pos.x + radius) >> 4;
         int minCellY = Mth.floor(pos.y - radius) >> 4;
         int maxCellY = Mth.floor(pos.y + radius) >> 4;
         int minCellZ = Mth.floor(pos.z - radius) >> 4;
         int maxCellZ = Mth.floor(pos.z + radius) >> 4;
         AABB searchBox = null;

         for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
               for (int cellZ = minCellZ; cellZ <= maxCellZ; cellZ++) {
                  if (!this.spatialGroups.containsKey(cellKey(cellX, cellY, cellZ))) {
                     AABB cellBox = cellBox(cellX, cellY, cellZ);
                     searchBox = searchBox == null ? cellBox : searchBox.minmax(cellBox);
                  }
               }
            }
         }

         if (searchBox != null) {
            for (LivingEntity entity : this.serverLevel.getEntitiesOfClass(LivingEntity.class, searchBox)) {
               if (this.owner != entity && entity.isAlive()) {
                  int entityCellX = Mth.floor(entity.getX()) >> 4;
                  int entityCellY = Mth.floor(entity.getY()) >> 4;
                  int entityCellZ = Mth.floor(entity.getZ()) >> 4;
                  if (entityCellX >= minCellX
                     && entityCellX <= maxCellX
                     && entityCellY >= minCellY
                     && entityCellY <= maxCellY
                     && entityCellZ >= minCellZ
                     && entityCellZ <= maxCellZ) {
                     List<LivingEntity> cell = this.spatialGroups
                        .computeIfAbsent(cellKey(entityCellX, entityCellY, entityCellZ), key -> new ArrayList<>());
                     if (cell.stream().noneMatch(existing -> existing.getUUID().equals(entity.getUUID()))) {
                        cell.add(entity);
                     }
                  }
               }
            }
         }

         for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
               for (int cellZx = minCellZ; cellZx <= maxCellZ; cellZx++) {
                  List<LivingEntity> cell = this.spatialGroups.get(cellKey(cellX, cellY, cellZx));
                  if (cell != null) {
                     for (LivingEntity entityx : cell) {
                        if (entityx.isAlive()
                           && entityx.getBoundingBox().getCenter().distanceToSqr(pos) <= radiusSq
                           && (filter == null || filter.test(entityx))) {
                           result.add(entityx);
                        }
                     }
                  }
               }
            }
         }

         return result;
      } else {
         return result;
      }
   }

   private static AABB cellBox(int cellX, int cellY, int cellZ) {
      return new AABB(
         (double)(cellX << 4), (double)(cellY << 4), (double)(cellZ << 4), (double)(cellX + 1 << 4), (double)(cellY + 1 << 4), (double)(cellZ + 1 << 4)
      );
   }

   public LivingEntity getNewTarget(Minion minion, List<LivingEntity> targets, float ownerWarningDistance, boolean selfCenter) {
      LivingEntity owner = minion.getOwner();
      LivingEntity currentTarget = minion.getTarget();
      LivingEntity newTarget = null;
      double bestScore = Double.MAX_VALUE;

      for (LivingEntity entity : targets) {
         if (Attachments.get(owner, FSFAttachmentRegister.TARGET_CACHE).isTarget(entity)) {
            return entity;
         }

         double score = selfCenter ? (double)this.getDistance(minion, entity) : (double)this.getDistance(owner, entity);
         if (ownerWarningDistance > 0.0F && this.getDistance(owner, entity) < ownerWarningDistance) {
            score -= 10000.0;
         }

         if (entity == currentTarget) {
            score -= 1000.0;
         }

         score += (double)((entity.getId() * 31 + minion.hashCode() * 17) % 5 * 40);
         if (score < bestScore) {
            bestScore = score;
            newTarget = entity;
         }
      }

      return newTarget;
   }

   public float getDistance(Minion minion, LivingEntity living) {
      int key = minion.getUuid().hashCode() + living.getUUID().hashCode();
      return this.distanceCache.computeIfAbsent(key,
              k -> (float) minion.getPos().distanceTo(living.getBoundingBox().getCenter()));
   }

   public float getDistance(LivingEntity living1, LivingEntity living2) {
      int key = living1.getUUID().hashCode() + living2.getUUID().hashCode();
      return this.distanceCache.computeIfAbsent(key,
              k -> (float) living1.getEyePosition().distanceTo(living2.getBoundingBox().getCenter()));
   }

   public float getSummonSearchRange(@Nullable Player player, float distance) {
      if (player != null) {
         AttributeInstance instance = player.getAttribute(FSFAttributeRegister.SummonSearchRange);
         if (instance != null) {
            distance *= (float)instance.getValue();
         }
      }

      return distance;
   }

   @Deprecated
   public boolean isVisibility(Summon summon, LivingEntity living) {
      Integer key = summon.getUuid().hashCode() + living.getUUID().hashCode();
      return this.visibilityCache.computeIfAbsent(key, k -> this.hasLineOfSight(summon.getPos(), living.getBoundingBox().getCenter()));
   }

   public boolean isVisibility(@Nullable LivingEntity living1, @Nullable LivingEntity living2) {
      if (living1 != null && living2 != null) {
         Integer key = living1.getUUID().hashCode() + living2.getUUID().hashCode();
         return this.visibilityCache.computeIfAbsent(key, k -> this.hasLineOfSight(living1.getEyePosition(), living2.getEyePosition()));
      } else {
         return false;
      }
   }

   private boolean hasLineOfSight(Vec3 from, Vec3 to) {
      if (this.serverLevel == null) {
         return false;
      } else if (from.distanceToSqr(to) > 16384.0) {
         return false;
      } else {
         double startX = Mth.lerp(-1.0E-7, from.x, to.x);
         double startY = Mth.lerp(-1.0E-7, from.y, to.y);
         double startZ = Mth.lerp(-1.0E-7, from.z, to.z);
         double endX = Mth.lerp(-1.0E-7, to.x, from.x);
         double endY = Mth.lerp(-1.0E-7, to.y, from.y);
         double endZ = Mth.lerp(-1.0E-7, to.z, from.z);
         int curX = Mth.floor(endX);
         int curY = Mth.floor(endY);
         int curZ = Mth.floor(endZ);
         double dx = startX - endX;
         double dy = startY - endY;
         double dz = startZ - endZ;
         int stepX = Mth.sign(dx);
         int stepY = Mth.sign(dy);
         int stepZ = Mth.sign(dz);
         double tDeltaX = stepX == 0 ? Double.MAX_VALUE : (double)stepX / dx;
         double tDeltaY = stepY == 0 ? Double.MAX_VALUE : (double)stepY / dy;
         double tDeltaZ = stepZ == 0 ? Double.MAX_VALUE : (double)stepZ / dz;
         double tMaxX = tDeltaX * (stepX > 0 ? 1.0 - Mth.frac(endX) : Mth.frac(endX));
         double tMaxY = tDeltaY * (stepY > 0 ? 1.0 - Mth.frac(endY) : Mth.frac(endY));
         double tMaxZ = tDeltaZ * (stepZ > 0 ? 1.0 - Mth.frac(endZ) : Mth.frac(endZ));
         int minBuildHeight = this.serverLevel.getMinBuildHeight();
         int maxBuildHeight = this.serverLevel.getMaxBuildHeight();
         long lastChunkKey = Long.MIN_VALUE;
         LevelChunk chunk = null;
         int lastSectionY = Integer.MIN_VALUE;
         LevelChunkSection section = null;

         for (int steps = 0; steps < 128; steps++) {
            if (tMaxX > 1.0 && tMaxY > 1.0 && tMaxZ > 1.0) {
               return true;
            }

            if (tMaxX < tMaxY) {
               if (tMaxX < tMaxZ) {
                  curX += stepX;
                  tMaxX += tDeltaX;
               } else {
                  curZ += stepZ;
                  tMaxZ += tDeltaZ;
               }
            } else if (tMaxY < tMaxZ) {
               curY += stepY;
               tMaxY += tDeltaY;
            } else {
               curZ += stepZ;
               tMaxZ += tDeltaZ;
            }

            if (curY >= minBuildHeight && curY < maxBuildHeight) {
               int chunkX = SectionPos.blockToSectionCoord(curX);
               int chunkZ = SectionPos.blockToSectionCoord(curZ);
               long chunkKey = ChunkPos.asLong(chunkX, chunkZ);
               if (chunkKey != lastChunkKey) {
                  chunk = this.chunkCache.get(chunkKey);
                  if (chunk == null) {
                     chunk = this.serverLevel.getChunkSource().getChunkNow(chunkX, chunkZ);
                     if (chunk != null) {
                        this.chunkCache.put(chunkKey, chunk);
                     }
                  }

                  lastChunkKey = chunkKey;
                  lastSectionY = Integer.MIN_VALUE;
               }

               if (chunk == null) {
                  return false;
               }

               int sectionY = chunk.getSectionIndex(curY);
               if (sectionY != lastSectionY) {
                  section = chunk.getSection(sectionY);
                  lastSectionY = sectionY;
               }

               if (section != null && !section.hasOnlyAir()) {
                  BlockState blockState = section.getBlockState(curX & 15, curY & 15, curZ & 15);
                  if (blockState.canOcclude()) {
                     if (blockState.isCollisionShapeFullBlock(this.serverLevel, this.mutablePos.set(curX, curY, curZ))) {
                        return false;
                     }

                     VoxelShape voxelShape = blockState.getCollisionShape(this.serverLevel, this.mutablePos);
                     if (!voxelShape.isEmpty()) {
                        BlockHitResult hitResult = voxelShape.clip(from, to, this.mutablePos);
                        if (hitResult != null) {
                           return false;
                        }
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
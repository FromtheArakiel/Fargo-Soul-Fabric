package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Targeting;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;
import java.util.function.Predicate;

public class SoulTargets {

    public static SoulTargets get(Player player) {
        return Attachments.get(player, FSFAttachmentRegister.SOUL_TARGET_CACHE);
    }

    private final Int2FloatOpenHashMap distanceCache = new Int2FloatOpenHashMap();
    private final Long2ObjectOpenHashMap<List<LivingEntity>> spatialGroups = new Long2ObjectOpenHashMap<>();

    private static long cellKey(Vec3 pos) {
        return ((long) (Mth.floor(pos.x) >> 4) & 0x3FFFFFFL) << 38 | ((long) (Mth.floor(pos.y) >> 4) & 0x3FFFFFFL) << 12 | ((long) (Mth.floor(pos.z) >> 4) & 0x3FFFFFFL);
    }

    public List<LivingEntity> getEntitiesInRadius(Vec3 pos, float radius, Predicate<LivingEntity> filter) {
        List<LivingEntity> result = new ArrayList<>();
        if (radius <= 0) {
            return result;
        }
        double radiusSq = radius * radius;
        int minCellX = Mth.floor(pos.x - radius) >> 4;
        int maxCellX = Mth.floor(pos.x + radius) >> 4;
        int minCellY = Mth.floor(pos.y - radius) >> 4;
        int maxCellY = Mth.floor(pos.y + radius) >> 4;
        int minCellZ = Mth.floor(pos.z - radius) >> 4;
        int maxCellZ = Mth.floor(pos.z + radius) >> 4;
        for (int cellX = minCellX; cellX <= maxCellX; cellX++) {
            for (int cellY = minCellY; cellY <= maxCellY; cellY++) {
                for (int cellZ = minCellZ; cellZ <= maxCellZ; cellZ++) {
                    List<LivingEntity> cell = spatialGroups.get(((long) cellX & 0x3FFFFFFL) << 38 | ((long) cellY & 0x3FFFFFFL) << 12 | ((long) cellZ & 0x3FFFFFFL));
                    if (cell == null) {
                        continue;
                    }
                    for (LivingEntity entity : cell) {
                        if (entity.getBoundingBox().getCenter().distanceToSqr(pos) <= radiusSq) {
                            if (filter == null || filter.test(entity)) {
                                result.add(entity);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public float getDistance(LivingEntity living1, LivingEntity living2) {
        int key = living1.getUUID().hashCode() + living2.getUUID().hashCode();
        return distanceCache.computeIfAbsent(key, (IntToDoubleFunction) (k -> (float) living1.getBoundingBox().getCenter().distanceTo(living2.getBoundingBox().getCenter())));
    }

    public void tick(Player player) {
        spatialGroups.clear();
        distanceCache.clear();
        List<LivingEntity> livingEntityList = player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(128));
        for (LivingEntity livingEntity : livingEntityList) {
            if (getDistance(player, livingEntity) < 128 && isTarget(player, livingEntity)) {
                spatialGroups.computeIfAbsent(cellKey(livingEntity.position()), k -> new ArrayList<>()).add(livingEntity);
            }
        }
    }

    public static boolean isTarget(@NotNull Player player, @NotNull LivingEntity target) {
        if (player != target && target.isAlive()) {
            if (target instanceof Enemy) {
                return true;
            }
            if (target instanceof Targeting targeting && targeting.getTarget() == player) {
                return true;
            }
            if (Invulnerability.get(target).hasAttack(player.getUUID())) {
                return true;
            }
            if (Invulnerability.get(player).hasAttack(target.getUUID())) {
                return true;
            }
        }
        return false;
    }
}

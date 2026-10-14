package dev.arakiel.fargosoulfabric.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public final class GroundPathHelper {
   private GroundPathHelper() {
   }

   public static boolean isSolid(ServerLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return state.isAir() ? false : !state.getCollisionShape(level, pos).isEmpty();
   }

   public static boolean isPassable(ServerLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return state.isAir() ? true : state.getCollisionShape(level, pos).isEmpty();
   }

   public static boolean isWalkable(ServerLevel level, BlockPos pos) {
      if (!isPassable(level, pos)) {
         return false;
      } else {
         return !isPassable(level, pos.above()) ? false : isSolid(level, pos.below());
      }
   }

   public static boolean isWalkableNoGround(ServerLevel level, BlockPos pos) {
      return !isPassable(level, pos) ? false : isPassable(level, pos.above());
   }

   public static boolean canJumpTo(ServerLevel level, BlockPos from, BlockPos to) {
      return !isPassable(level, from.above()) ? false : isWalkable(level, to);
   }
}

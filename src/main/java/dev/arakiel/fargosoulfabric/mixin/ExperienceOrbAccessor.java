package dev.arakiel.fargosoulfabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ExperienceOrb.class})
public interface ExperienceOrbAccessor {
   @Invoker
   static boolean callTryMergeToExisting(ServerLevel level, Vec3 pos, int amount) {
      throw new UnsupportedOperationException();
   }
}

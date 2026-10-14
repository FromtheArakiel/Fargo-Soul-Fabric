package dev.arakiel.fargosoulfabric.client.api;

import dev.arakiel.fargosoulfabric.client.dynamicLight.DynamicLightDispatcher;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.DamageInfoData;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface FargoSoulClientAPI {
   static FargoSoulHelper entity(Player player) {
      return FargoSoulHelper.get(player);
   }

   static ParticleHelper particle(Level level) {
      return ParticleHelper.create(level);
   }

   static DamageInfoData.DamageInfoBuilder damageInfo(Level level) {
      return DamageInfoData.build(level);
   }

   static void light(Vec3 pos, int level) {
      DynamicLightDispatcher.INSTANCE.addLightSource(pos, level);
   }

   static void light(Vec3 pos, float strength) {
      DynamicLightDispatcher.INSTANCE.addLightSource(pos, strength);
   }
}

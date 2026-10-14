package dev.arakiel.fargosoulfabric.client.particle.genericParticle;

import dev.arakiel.fargosoulfabric.common.particle.genericParticle.GenericParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import org.jetbrains.annotations.NotNull;

public class GenericParticleProvider implements ParticleProvider<GenericParticleOptions> {
   private final SpriteSet sprite;

   public GenericParticleProvider(SpriteSet sprite) {
      this.sprite = sprite;
   }

   public Particle createParticle(
      @NotNull GenericParticleOptions options, @NotNull ClientLevel level, double x, double y, double z, double vx, double vy, double vz
   ) {
      return GenericParticle.createWithOptions(level, x, y, z, vx, vy, vz, this.sprite, options);
   }
}

package dev.arakiel.fargosoulfabric.common.particle.genericParticle;

import dev.arakiel.fargosoulfabric.register.FargoSoulRegisters;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record GenericParticleOptions(int centerColor, int edgeColor, int lifetime, float spinSpeed, float friction, float scale) implements ParticleOptions {
   public static final MapCodec<GenericParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(
               Codec.INT.fieldOf("centerColor").forGetter(GenericParticleOptions::centerColor),
               Codec.INT.fieldOf("edgeColor").forGetter(GenericParticleOptions::edgeColor),
               Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOptions::lifetime),
               Codec.FLOAT.fieldOf("spinSpeed").forGetter(GenericParticleOptions::spinSpeed),
               Codec.FLOAT.fieldOf("friction").forGetter(GenericParticleOptions::friction),
               Codec.FLOAT.fieldOf("scale").forGetter(GenericParticleOptions::scale)
            )
            .apply(instance, GenericParticleOptions::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, GenericParticleOptions> STREAM_CODEC = StreamCodec.of((buffer, options) -> {
      buffer.writeInt(options.centerColor);
      buffer.writeInt(options.edgeColor);
      buffer.writeInt(options.lifetime);
      buffer.writeFloat(options.spinSpeed);
      buffer.writeFloat(options.friction);
      buffer.writeFloat(options.scale);
   }, buffer -> new GenericParticleOptions(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readFloat(), buffer.readFloat()));

   public GenericParticleOptions(int centerColor, int edgeColor, int lifetime, float spinSpeed, float friction, float scale) {
      this.centerColor = centerColor;
      this.edgeColor = edgeColor;
      this.lifetime = lifetime;
      this.spinSpeed = spinSpeed;
      this.friction = friction;
      this.scale = scale;
   }

   @NotNull
   public ParticleType<GenericParticleOptions> getType() {
      return FargoSoulRegisters.GENERIC_PARTICLE;
   }
}
package dev.arakiel.fargosoulfabric.utils;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.attachment.ParticleBatch;
import dev.arakiel.fargosoulfabric.common.particle.generic.GenericParticleBuilder;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ParticleHelper {
   private final Level level;
   private final Random random = new Random();
   private ParticleOptions particleType;
   private GenericParticleBuilder genericBuilder;
   private double x;
   private double y;
   private double z;
   private double vx = 0.0;
   private double vy = 0.0;
   private double vz = 0.0;
   private int count = 1;
   private double speed = 1.0;
   private double spreadAngle = 0.4;
   private double offsetX = 0.0;
   private double offsetY = 0.0;
   private double offsetZ = 0.0;

   public ParticleHelper(Level level) {
      this.level = level;
   }

   public static ParticleHelper create(Level level) {
      return new ParticleHelper(level);
   }

   public ParticleHelper type(ParticleOptions type) {
      this.particleType = type;
      return this;
   }

   public ParticleHelper generic(Consumer<GenericParticleBuilder> configurator) {
      GenericParticleBuilder builder = GenericParticleBuilder.create();
      configurator.accept(builder);
      this.genericBuilder = builder;
      return this;
   }

   public ParticleHelper generic(GenericParticleBuilder builder) {
      this.genericBuilder = builder;
      return this;
   }

   public ParticleHelper pos(Vec3 position) {
      this.x = position.x;
      this.y = position.y;
      this.z = position.z;
      return this;
   }

   public ParticleHelper pos(double x, double y, double z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public ParticleHelper velocity(Vec3 velocity) {
      this.vx = velocity.x;
      this.vy = velocity.y;
      this.vz = velocity.z;
      return this;
   }

   public ParticleHelper velocity(double vx, double vy, double vz) {
      this.vx = vx;
      this.vy = vy;
      this.vz = vz;
      return this;
   }

   public ParticleHelper count(int count) {
      this.count = count;
      return this;
   }

   public ParticleHelper speed(double speed) {
      this.speed = speed;
      return this;
   }

   public ParticleHelper spread(double spreadAngle) {
      this.spreadAngle = spreadAngle;
      return this;
   }

   public ParticleHelper offset(double x, double y, double z) {
      this.offsetX = x;
      this.offsetY = y;
      this.offsetZ = z;
      return this;
   }

   public ParticleHelper offset(double radius) {
      this.offsetX = this.offsetY = this.offsetZ = radius;
      return this;
   }

   public void emit() {
      if (this.particleType == null && this.genericBuilder == null) {
         throw new IllegalStateException("Particle type not set. Call type() or generic() first.");
      } else {
         boolean server = !this.level.isClientSide();
         ParticleBatch batch = server ? Attachments.get(this.level, FSFAttachmentRegister.BATCHED_PARTICLES) : null;
         if (this.count <= 0) {
            ParticleOptions options = (this.genericBuilder != null ? this.genericBuilder.build() : this.particleType);
            if (server) {
               batch.add(options, this.x, this.y, this.z, this.vx, this.vy, this.vz);
            } else {
               this.level.addParticle(options, this.x, this.y, this.z, this.vx, this.vy, this.vz);
            }
         } else {
            Vec3 baseDir = new Vec3(this.vx, this.vy, this.vz).normalize();

            for (int i = 0; i < this.count; i++) {
               double theta = (this.random.nextDouble() - 0.5) * this.spreadAngle * 2.0;
               double phi = (this.random.nextDouble() - 0.5) * this.spreadAngle * 2.0;
               double speedVar = this.speed * (0.5 + this.random.nextDouble() * 0.5);
               Vec3 scatteredDir = baseDir.yRot((float)theta).xRot((float)phi);
               Vec3 velocity = scatteredDir.scale(speedVar);
               double px = this.x + (this.offsetX > 0.0 ? (this.random.nextDouble() - 0.5) * 2.0 * this.offsetX : 0.0);
               double py = this.y + (this.offsetY > 0.0 ? (this.random.nextDouble() - 0.5) * 2.0 * this.offsetY : 0.0);
               double pz = this.z + (this.offsetZ > 0.0 ? (this.random.nextDouble() - 0.5) * 2.0 * this.offsetZ : 0.0);
               ParticleOptions options = (this.genericBuilder != null ? this.genericBuilder.build() : this.particleType);
               if (server) {
                  batch.add(options, px, py, pz, velocity.x, velocity.y, velocity.z);
               } else {
                  this.level.addParticle(options, px, py, pz, velocity.x, velocity.y, velocity.z);
               }
            }
         }
      }
   }
}
package dev.arakiel.fargosoulfabric.common.particle.generic;

import java.util.Random;

public class GenericParticleBuilder {
   private final Random random = new Random();
   private int centerColor = 16777215;
   private int edgeColor = 16777215;
   private int lifetime = 0;
   private int lifetimeJitter = 0;
   private float spinSpeed = 0.0F;
   private float spinJitter = 0.0F;
   private float friction = 1.0F;
   private float scale = 1.0F;
   private float scaleJitter = 0.0F;

   private GenericParticleBuilder() {
   }

   public static GenericParticleBuilder create() {
      return new GenericParticleBuilder();
   }

   public GenericParticleBuilder centerColor(int rgb) {
      this.centerColor = rgb;
      return this;
   }

   public GenericParticleBuilder edgeColor(int rgb) {
      this.edgeColor = rgb;
      return this;
   }

   public GenericParticleBuilder lifetime(int lifetime) {
      this.lifetime = lifetime;
      return this;
   }

   public GenericParticleBuilder lifetimeRandom(int range) {
      this.lifetimeJitter = range;
      return this;
   }

   public GenericParticleBuilder spin(float spinSpeed) {
      this.spinSpeed = spinSpeed;
      return this;
   }

   public GenericParticleBuilder spinRandom(float range) {
      this.spinJitter = range;
      return this;
   }

   public GenericParticleBuilder friction(float friction) {
      this.friction = friction;
      return this;
   }

   public GenericParticleBuilder scale(float scale) {
      this.scale = scale;
      return this;
   }

   public GenericParticleBuilder scaleRandom(float range) {
      this.scaleJitter = range;
      return this;
   }

   public GenericParticleOptions build() {
      return new GenericParticleOptions(
         this.centerColor,
         this.edgeColor,
         this.lifetime + (this.lifetimeJitter > 0 ? this.random.nextInt(this.lifetimeJitter) : 0),
         this.spinSpeed + (this.spinJitter > 0.0F ? this.random.nextFloat() * this.spinJitter * 2.0F - this.spinJitter : 0.0F),
         this.friction,
         this.scale + (this.scaleJitter > 0.0F ? this.random.nextFloat() * this.scaleJitter : 0.0F)
      );
   }
}

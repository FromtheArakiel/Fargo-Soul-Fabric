package dev.arakiel.fargosoulfabric.client.render.model.virtual;

import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;

public final class VirtualEntityPose {
   private int ageTicks;
   private float bodyYawDegrees;
   private float headYawDegrees = Float.NaN;
   private float headPitchDegrees;
   private float limbSwingPosition;
   private float limbSwingAmount;
   private float attackProgress;
   private int hurtTime;
   private int deathTime;
   private float scale = 1.0F;
   private int color = -1;
   private float alpha = 1.0F;
   private int packedLight = 15728880;
   private boolean crouching;
   private boolean swimming;
   private Consumer<Entity> customizer;

   private VirtualEntityPose() {
   }

   public static VirtualEntityPose create() {
      return new VirtualEntityPose();
   }

   public VirtualEntityPose ageTicks(int ageTicks) {
      this.ageTicks = ageTicks;
      return this;
   }

   public VirtualEntityPose bodyYaw(float degrees) {
      this.bodyYawDegrees = degrees;
      return this;
   }

   public VirtualEntityPose headYaw(float degrees) {
      this.headYawDegrees = degrees;
      return this;
   }

   public VirtualEntityPose look(float bodyYawDegrees, float headYawDegrees, float headPitchDegrees) {
      this.bodyYawDegrees = bodyYawDegrees;
      this.headYawDegrees = headYawDegrees;
      this.headPitchDegrees = headPitchDegrees;
      return this;
   }

   public VirtualEntityPose walk(float limbSwingPosition, float limbSwingAmount) {
      this.limbSwingPosition = limbSwingPosition;
      this.limbSwingAmount = limbSwingAmount;
      return this;
   }

   public VirtualEntityPose attack(float progress) {
      this.attackProgress = progress;
      return this;
   }

   public VirtualEntityPose hurt(int hurtTime) {
      this.hurtTime = hurtTime;
      return this;
   }

   public VirtualEntityPose death(int deathTime) {
      this.deathTime = deathTime;
      return this;
   }

   public VirtualEntityPose crouch(boolean crouching) {
      this.crouching = crouching;
      return this;
   }

   public VirtualEntityPose swim(boolean swimming) {
      this.swimming = swimming;
      return this;
   }

   public VirtualEntityPose scale(float scale) {
      this.scale = scale;
      return this;
   }

   public VirtualEntityPose color(int argb) {
      this.color = argb;
      return this;
   }

   public VirtualEntityPose alpha(float alpha) {
      this.alpha = alpha;
      return this;
   }

   public VirtualEntityPose packedLight(int packedLight) {
      this.packedLight = packedLight;
      return this;
   }

   public VirtualEntityPose customize(Consumer<Entity> customizer) {
      this.customizer = customizer;
      return this;
   }

   int ageTicks() {
      return this.ageTicks;
   }

   float bodyYawDegrees() {
      return this.bodyYawDegrees;
   }

   float headYawDegrees() {
      return Float.isNaN(this.headYawDegrees) ? this.bodyYawDegrees : this.headYawDegrees;
   }

   float headPitchDegrees() {
      return this.headPitchDegrees;
   }

   float limbSwingPosition() {
      return this.limbSwingPosition;
   }

   float limbSwingAmount() {
      return this.limbSwingAmount;
   }

   float attackProgress() {
      return this.attackProgress;
   }

   int hurtTime() {
      return this.hurtTime;
   }

   int deathTime() {
      return this.deathTime;
   }

   float scale() {
      return this.scale;
   }

   int color() {
      return this.color;
   }

   float alpha() {
      return this.alpha;
   }

   int packedLight() {
      return this.packedLight;
   }

   boolean crouching() {
      return this.crouching;
   }

   boolean swimming() {
      return this.swimming;
   }

   Consumer<Entity> customizer() {
      return this.customizer;
   }
}

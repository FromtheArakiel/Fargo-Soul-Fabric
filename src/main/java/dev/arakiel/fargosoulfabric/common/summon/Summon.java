package dev.arakiel.fargosoulfabric.common.summon;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.attachment.Immunities;
import dev.arakiel.fargosoulfabric.common.attachment.RecentTargets;
import dev.arakiel.fargosoulfabric.utils.FSFStreamCodecs;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.util.GeckoLibUtil;

@SuppressWarnings("this-escape")
public abstract class Summon implements GeoAnimatable {
   protected final SummonType<?> type;
   protected final ArrayList<PathNode> historyNodes = new ArrayList<>();
   protected final SummonGoalSelector goalSelector = new SummonGoalSelector();
   protected final SyncFieldDispatcher syncFields = SyncFieldDispatcher.create(this::registerSyncFields);
   protected final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
   protected UUID uuid = UUID.randomUUID();
   protected Player owner = null;
   protected PlannedPath currentPlannedPath = null;
   protected boolean remove = false;
   protected int tickCount = 0;
   protected float damage = 0.0F;
   protected float knockback = 0.0F;
   protected float armorPierce = 0.0F;
   protected boolean clientInit = false;
   protected PathNode currentPathNode = null;

   protected void registerSyncFields(SyncFieldDispatcher fields) {
      fields.field(FSFStreamCodecs.FLOAT, this::getDamage, this::setDamage);
      fields.field(FSFStreamCodecs.FLOAT, this::getKnockback, this::setKnockback);
      fields.field(FSFStreamCodecs.FLOAT, this::getArmorPierce, this::setArmorPierce);
      fields.field(FSFStreamCodecs.INT, this::getTickCount, this::setTickCount);
      fields.field(FSFStreamCodecs.PATH_NODE, this::getCurrentPathNode, this::setCurrentPathNode);
   }

   public Summon(SummonType<?> type) {
      this.type = type;
      this.init(new PathNode(Vec3.ZERO, 0.0F, 0.0F, 0.0F));
      this.registerGoals(this.goalSelector);
   }

   public void registerGoals(SummonGoalSelector goalSelector) {
   }

   public void registerControllers(ControllerRegistrar controllers) {
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.animationCache;
   }

   public double getTick(Object object) {
      return (double)this.tickCount;
   }

   @NotNull
   public DamageSource getDamageSource() {
      return new SummonDamageSource(this.getLevel().damageSources().generic().typeHolder(), null, null, this.getPos(), this);
   }

   public RecentTargets getRecentTargets() {
      return Attachments.get(this.owner, FSFAttachmentRegister.TARGET_CACHE);
   }

   /** Whether {@code target} is something this entity may attack. */
   public boolean isTarget(LivingEntity target) {
      return target != null && this.getRecentTargets().isTarget(target);
   }

   public boolean attack(LivingEntity target, float amount, int tick) {
      Immunities data = Immunities.get(target);
      if (!data.isActive(this.getUuid()) && data.attack(this.getDamageSource(), amount)) {
         data.record(this.getUuid(), tick);
         return true;
      } else {
         return false;
      }
   }

   public void copyAttributes(Summon other) {
      this.setDamage(other.getDamage());
      this.setKnockback(other.getKnockback());
      this.setArmorPierce(other.getArmorPierce());
   }

   public float getDamage() {
      return this.damage;
   }

   public void setArmorPierce(float armorPierce) {
      this.armorPierce = armorPierce;
   }

   public float getArmorPierce() {
      return this.armorPierce;
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public float getKnockback() {
      return this.knockback;
   }

   public void setKnockback(float knockback) {
      this.knockback = knockback;
   }

   public SyncFieldDispatcher getSyncFieldDispatcher() {
      return this.syncFields;
   }

   public void tick() {
   }

   public final void tickCurrentPathNode() {
      Level level = this.getLevel();
      if (!level.isClientSide()) {
         this.tickCount++;
         if (this instanceof IBlockCollision<?> blockCollision) {
            blockCollision.blockCollision();
         }

         if (this instanceof IEntityCollision<?> iEntityCollision) {
            iEntityCollision.entityCollision();
         }
      }

      this.historyNodes.addFirst(this.currentPathNode);
      if (this.historyNodes.size() > 16) {
         this.historyNodes.removeLast();
      }

      if (!level.isClientSide() && this.currentPlannedPath != null && !this.currentPlannedPath.isFinished()) {
         this.currentPathNode = this.currentPlannedPath.advance();
      }
   }

   public void onRemove() {
   }

   public void onLevelChange() {
   }

   public void setPlannedPath(PlannedPath path) {
      this.currentPlannedPath = path;
   }

   public PlannedPath getCurrentPath() {
      return this.currentPlannedPath;
   }

   public PathNode getCurrentPathNode() {
      return this.currentPathNode;
   }

   public boolean isExecutingPath() {
      return this.currentPlannedPath != null && !this.currentPlannedPath.isFinished();
   }

   public void setPath(List<PathNode> nodes) {
      this.currentPlannedPath = new PlannedPath(nodes);
      if (!nodes.isEmpty()) {
         this.currentPathNode = nodes.getFirst();
      }
   }

   public void setPath(PlannedPath plannedPath) {
      List<PathNode> nodes = plannedPath.getNodes();
      if (!nodes.isEmpty()) {
         this.currentPathNode = nodes.getFirst();
      }
   }

   public boolean isRemove() {
      return this.remove;
   }

   public void setRemove() {
      this.remove = true;
   }

   public void init(PathNode node) {
      this.clientInit = false;
      this.currentPathNode = node;
      this.historyNodes.clear();
      this.historyNodes.add(node);
   }

   public ArrayList<PathNode> getHistoryNodes() {
      return this.historyNodes;
   }

   public PathNode getRenderNode(float partialTick) {
      return this.historyNodes.size() > 1 ? this.historyNodes.get(1).lerp(this.currentPathNode, partialTick) : this.currentPathNode;
   }

   public void setCurrentPathNode(PathNode currentPathNode) {
      this.currentPathNode = currentPathNode;
   }

   public boolean isAlive() {
      return true;
   }

   public Vec3 getPos() {
      return this.currentPathNode.pos();
   }

   public void setPos(Vec3 pos) {
      this.currentPathNode = new PathNode(pos, this.getYaw(), this.getPitch(), this.getRoll());
   }

   public float getYaw() {
      return this.currentPathNode.yaw();
   }

   public float getPitch() {
      return this.currentPathNode.pitch();
   }

   public float getRoll() {
      return this.currentPathNode.roll();
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public void setUuid(UUID uuid) {
      this.uuid = uuid;
   }

   public Level getLevel() {
      return this.owner.level();
   }

   public RandomSource getRandom() {
      return this.getLevel().getRandom();
   }

   public int getTickCount() {
      return this.tickCount;
   }

   public void setTickCount(int tickCount) {
      this.tickCount = tickCount;
   }

   public boolean isClientInit() {
      return this.clientInit;
   }

   public void setClientInit(boolean clientInit) {
      this.clientInit = clientInit;
   }

   public Player getOwner() {
      return this.owner;
   }

   public void setOwner(Player owner) {
      this.owner = owner;
   }

   /** Hands this entity to {@code owner}; from now on it ticks with the summons of that player. */
   public void join(Player owner) {
      this.setOwner(owner);
      Attachments.get(owner, FSFAttachmentRegister.ENTITY_DATA).add(this);
   }

   public Vec3 calculateBezierPoint(float delta, Vec3... P) {
      if (P.length == 0) {
         return Vec3.ZERO;
      } else if (P.length == 1) {
         return P[0];
      } else {
         Vec3[] pts = P.clone();

         for (int k = P.length - 1; k > 0; k--) {
            for (int i = 0; i < k; i++) {
               pts[i] = pts[i].lerp(pts[i + 1], (double)delta);
            }
         }

         return pts[0];
      }
   }

   public Vec3 getLookAngle() {
      return Vec3.directionFromRotation(this.getPitch(), this.getYaw()).normalize();
   }

   public Vec3 getCurrentVelocity() {
      Vec3 currentPos = this.getPos();
      ArrayList<PathNode> history = this.getHistoryNodes();
      if (history.size() > 1) {
         Vec3 rawVel = currentPos.subtract(history.getFirst().pos());
         if (rawVel.lengthSqr() > 1.0E-5) {
            return rawVel.normalize();
         }
      }

      return Vec3.directionFromRotation(this.getPitch(), this.getYaw()).normalize();
   }

   public Vec3 getCurrentNormal() {
      Quaternionf q = new Quaternionf()
         .rotateY((float)Math.toRadians((double)(-this.getYaw())))
         .rotateX((float)Math.toRadians((double)this.getPitch()))
         .rotateZ((float)Math.toRadians((double)this.getRoll()));
      Vector3f upV = new Vector3f(0.0F, 1.0F, 0.0F).rotate(q);
      return new Vec3((double)upV.x(), (double)upV.y(), (double)upV.z()).normalize();
   }

   public PathNode getEulerNode(Vec3 pos, Vec3 direction, Vec3 normal) {
      direction = direction.normalize();
      normal = normal.normalize();
      float yaw = (float)Math.toDegrees(Math.atan2(-direction.x, direction.z));
      float pitch = (float)Math.toDegrees(Math.asin(-direction.y));
      float pr = (float)Math.toRadians((double)pitch);
      float yr = (float)Math.toRadians((double)yaw);
      float cp = (float)Math.cos((double)pr);
      float sp = (float)Math.sin((double)pr);
      float cy = (float)Math.cos((double)yr);
      float sy = (float)Math.sin((double)yr);
      Vec3 localY = new Vec3((double)(-sy * sp), (double)cp, (double)(cy * sp));
      Vec3 projLocalY = localY.subtract(direction.scale(localY.dot(direction))).normalize();
      Vec3 projNormal = normal.subtract(direction.scale(normal.dot(direction))).normalize();
      double d = projLocalY.dot(projNormal);
      Vec3 c = projLocalY.cross(projNormal);
      float roll = (float)Math.toDegrees(Math.atan2(c.dot(direction), d));
      return new PathNode(pos, yaw, pitch, roll);
   }

   public SummonType<?> getType() {
      return this.type;
   }
}
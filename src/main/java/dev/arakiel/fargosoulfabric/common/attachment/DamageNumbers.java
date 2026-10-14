package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.damageNumbers.DamageInfo;
import dev.arakiel.fargosoulfabric.common.damageNumbers.IDamageSourceCritical;
import dev.arakiel.fargosoulfabric.common.network.DamageNumbersPayload;
import java.util.ArrayList;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class DamageNumbers {
   private final List<DamageNumbersPayload.Entry> pendingEntries = new ArrayList<>();
   private final Map<ResourceLocation, List<DamageInfo>> activeInfos = new HashMap<>();

   public static void handler(LivingEntity entity, DamageSource damageSource, float damage) {
      Level level = entity.level();
      if (!level.isClientSide() && damageSource.getEntity() instanceof Player) {
         RandomSource random;
         Vec3 pos;
         Vec3 velocity;
         boolean var10000;
         label15: {
            AABB box = entity.getBoundingBox();
            random = entity.getRandom();
            pos = box.getCenter().add(0.0, box.getYsize() / 2.0, 0.0);
            velocity = pos.add(0.0, box.getYsize() / 2.0, 0.0).offsetRandom(random, (float)(box.getXsize() + box.getZsize()) * 0.5F).subtract(pos).normalize();
            if (damageSource instanceof IDamageSourceCritical iDamageSourceCritical && iDamageSourceCritical.fsf$isCritical()) {
               var10000 = true;
               break label15;
            }

            var10000 = false;
         }

         boolean critical = var10000;
         build(level)
            .damageType(damageSource.typeHolder().getRegisteredName())
            .damageAmount(damage)
            .pos(pos)
            .velocity(velocity.scale((double)((float)random.nextInt(50, 70) * 0.01F)))
            .critical(critical)
            .emit();
      }
   }

   /** Sends the damage numbers the server collected during this tick to everyone nearby. */
   public static void tick(ServerLevel level) {
      DamageNumbers damageData = Attachments.get(level, FSFAttachmentRegister.DAMAGE_INFO_DATA);
      if (damageData.size() > 0) {
         List<DamageNumbersPayload.Entry> entries = damageData.drain();
         for (ServerPlayer player : PlayerLookup.world(level)) {
            // Only the numbers this player could actually see. The batch used to go to the whole
            // level, which multiplied the traffic by the player count for no reason.
            List<DamageNumbersPayload.Entry> visible = new ArrayList<>(entries.size());
            for (DamageNumbersPayload.Entry entry : entries) {
               double dx = entry.x() - player.getX();
               double dy = entry.y() - player.getY();
               double dz = entry.z() - player.getZ();
               if (dx * dx + dy * dy + dz * dz <= VISIBLE_RANGE_SQUARED) {
                  visible.add(entry);
               }
            }
            if (!visible.isEmpty()) {
               ServerPlayNetworking.send(player, new DamageNumbersPayload(visible));
            }
         }
      }
   }

   /** Damage numbers further away than this are not sent to a player at all. */
   private static final double VISIBLE_RANGE_SQUARED = 48.0D * 48.0D;

   public DamageNumbers() {
   }

   public static DamageNumbers.DamageInfoBuilder build(Level level) {
      return new DamageNumbers.DamageInfoBuilder(level);
   }

   public void addEntry(DamageNumbersPayload.Entry entry) {
      this.pendingEntries.add(entry);
   }

   public List<DamageNumbersPayload.Entry> drain() {
      List<DamageNumbersPayload.Entry> snapshot = new ArrayList<>(this.pendingEntries);
      this.pendingEntries.clear();
      return snapshot;
   }

   public int size() {
      return this.pendingEntries.size();
   }

   /** Advances the numbers the client is currently drawing, and drops the expired ones. */
   public void tickActiveInfos() {
      this.activeInfos.values().removeIf(infoList -> {
         infoList.removeIf(DamageInfo::tick);
         return infoList.isEmpty();
      });
   }

   public Map<ResourceLocation, List<DamageInfo>> getActiveInfos() {
      return this.activeInfos;
   }

   public static final class DamageInfoBuilder {
      private final Level level;
      private String damageType = "default";
      private float damageAmount;
      private double x;
      private double y;
      private double z;
      private double vx;
      private double vy;
      private double vz;
      private boolean critical = false;

      DamageInfoBuilder(Level level) {
         this.level = level;
      }

      public DamageNumbers.DamageInfoBuilder damageType(Holder<DamageType> damageType) {
         this.damageType = damageType.getRegisteredName();
         return this;
      }

      public DamageNumbers.DamageInfoBuilder damageType(String damageType) {
         this.damageType = damageType;
         return this;
      }

      public DamageNumbers.DamageInfoBuilder damageAmount(float amount) {
         this.damageAmount = amount;
         return this;
      }

      public DamageNumbers.DamageInfoBuilder pos(double x, double y, double z) {
         this.x = x;
         this.y = y;
         this.z = z;
         return this;
      }

      public DamageNumbers.DamageInfoBuilder pos(Vec3 pos) {
         return this.pos(pos.x, pos.y, pos.z);
      }

      public DamageNumbers.DamageInfoBuilder velocity(double vx, double vy, double vz) {
         this.vx = vx;
         this.vy = vy;
         this.vz = vz;
         return this;
      }

      public DamageNumbers.DamageInfoBuilder velocity(Vec3 velocity) {
         return this.velocity(velocity.x, velocity.y, velocity.z);
      }

      public DamageNumbers.DamageInfoBuilder critical(boolean critical) {
         this.critical = critical;
         return this;
      }

      public void emit() {
         if (!this.level.isClientSide() && (double)this.damageAmount >= 0.01) {
            Attachments.get(this.level, FSFAttachmentRegister.DAMAGE_INFO_DATA)
               .addEntry(
                  new DamageNumbersPayload.Entry(this.damageType, this.damageAmount, this.x, this.y, this.z, this.vx, this.vy, this.vz, this.critical)
               );
         }
      }
   }
}

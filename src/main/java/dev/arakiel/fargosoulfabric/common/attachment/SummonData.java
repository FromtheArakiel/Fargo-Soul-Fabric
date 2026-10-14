package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
import dev.arakiel.fargosoulfabric.register.FSFRegisters;
import dev.arakiel.fargosoulfabric.common.Attachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import dev.arakiel.fargosoulfabric.utils.FSFStreamCodecs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SummonData implements Attachments.Bound {
   private final Map<SummonType<?>, List<Summon>> pendingAdd = new HashMap<>();
   private final Map<SummonType<?>, List<Summon>> groups = new HashMap<>();
   private final Map<UUID, Summon> uuidToEntity = new HashMap<>();
   private final List<Summon> renderCache = new ArrayList<>();
   /**
    * Payloads that arrived and are waiting for their owner's client tick to apply them.
    *
    * <p>The queue deliberately does not live on this object: a synced attachment is replaced whole by
    * whatever the codec decodes, so a queue kept here would be dropped as soon as a second update
    * arrived before the client tick drained the first one. The payload carries the owner id, which is
    * what makes it possible to keep the queue on the side, keyed by owner.
    */
   private static final Map<UUID, List<byte[]>> PENDING_PAYLOADS = new java.util.concurrent.ConcurrentHashMap<>();
   private Player owner;
   private Level level = null;
   private boolean changed = false;

   public SummonData() {
      this.owner = null;
   }

   public SummonData(AttachmentTarget owner) {
      if (owner instanceof Player player) {
         this.owner = player;
      } else {
         throw new IllegalArgumentException(owner + " is not a valid SummonData");
      }
   }

   @Override
   public void bind(AttachmentTarget target) {
      if (this.owner == null && target instanceof Player holder) {
         this.owner = holder;
      }
   }

   public void tick() {
      this.uuidToEntity.clear();

      for (List<Summon> list : this.groups.values()) {
         for (Summon entity : list) {
            this.uuidToEntity.put(entity.getUuid(), entity);
         }
      }

      if (this.isClientSide()) {
         this.applyPendingSync();
         this.renderCache.clear();
         if (this.isRunning()) {
            for (List<Summon> list : this.groups.values()) {
               for (Summon entity : list) {
                  entity.tickCurrentPathNode();
                  this.renderCache.add(entity);
               }
            }
         }
      } else if (this.isRunning()) {
         boolean levelChange = this.level != null && this.level != this.owner.level();
         this.level = this.owner.level();
         Map<Long, List<Minion>> sameCache = new HashMap<>();

         for (List<Summon> list : this.groups.values()) {
            for (Summon entity : list) {
               if (entity instanceof Minion minion) {
                  List<Minion> minionList = sameCache.computeIfAbsent(minion.getSameHash(), key -> new ArrayList<>());
                  minion.setOrder(minionList.size());
                  minionList.add(minion);

                  for (Minion minion1 : minionList) {
                     minion1.setSameSize(list.size());
                  }
               }

               if (levelChange) {
                  entity.onLevelChange();
               }

               entity.tick();
               entity.tickCurrentPathNode();
            }
         }

         if (!this.pendingAdd.isEmpty()) {
            for (Entry<SummonType<?>, List<Summon>> entry : this.pendingAdd.entrySet()) {
               List<Summon> entities = this.groups.computeIfAbsent(entry.getKey(), key -> new ArrayList<>());

               for (Summon summon : entry.getValue()) {
                  if (!summon.getCurrentPathNode().pos().equals(Vec3.ZERO)) {
                     summon.setOwner(this.owner);
                     entities.add(summon);
                  }
               }
            }

            this.pendingAdd.clear();
            this.changed = true;
         }

         Map<MinionSlotType, Integer> limits = new HashMap<>();

         for (Minion minion : this.get(Minion.class)) {
            Player owner = minion.getOwner();
            int limit = limits.computeIfAbsent(minion.getSlotType(), slotType -> {
               AttributeInstance instance = switch (slotType) {
                  case Minion -> owner.getAttribute(FSFAttributeRegister.MinionMaxCount);
                  case Sentry -> owner.getAttribute(FSFAttributeRegister.SentryMaxCount);
                  case None -> null;
               };
               return instance != null ? (int)instance.getValue() : 0;
            });
            int slotCost = minion.getSlotCost();
            if (slotCost > 0) {
               if (limit >= slotCost) {
                  limits.put(minion.getSlotType(), limit - slotCost);
               } else {
                  minion.setRemove();
               }
            }
         }

         this.groups.values().removeIf(list -> {
            do {
               list.removeIf(entity -> {
                  if (!entity.isAlive()) {
                     entity.setRemove();
                  }

                  if (entity.isRemove()) {
                     entity.onRemove();
                     this.changed = true;
                     return true;
                  } else {
                     return false;
                  }
               });
            } while (!list.isEmpty() && !list.stream().noneMatch(Summon::isRemove));

            return list.isEmpty();
         });
         if (!this.groups.isEmpty() || this.changed) {
            this.changed = false;
            Attachments.sync(this.owner, FSFAttachmentRegister.ENTITY_DATA);
         }
      }
   }

   @Nullable
   public Summon getEntity(UUID uuid) {
      return this.uuidToEntity.get(uuid);
   }

   public Level getLevel() {
      return this.owner.level();
   }

   public boolean isClientSide() {
      return this.owner.level().isClientSide();
   }

   public boolean isRunning() {
      return this.isClientSide() ? !this.groups.isEmpty() : !this.groups.isEmpty() || !this.pendingAdd.isEmpty() || this.changed;
   }

   public void add(Summon entity) {
      this.pendingAdd.computeIfAbsent(entity.getType(), key -> new ArrayList<>()).add(entity);
   }

   @SuppressWarnings("unchecked")
   public <T extends Summon> List<T> get(SummonType<T> summonType) {
      List<T> result = new ArrayList<>();
      for (Summon entity : this.groups.getOrDefault(summonType, new ArrayList<>())) {
         if (!entity.isRemove()) {
            result.add((T) entity);
         }
      }
      return result;
   }

   public <T extends Summon> List<T> get(Class<T> classType) {
      List<T> result = new ArrayList<>();
      for (List<Summon> list : this.groups.values()) {
         for (Summon entity : list) {
            if (!entity.isRemove() && classType.isInstance(entity)) {
               result.add(classType.cast(entity));
            }
         }
      }
      return result;
   }

   public void remove(SummonType<?> entityType) {
      this.getGroups().getOrDefault(entityType, new ArrayList<>()).forEach(Summon::setRemove);
   }

   public Map<SummonType<?>, List<Summon>> getGroups() {
      return this.groups;
   }

   public List<Summon> getRenderCache() {
      return this.renderCache;
   }

   private void applyPendingSync() {
      Level level = this.getLevel();
      UUID ownerId = this.owner instanceof net.minecraft.world.entity.Entity ownerEntity
              ? ownerEntity.getUUID() : null;
      List<byte[]> queued = ownerId == null ? null : PENDING_PAYLOADS.remove(ownerId);

      if (queued == null) {
         return;
      }
      for (byte[] payload : queued) {
         RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload), level.registryAccess());
         Map<UUID, Summon> existing = new HashMap<>();
         this.groups.values().forEach(listx -> listx.forEach(entityx -> existing.put(entityx.getUuid(), entityx)));
         this.groups.clear();
         int typeCount = buf.readVarInt();

         for (int i = 0; i < typeCount; i++) {
            SummonType<?> entityType =
                  FSFRegisters.ATTACHMENT_ENTITY_TYPES.get(buf.readResourceLocation());
            if (entityType == null) {
               throw new IllegalStateException("Unknown summon type in a sync packet");
            }
            List<Summon> list = this.groups.computeIfAbsent(entityType, kx -> new ArrayList<>());
            int listSize = buf.readVarInt();

            for (int k = 0; k < listSize; k++) {
               UUID uuid = buf.readUUID();
               Summon entity = existing.get(uuid);
               if (entity == null) {
                  entity = (Summon)entityType.factory().get();
                  entity.setUuid(uuid);
               }

               entity.setOwner(this.owner);
               entity.getSyncFieldDispatcher().decode(buf, level);
               if (!buf.readBoolean()) {
                  PathNode pathNode = FSFStreamCodecs.PATH_NODE.decode(buf);
                  ArrayList<PathNode> historyNodes = entity.getHistoryNodes();
                  historyNodes.clear();
                  historyNodes.add(pathNode);
                  historyNodes.add(pathNode);
               }

               list.add(entity);
            }
         }
      }
   }

   /** Sends the summons of one owner to its clients. */
   public static final class SyncHandler implements StreamCodec<RegistryFriendlyByteBuf, SummonData> {
      public SyncHandler() {
      }

      @Override
      public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull SummonData data) {
         boolean initialSync = true;
         // The owner travels with the payload so the receiver can queue it under the right entity.
         UUID ownerId = data.owner instanceof net.minecraft.world.entity.Entity ownerEntity
                 ? ownerEntity.getUUID() : null;
         buf.writeBoolean(ownerId != null);
         if (ownerId != null) {
            buf.writeUUID(ownerId);
         }
         buf.writeVarInt(data.groups.size());

         for (Entry<SummonType<?>, List<Summon>> entityEntry : data.groups.entrySet()) {
            buf.writeResourceLocation(FSFRegisters.ATTACHMENT_ENTITY_TYPES.getKey(entityEntry.getKey()));
            List<Summon> list = entityEntry.getValue();
            buf.writeVarInt(list.size());

            for (Summon entity : list) {
               buf.writeUUID(entity.getUuid());
               entity.getSyncFieldDispatcher().encode(buf, entity.getLevel(), initialSync);
               buf.writeBoolean(entity.isClientInit());
               if (!entity.isClientInit()) {
                  entity.setClientInit(true);
                  FSFStreamCodecs.PATH_NODE.encode(buf, entity.getHistoryNodes().getFirst());
               }
            }
         }
      }

      @Override
      public @NotNull SummonData decode(@NotNull RegistryFriendlyByteBuf buf) {
         SummonData data = new SummonData();
         UUID ownerId = buf.readBoolean() ? buf.readUUID() : null;
         ByteBuf copy = buf.copy();
         byte[] payload = new byte[copy.readableBytes()];
         copy.readBytes(payload);
         if (payload.length == 0 || ownerId == null) {
            return data;
         } else {
            PENDING_PAYLOADS.compute(ownerId, (key, queued) -> {
               List<byte[]> updated = queued == null ? new ArrayList<>(1) : new ArrayList<>(queued);
               updated.add(payload);
               return updated;
            });
            return data;
         }
      }
   }
}

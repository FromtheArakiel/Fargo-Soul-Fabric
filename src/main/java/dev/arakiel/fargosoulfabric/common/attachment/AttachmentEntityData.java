package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulRegisters;
import dev.arakiel.fargosoulfabric.api.Attachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import dev.arakiel.fargosoulfabric.utils.FargoSoulStreamCodecs;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AttachmentEntityData implements Attachments.Bound {
   private final Map<AttachmentEntityType<?>, List<AttachmentEntity>> pendingAdd = new HashMap<>();
   private final Map<AttachmentEntityType<?>, List<AttachmentEntity>> groups = new HashMap<>();
   private final Map<UUID, AttachmentEntity> uuidToEntity = new HashMap<>();
   private final List<AttachmentEntity> renderCache = new ArrayList<>();
   private final AtomicReference<List<byte[]>> pendingPayloads = new AtomicReference<>(List.of());
   private Player owner;
   private Level level = null;
   private boolean changed = false;

   public AttachmentEntityData() {
      this.owner = null;
   }

   public AttachmentEntityData(AttachmentTarget owner) {
      if (owner instanceof Player player) {
         this.owner = player;
      } else {
         throw new IllegalArgumentException(owner + " is not a valid AttachmentEntityData");
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

      for (List<AttachmentEntity> list : this.groups.values()) {
         for (AttachmentEntity entity : list) {
            this.uuidToEntity.put(entity.getUuid(), entity);
         }
      }

      if (this.isClientSide()) {
         this.applyPendingSync();
         this.renderCache.clear();
         if (this.isRunning()) {
            for (List<AttachmentEntity> list : this.groups.values()) {
               for (AttachmentEntity entity : list) {
                  entity.tickCurrentPathNode();
                  this.renderCache.add(entity);
               }
            }
         }
      } else if (this.isRunning()) {
         boolean levelChange = this.level != null && this.level != this.owner.level();
         this.level = this.owner.level();
         Map<Long, List<Minion>> sameCache = new HashMap<>();

         for (List<AttachmentEntity> list : this.groups.values()) {
            for (AttachmentEntity entity : list) {
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
            for (Entry<AttachmentEntityType<?>, List<AttachmentEntity>> entry : this.pendingAdd.entrySet()) {
               List<AttachmentEntity> entities = this.groups.computeIfAbsent(entry.getKey(), key -> new ArrayList<>());

               for (AttachmentEntity attachmentEntity : entry.getValue()) {
                  if (!attachmentEntity.getCurrentPathNode().pos().equals(Vec3.ZERO)) {
                     attachmentEntity.setOwner(this.owner);
                     entities.add(attachmentEntity);
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
                  case Minion -> owner.getAttribute(FargoSoulAttributeRegister.MinionMaxCount);
                  case Sentry -> owner.getAttribute(FargoSoulAttributeRegister.SentryMaxCount);
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
            } while (!list.isEmpty() && !list.stream().noneMatch(AttachmentEntity::isRemove));

            return list.isEmpty();
         });
         if (!this.groups.isEmpty() || this.changed) {
            this.changed = false;
            Attachments.sync(this.owner, FargoSoulAttachmentRegister.ENTITY_DATA);
         }
      }
   }

   @Nullable
   public AttachmentEntity getEntity(UUID uuid) {
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

   public void add(AttachmentEntity entity) {
      this.pendingAdd.computeIfAbsent(entity.getType(), key -> new ArrayList<>()).add(entity);
   }

   @SuppressWarnings("unchecked")
   public <T extends AttachmentEntity> List<T> get(AttachmentEntityType<T> attachmentEntityType) {
      List<T> result = new ArrayList<>();
      for (AttachmentEntity entity : this.groups.getOrDefault(attachmentEntityType, new ArrayList<>())) {
         if (!entity.isRemove()) {
            result.add((T) entity);
         }
      }
      return result;
   }

   public <T extends AttachmentEntity> List<T> get(Class<T> classType) {
      List<T> result = new ArrayList<>();
      for (List<AttachmentEntity> list : this.groups.values()) {
         for (AttachmentEntity entity : list) {
            if (!entity.isRemove() && classType.isInstance(entity)) {
               result.add(classType.cast(entity));
            }
         }
      }
      return result;
   }

   public void remove(AttachmentEntityType<?> entityType) {
      this.getGroups().getOrDefault(entityType, new ArrayList<>()).forEach(AttachmentEntity::setRemove);
   }

   public Map<AttachmentEntityType<?>, List<AttachmentEntity>> getGroups() {
      return this.groups;
   }

   public List<AttachmentEntity> getRenderCache() {
      return this.renderCache;
   }

   private void applyPendingSync() {
      Level level = this.getLevel();

      for (byte[] payload : this.pendingPayloads.getAndSet(List.of())) {
         RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(payload), level.registryAccess());
         Map<UUID, AttachmentEntity> existing = new HashMap<>();
         this.groups.values().forEach(listx -> listx.forEach(entityx -> existing.put(entityx.getUuid(), entityx)));
         this.groups.clear();
         int typeCount = buf.readVarInt();

         for (int i = 0; i < typeCount; i++) {
            AttachmentEntityType<?> entityType =
                  FargoSoulRegisters.ATTACHMENT_ENTITY_TYPES.get(buf.readResourceLocation());
            if (entityType == null) {
               throw new IllegalStateException("Unknown summon type in a sync packet");
            }
            List<AttachmentEntity> list = this.groups.computeIfAbsent(entityType, kx -> new ArrayList<>());
            int listSize = buf.readVarInt();

            for (int k = 0; k < listSize; k++) {
               UUID uuid = buf.readUUID();
               AttachmentEntity entity = existing.get(uuid);
               if (entity == null) {
                  entity = (AttachmentEntity)entityType.factory().get();
                  entity.setUuid(uuid);
               }

               entity.setOwner(this.owner);
               entity.getSyncFieldDispatcher().decode(buf, level);
               if (!buf.readBoolean()) {
                  PathNode pathNode = FargoSoulStreamCodecs.PATH_NODE.decode(buf);
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
   public static final class SyncHandler implements StreamCodec<RegistryFriendlyByteBuf, AttachmentEntityData> {
      public SyncHandler() {
      }

      @Override
      public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull AttachmentEntityData data) {
         boolean initialSync = true;
         buf.writeVarInt(data.groups.size());

         for (Entry<AttachmentEntityType<?>, List<AttachmentEntity>> entityEntry : data.groups.entrySet()) {
            buf.writeResourceLocation(FargoSoulRegisters.ATTACHMENT_ENTITY_TYPES.getKey(entityEntry.getKey()));
            List<AttachmentEntity> list = entityEntry.getValue();
            buf.writeVarInt(list.size());

            for (AttachmentEntity entity : list) {
               buf.writeUUID(entity.getUuid());
               entity.getSyncFieldDispatcher().encode(buf, entity.getLevel(), initialSync);
               buf.writeBoolean(entity.isClientInit());
               if (!entity.isClientInit()) {
                  entity.setClientInit(true);
                  FargoSoulStreamCodecs.PATH_NODE.encode(buf, entity.getHistoryNodes().getFirst());
               }
            }
         }
      }

      @Override
      public @NotNull AttachmentEntityData decode(@NotNull RegistryFriendlyByteBuf buf) {
         AttachmentEntityData data = new AttachmentEntityData();
         ByteBuf copy = buf.copy();
         byte[] payload = new byte[copy.readableBytes()];
         copy.readBytes(payload);
         if (payload.length == 0) {
            return data;
         } else {
            data.pendingPayloads.updateAndGet(payloads -> {
               List<byte[]> updated = new ArrayList<>(payloads.size() + 1);
               updated.addAll(payloads);
               updated.add(payload);
               return updated;
            });
            return data;
         }
      }
   }
}
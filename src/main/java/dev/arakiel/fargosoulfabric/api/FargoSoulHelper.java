package dev.arakiel.fargosoulfabric.api;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachment.TargetCache;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public final class FargoSoulHelper {
   private final Player player;
   private final AttachmentEntityData attachmentEntityData;

   private FargoSoulHelper(Player player) {
      this.player = player;
      this.attachmentEntityData = Attachments.get(player, FargoSoulAttachmentRegister.ENTITY_DATA);
   }

   public static FargoSoulHelper get(Player player) {
      return new FargoSoulHelper(player);
   }

   public AttachmentEntityData getEntityData() {
      return this.attachmentEntityData;
   }

   /** The target cache of the owner, so the summons of the same owner share their lookups. */
   public TargetCache getTargetCache() {
      return Attachments.get(this.player, FargoSoulAttachmentRegister.TARGET_CACHE);
   }

   public void add(AttachmentEntity entity) {
      this.attachmentEntityData.add(entity);
   }

   public void remove(AttachmentEntityType<?> entityType) {
      this.attachmentEntityData
         .getGroups()
         .getOrDefault(entityType, List.of())
         .stream()
         .filter(entity -> entity instanceof Minion minion)
         .map(entity -> (Minion)entity)
         .forEach(AttachmentEntity::setRemove);
   }

   public boolean canSummon(MinionSlotType type, int slotCost) {
      return this.getMaxCount(type) - this.getUsedSlots(type) >= slotCost;
   }

   public int getMaxCount(MinionSlotType type) {
      return switch (type) {
         case Minion -> {
            AttributeInstance attributeInstance = this.player.getAttribute(FargoSoulAttributeRegister.MinionMaxCount);
            yield attributeInstance != null ? (int)attributeInstance.getValue() : 0;
         }
         case Sentry -> {
            AttributeInstance attributeInstance = this.player.getAttribute(FargoSoulAttributeRegister.SentryMaxCount);
            yield attributeInstance != null ? (int)attributeInstance.getValue() : 0;
         }
         case None -> 0;
      };
   }

   public int getUsedSlots(MinionSlotType type) {
      return this.attachmentEntityData
         .getGroups()
         .values()
         .stream()
         .flatMap(Collection::stream)
         .filter(entity -> entity instanceof Minion minion)
         .map(entity -> (Minion)entity)
         .filter(minion -> minion.getSlotType() == type && !minion.isRemove())
         .mapToInt(Minion::getSlotCost)
         .sum();
   }
}
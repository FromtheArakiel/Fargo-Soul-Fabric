package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.attachment.RecentTargets;
import dev.arakiel.fargosoulfabric.common.summon.Summon;
import dev.arakiel.fargosoulfabric.common.summon.SummonType;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.minion.MinionSlotType;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;

public final class FSFPlayerSummons {
   private final Player player;
   private final SummonData summonData;

   private FSFPlayerSummons(Player player) {
      this.player = player;
      this.summonData = Attachments.get(player, FSFAttachmentRegister.ENTITY_DATA);
   }

   public static FSFPlayerSummons get(Player player) {
      return new FSFPlayerSummons(player);
   }

   public SummonData getSummons() {
      return this.summonData;
   }

   /** The target cache of the owner, so the summons of the same owner share their lookups. */
   public RecentTargets getRecentTargets() {
      return Attachments.get(this.player, FSFAttachmentRegister.TARGET_CACHE);
   }

   public void add(Summon entity) {
      this.summonData.add(entity);
   }

   public void remove(SummonType<?> entityType) {
      this.summonData
         .getGroups()
         .getOrDefault(entityType, List.of())
         .stream()
         .filter(entity -> entity instanceof Minion minion)
         .map(entity -> (Minion)entity)
         .forEach(Summon::setRemove);
   }

   public boolean canSummon(MinionSlotType type, int slotCost) {
      return this.getMaxCount(type) - this.getUsedSlots(type) >= slotCost;
   }

   public int getMaxCount(MinionSlotType type) {
      return switch (type) {
         case Minion -> {
            AttributeInstance attributeInstance = this.player.getAttribute(FSFAttributeRegister.MinionMaxCount);
            yield attributeInstance != null ? (int)attributeInstance.getValue() : 0;
         }
         case Sentry -> {
            AttributeInstance attributeInstance = this.player.getAttribute(FSFAttributeRegister.SentryMaxCount);
            yield attributeInstance != null ? (int)attributeInstance.getValue() : 0;
         }
         case None -> 0;
      };
   }

   public int getUsedSlots(MinionSlotType type) {
      return this.summonData
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
package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.FSFHooks;
import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.attachment.DamageNumbers;
import dev.arakiel.fargosoulfabric.common.attachment.Immunities;
import dev.arakiel.fargosoulfabric.common.attachment.ParticleBatch;
import dev.arakiel.fargosoulfabric.common.attachment.SummonMarks;
import dev.arakiel.fargosoulfabric.common.attachment.RecentTargets;
import dev.arakiel.fargosoulfabric.common.summon.SummonDamageSource;
import dev.arakiel.fargosoulfabric.common.summonMark.SummonMarkInstance;
import dev.arakiel.fargosoulfabric.register.FSFItemRegistries;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

/** Everything the summon library reacts to. */
public final class FSFListeners {

    private FSFListeners() {
    }

    public static void init() {
        FSFHooks.ENTITY_TICK.register(FSFListeners::entityTick);
        FSFHooks.PLAYER_TICK.register(FSFListeners::playerTick);
        FSFHooks.WORLD_TICK.register(FSFListeners::worldTick);
        ServerTickEvents.END_WORLD_TICK.register(level -> FSFHooks.WORLD_TICK.invoker().afterWorldTick(level));
        FSFHooks.INCOMING_DAMAGE.register(FSFListeners::incomingDamage);
        FSFHooks.DAMAGE_POST.register(FSFListeners::damagePost);
        ServerLivingEntityEvents.AFTER_DEATH.register(FSFListeners::death);
        LootTableEvents.MODIFY.register(FSFListeners::modifyLootTable);
    }

    private static void entityTick(LivingEntity entity) {
        Attachments.get(entity, FSFAttachmentRegister.HEALTH_DATA).tick();
        Attachments.get(entity, FSFAttachmentRegister.IMMUNITY_DATA).tick();
    }

    private static void playerTick(Player player) {
        if (!player.level().isClientSide()) {
            Attachments.get(player, FSFAttachmentRegister.TARGET_CACHE).tick();
            Attachments.get(player, FSFAttachmentRegister.SUMMON_MARK_DATA).tick();
        }
        Attachments.get(player, FSFAttachmentRegister.ENTITY_DATA).tick();
    }

    private static void worldTick(ServerLevel level) {
        ParticleBatch.tick(level);
        DamageNumbers.tick(level);
    }

    /** Lets the summon marks change the damage their owner deals. */
    private static void incomingDamage(DamageContainer container) {
        LivingEntity target = container.getEntity();
        if (target.level().isClientSide()
                || !(container.getSource() instanceof SummonDamageSource damageSource)
                || !(damageSource.getEntity() instanceof Player attacker)
                || target == attacker) {
            return;
        }
        SummonMarks tracker = Attachments.get(attacker, FSFAttachmentRegister.SUMMON_MARK_DATA);
        if (tracker.isTarget(target)) {
            for (SummonMarkInstance instance : tracker.getSummonMarkInstances()) {
                container.setNewDamage(instance.getType()
                        .damagePre(tracker, instance, target, damageSource, container.getNewDamage()));
            }
        }
    }

    /** Records who hit whom and lets the summon marks react to the damage. */
    private static void damagePost(LivingEntity target, DamageSource source, float amount) {
        if (!(target.level() instanceof ServerLevel)) {
            return;
        }
        DamageNumbers.handler(target, source, amount);
        if (target instanceof Player player && source.getEntity() instanceof LivingEntity attacker
                && target != attacker) {
            Attachments.get(player, FSFAttachmentRegister.TARGET_CACHE).record(attacker, 200);
        }
        if (source.getEntity() instanceof Player player && target != player) {
            Attachments.get(player, FSFAttachmentRegister.TARGET_CACHE).record(target, 200);
        }
        if (source instanceof SummonDamageSource damageSource) {
            Player owner = damageSource.getOwner();
            SummonMarks tracker = Attachments.get(owner, FSFAttachmentRegister.SUMMON_MARK_DATA);
            if (tracker.isTarget(target) && source.getEntity() instanceof LivingEntity attacker && target != attacker) {
                for (SummonMarkInstance instance : tracker.getSummonMarkInstances()) {
                    instance.getType().damagePost(tracker, instance, target, damageSource, amount);
                }
            }
        }
    }

    private static void death(LivingEntity target, DamageSource source) {
        if (!(target.level() instanceof ServerLevel)
                || !(source instanceof SummonDamageSource damageSource)) {
            return;
        }
        Player owner = damageSource.getOwner();
        SummonMarks tracker = Attachments.get(owner, FSFAttachmentRegister.SUMMON_MARK_DATA);
        if (tracker.isTarget(target) && source.getEntity() instanceof LivingEntity attacker && target != attacker) {
            for (SummonMarkInstance instance : tracker.getSummonMarkInstances()) {
                instance.getType().kill(tracker, instance, target, damageSource);
            }
        }
    }

    /** Adds the loot the items declared. */
    private static void modifyLootTable(ResourceKey<LootTable> key, LootTable.Builder builder,
                                        net.fabricmc.fabric.api.loot.v3.LootTableSource source,
                                        net.minecraft.core.HolderLookup.Provider lookup) {
        for (FSFItemRegistries registries : FSFItemRegistries.REGISTRIES.values()) {
            List<Function<LootTable.Builder, LootPool.Builder>> pools = registries.lootTableData.get(key.location());
            if (pools == null) {
                continue;
            }
            for (Function<LootTable.Builder, LootPool.Builder> pool : new ArrayList<>(pools)) {
                builder.withPool(pool.apply(builder));
            }
        }
    }

    /** Registers the summon data ticking and the loot injection; called by the mod initializer. */
}

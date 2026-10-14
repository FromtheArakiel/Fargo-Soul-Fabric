package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachment.DamageInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.ImmunityData;
import dev.arakiel.fargosoulfabric.common.attachment.ParticlesData;
import dev.arakiel.fargosoulfabric.common.attachment.SummonMarkTracker;
import dev.arakiel.fargosoulfabric.common.attachment.TargetCache;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityDamageSource;
import dev.arakiel.fargosoulfabric.common.summonMark.SummonMarkInstance;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegistries;
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
public final class FargoSoulListeners {

    private FargoSoulListeners() {
    }

    public static void init() {
        FargoSoulHooks.ENTITY_TICK.register(FargoSoulListeners::entityTick);
        FargoSoulHooks.PLAYER_TICK.register(FargoSoulListeners::playerTick);
        FargoSoulHooks.WORLD_TICK.register(FargoSoulListeners::worldTick);
        ServerTickEvents.END_WORLD_TICK.register(level -> FargoSoulHooks.WORLD_TICK.invoker().afterWorldTick(level));
        FargoSoulHooks.INCOMING_DAMAGE.register(FargoSoulListeners::incomingDamage);
        FargoSoulHooks.DAMAGE_POST.register(FargoSoulListeners::damagePost);
        ServerLivingEntityEvents.AFTER_DEATH.register(FargoSoulListeners::death);
        LootTableEvents.MODIFY.register(FargoSoulListeners::modifyLootTable);
    }

    private static void entityTick(LivingEntity entity) {
        Attachments.get(entity, FargoSoulAttachmentRegister.HEALTH_DATA).tick();
        Attachments.get(entity, FargoSoulAttachmentRegister.IMMUNITY_DATA).tick();
    }

    private static void playerTick(Player player) {
        if (!player.level().isClientSide()) {
            Attachments.get(player, FargoSoulAttachmentRegister.TARGET_CACHE).tick();
            Attachments.get(player, FargoSoulAttachmentRegister.SUMMON_MARK_DATA).tick();
        }
        Attachments.get(player, FargoSoulAttachmentRegister.ENTITY_DATA).tick();
    }

    private static void worldTick(ServerLevel level) {
        ParticlesData.tick(level);
        DamageInfoData.tick(level);
    }

    /** Lets the summon marks change the damage their owner deals. */
    private static void incomingDamage(DamageContainer container) {
        LivingEntity target = container.getEntity();
        if (target.level().isClientSide()
                || !(container.getSource() instanceof AttachmentEntityDamageSource damageSource)
                || !(damageSource.getEntity() instanceof Player attacker)
                || target == attacker) {
            return;
        }
        SummonMarkTracker tracker = Attachments.get(attacker, FargoSoulAttachmentRegister.SUMMON_MARK_DATA);
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
        DamageInfoData.handler(target, source, amount);
        if (target instanceof Player player && source.getEntity() instanceof LivingEntity attacker
                && target != attacker) {
            Attachments.get(player, FargoSoulAttachmentRegister.TARGET_CACHE).record(attacker, 200);
        }
        if (source.getEntity() instanceof Player player && target != player) {
            Attachments.get(player, FargoSoulAttachmentRegister.TARGET_CACHE).record(target, 200);
        }
        if (source instanceof AttachmentEntityDamageSource damageSource) {
            Player owner = damageSource.getOwner();
            SummonMarkTracker tracker = Attachments.get(owner, FargoSoulAttachmentRegister.SUMMON_MARK_DATA);
            if (tracker.isTarget(target) && source.getEntity() instanceof LivingEntity attacker && target != attacker) {
                for (SummonMarkInstance instance : tracker.getSummonMarkInstances()) {
                    instance.getType().damagePost(tracker, instance, target, damageSource, amount);
                }
            }
        }
    }

    private static void death(LivingEntity target, DamageSource source) {
        if (!(target.level() instanceof ServerLevel)
                || !(source instanceof AttachmentEntityDamageSource damageSource)) {
            return;
        }
        Player owner = damageSource.getOwner();
        SummonMarkTracker tracker = Attachments.get(owner, FargoSoulAttachmentRegister.SUMMON_MARK_DATA);
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
        for (FargoSoulItemRegistries registries : FargoSoulItemRegistries.REGISTRIES.values()) {
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
    public static void initAttachments() {
        AttachmentEntityData.class.getName();
    }
}
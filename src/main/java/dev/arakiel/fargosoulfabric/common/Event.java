package dev.arakiel.fargosoulfabric.common;

import dev.arakiel.fargosoulfabric.api.Attachments;
import dev.arakiel.curios.api.event.CurioChangeEvent;
import dev.arakiel.curios.api.event.CuriosEventBus;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargetCache;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower.StardustSoul;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** Everything the souls react to. */
public final class Event {

    private Event() {
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(StardustSoul::serverTick);
        // The souls a player carries are cached. The cache has to be built when the player joins and
        // after a respawn as well, not only when a curio changes.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Player player = handler.getPlayer();
            Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA).update(player);
        });
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) ->
                Attachments.get(newPlayer, FargoSoulAttachmentRegister.SOUL_ITEM_DATA).update(newPlayer));

        FargoSoulHooks.ENTITY_TICK.register(entity -> {
            if (entity instanceof Player player && !player.level().isClientSide()) {
                Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA).tick(player);
                SoulTargetCache.get(player).tick(player);
                SoulItemData.forEach(player, soulItem -> soulItem.tick(player));
            }
        });

        CuriosEventBus.register(CurioChangeEvent.class, event -> {
            if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
                Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA).update(player);
            }
        });

        FargoSoulHooks.INCOMING_DAMAGE.register(Event::incomingDamage);
        FargoSoulHooks.CRITICAL_HIT.register(Event::criticalHit);
        FargoSoulHooks.ITEM_PICKUP.register(Event::pickup);
        FargoSoulHooks.HEAL.register(Event::heal);
        FargoSoulHooks.SHIELD_BLOCK.register(Event::shieldBlock);
        FargoSoulHooks.MOB_EFFECT_APPLICABLE.register(Event::effectApplicable);

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> allowDeath(entity, source));
        ServerLivingEntityEvents.AFTER_DEATH.register(Event::afterDeath);
    }

    /** Attack and hurt modifiers, plus the extra fire damage of the oil effect. */
    private static void incomingDamage(DamageContainer container) {
        LivingEntity target = container.getEntity();
        Entity attacker = container.getSource().getEntity();
        if (target.level().isClientSide()) {
            return;
        }
        if (target.hasEffect(FargoSoulMobEffectRegister.Oiled)
                && container.getSource().is(DamageTypeTags.IS_FIRE)) {
            container.setNewDamage(container.getNewDamage() * 3.0F);
        }
        List<ValueModifier> modifiers = new ArrayList<>();
        if (attacker instanceof Player player) {
            SoulItemData.forEach(player, soulItem -> soulItem.attack(player, target, container, modifiers));
        }
        if (target instanceof Player player) {
            SoulItemData.forEach(player, soulItem -> soulItem.hurt(attacker, player, container, modifiers));
        }
        float damage = ValueModifier.getModifierAfter(container.getNewDamage(), modifiers);
        container.setNewDamage(damage);
        if (damage <= 0.0F) {
            container.setCanceled(true);
        }
    }

    private static void criticalHit(Player player, Entity target, boolean critical, FargoSoulHooks.Value multiplier) {
        if (!player.level().isClientSide() && critical && target instanceof LivingEntity living) {
            List<ValueModifier> modifiers = new ArrayList<>();
            SoulItemData.forEach(player, soulItem -> soulItem.criticalHit(player, living, modifiers));
            multiplier.set(ValueModifier.getModifierAfter(multiplier.get(), modifiers));
        }
    }

    private static void pickup(Player player, net.minecraft.world.entity.item.ItemEntity itemEntity) {
        if (!player.level().isClientSide()) {
            SoulItemData.forEach(player, soulItem -> soulItem.pickup(player, itemEntity.getItem()));
        }
    }

    /** Bleeding and hemorrhage reduce the healing the player receives. */
    private static void heal(LivingEntity entity, FargoSoulHooks.Value amount) {
        if (entity.level().isClientSide()) {
            return;
        }
        float base = amount.get();
        if (entity instanceof Player player) {
            List<ValueModifier> modifiers = new ArrayList<>();
            SoulItemData.forEach(player, soulItem -> soulItem.healAmount(player, base, modifiers));
            amount.set(ValueModifier.getModifierAfter(base, modifiers));
        }
        if (entity.hasEffect(FargoSoulMobEffectRegister.Hemorrhage)) {
            amount.multiply(0.3F);
        }
        if (entity.hasEffect(FargoSoulMobEffectRegister.Bleeding)) {
            amount.multiply(0.5F);
        }
    }

    private static void shieldBlock(Player player, net.minecraft.world.damagesource.DamageSource source,
                                    float blockedDamage) {
        if (!player.level().isClientSide()) {
            SoulItemData.forEach(player, soulItem -> soulItem.shieldBlock(player, source, blockedDamage));
        }
    }

    private static void effectApplicable(LivingEntity entity, net.minecraft.world.effect.MobEffectInstance effect,
                                         FargoSoulHooks.Value canceled) {
        if (entity instanceof Player player && !player.level().isClientSide()) {
            SoulItemData.forEach(player, soulItem -> {
                if (!soulItem.effectApplicable(player, effect)) {
                    canceled.cancel();
                }
            });
        }
    }

    /** Returning false keeps the entity alive. */
    private static boolean allowDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
        if (!(entity instanceof Player player) || player.level().isClientSide()) {
            return true;
        }
        FargoSoulHooks.Value canceled = new FargoSoulHooks.Value(0.0F);
        FargoSoulHooks.DEATH.invoker().onDeath(player, source, canceled);
        boolean[] alive = {!canceled.isCanceled()};
        SoulItemData.forEach(player, soulItem -> {
            if (!soulItem.death(player, source, false)) {
                alive[0] = false;
            }
        });
        return alive[0];
    }

    private static void afterDeath(LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
        if (source.getEntity() instanceof Player player && !player.level().isClientSide()) {
            SoulItemData.forEach(player, soulItem -> soulItem.kill(player, entity, source));
        }
    }

    /** The attribute the player uses to pierce armor, added by the attribute patch. */
    public static net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> armorPierce() {
        return FargoSoulAttributeRegister.ArmorPierce;
    }
}

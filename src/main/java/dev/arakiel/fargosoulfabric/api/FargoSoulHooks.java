package dev.arakiel.fargosoulfabric.api;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

/**
 * The hooks this mod needs and Fabric API does not offer.
 *
 * <p>They follow the usual Fabric shape: an {@link Event} that the mixins of this mod fire, and that
 * the gameplay code subscribes to. Client sided hooks only ever fire on the client.
 */
public final class FargoSoulHooks {

    /** A value a listener may change, optionally flagging it as cancelled. */
    public static final class Value {

        private float value;

        private boolean canceled;

        public Value(float value) {
            this.value = value;
        }

        public float get() {
            return this.value;
        }

        public void set(float value) {
            this.value = value;
        }

        public void add(float delta) {
            this.value += delta;
        }

        public void multiply(float factor) {
            this.value *= factor;
        }

        public boolean isCanceled() {
            return this.canceled;
        }

        public void cancel() {
            this.canceled = true;
        }
    }

    /** Fired right before a living entity takes damage. */
    @FunctionalInterface
    public interface IncomingDamage {
        void onIncomingDamage(DamageContainer container);
    }

    /** Fired after a living entity took damage. */
    @FunctionalInterface
    public interface DamagePost {
        void afterDamage(LivingEntity entity, DamageSource source, float amount);
    }

    /** Fired when a living entity heals. */
    @FunctionalInterface
    public interface Heal {
        void onHeal(LivingEntity entity, Value amount);
    }

    /** Fired when a shield absorbs part of a hit. */
    @FunctionalInterface
    public interface ShieldBlock {
        void onShieldBlock(Player player, DamageSource source, float blockedDamage);
    }

    /** Fired right before a living entity dies. */
    @FunctionalInterface
    public interface Death {
        void onDeath(LivingEntity entity, DamageSource source, Value canceled);
    }

    /** Fired when a mob effect is about to be applied. */
    @FunctionalInterface
    public interface MobEffectApplicable {
        void onEffectApplicable(LivingEntity entity, MobEffectInstance effect, Value canceled);
    }

    /** Fired when a player lands a critical hit. */
    @FunctionalInterface
    public interface CriticalHit {
        void onCriticalHit(Player player, Entity target, boolean critical, Value multiplier);
    }

    /** Fired after a player picked an item entity up. */
    @FunctionalInterface
    public interface ItemPickup {
        void afterPickup(Player player, ItemEntity itemEntity);
    }

    /** Fired after a living entity ticked on the logical server. */
    @FunctionalInterface
    public interface EntityTick {
        void afterEntityTick(LivingEntity entity);
    }

    /** Fired after a player ticked, on both sides. */
    @FunctionalInterface
    public interface PlayerTick {
        void afterPlayerTick(Player player);
    }

    /** Fired after a level ticked on the logical server. */
    @FunctionalInterface
    public interface WorldTick {
        void afterWorldTick(ServerLevel level);
    }

    /** Fired when a key is pressed on the client. */
    @FunctionalInterface
    public interface KeyInput {
        void onKeyInput(Player player, SoulKey key);
    }

    /** Fired while the client draws the fire overlay over the screen. */
    @FunctionalInterface
    public interface FireOverlay {
        void onFireOverlay(Player player, Value cancel);
    }

    public static final Event<IncomingDamage> INCOMING_DAMAGE = arrayBacked(IncomingDamage.class,
            listeners -> container -> {
                for (IncomingDamage listener : listeners) {
                    listener.onIncomingDamage(container);
                }
            });

    public static final Event<DamagePost> DAMAGE_POST = arrayBacked(DamagePost.class,
            listeners -> (entity, source, amount) -> {
                for (DamagePost listener : listeners) {
                    listener.afterDamage(entity, source, amount);
                }
            });

    public static final Event<Heal> HEAL = arrayBacked(Heal.class,
            listeners -> (entity, amount) -> {
                for (Heal listener : listeners) {
                    listener.onHeal(entity, amount);
                }
            });

    public static final Event<ShieldBlock> SHIELD_BLOCK = arrayBacked(ShieldBlock.class,
            listeners -> (player, source, amount) -> {
                for (ShieldBlock listener : listeners) {
                    listener.onShieldBlock(player, source, amount);
                }
            });

    public static final Event<Death> DEATH = arrayBacked(Death.class,
            listeners -> (entity, source, canceled) -> {
                for (Death listener : listeners) {
                    listener.onDeath(entity, source, canceled);
                }
            });

    public static final Event<MobEffectApplicable> MOB_EFFECT_APPLICABLE = arrayBacked(MobEffectApplicable.class,
            listeners -> (entity, effect, canceled) -> {
                for (MobEffectApplicable listener : listeners) {
                    listener.onEffectApplicable(entity, effect, canceled);
                }
            });

    public static final Event<CriticalHit> CRITICAL_HIT = arrayBacked(CriticalHit.class,
            listeners -> (player, target, critical, multiplier) -> {
                for (CriticalHit listener : listeners) {
                    listener.onCriticalHit(player, target, critical, multiplier);
                }
            });

    public static final Event<ItemPickup> ITEM_PICKUP = arrayBacked(ItemPickup.class,
            listeners -> (player, itemEntity) -> {
                for (ItemPickup listener : listeners) {
                    listener.afterPickup(player, itemEntity);
                }
            });

    public static final Event<EntityTick> ENTITY_TICK = arrayBacked(EntityTick.class,
            listeners -> entity -> {
                for (EntityTick listener : listeners) {
                    listener.afterEntityTick(entity);
                }
            });

    public static final Event<PlayerTick> PLAYER_TICK = arrayBacked(PlayerTick.class,
            listeners -> player -> {
                for (PlayerTick listener : listeners) {
                    listener.afterPlayerTick(player);
                }
            });

    public static final Event<WorldTick> WORLD_TICK = arrayBacked(WorldTick.class,
            listeners -> level -> {
                for (WorldTick listener : listeners) {
                    listener.afterWorldTick(level);
                }
            });

    public static final Event<KeyInput> KEY_INPUT = arrayBacked(KeyInput.class,
            listeners -> (player, key) -> {
                for (KeyInput listener : listeners) {
                    listener.onKeyInput(player, key);
                }
            });

    public static final Event<FireOverlay> FIRE_OVERLAY = arrayBacked(FireOverlay.class,
            listeners -> (player, cancel) -> {
                for (FireOverlay listener : listeners) {
                    listener.onFireOverlay(player, cancel);
                }
            });

    private FargoSoulHooks() {
    }

    private static <T> Event<T> arrayBacked(Class<T> type, java.util.function.Function<T[], T> factory) {
        return EventFactory.createArrayBacked(type, factory);
    }
}

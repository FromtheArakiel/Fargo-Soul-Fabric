package dev.arakiel.fargosoulfabric.mixin;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.InvincibleData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SummonMarkTracker;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityDamageSource;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import dev.arakiel.fargosoulfabric.common.projectile.Projectile;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Fires the hooks the summon system needs and scales the damage the summons deal. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow
    protected int lastHurtByPlayerTime;

    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    @Shadow
    protected abstract boolean isAlwaysExperienceDropper();

    @Shadow
    public abstract boolean wasExperienceConsumed();

    @Shadow
    public abstract int getExperienceReward(ServerLevel level, @Nullable Entity killer);

    @Unique
    private DamageSource fargoSoul$currentDamageSource;

    @WrapMethod(method = "hurt")
    private boolean fargoSoul$hurt(DamageSource source, float amount, Operation<Boolean> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()) {
            return original.call(source, amount);
        }
        if (source instanceof AttachmentEntityDamageSource summonSource) {
            Player owner = summonSource.getOwner();
            AttributeInstance damage = owner.getAttribute(FargoSoulAttributeRegister.SummonDamage);
            amount *= damage != null ? (float) damage.getValue() : 1.0F;
            amount *= 0.85F + owner.getRandom().nextFloat() * 0.3F;
            SummonMarkTracker tracker = Attachments.get(owner, FargoSoulAttachmentRegister.SUMMON_MARK_DATA);
            if (tracker.isTarget(self)) {
                amount = tracker.getDamageModifier(summonSource, amount);
            }
        }
        DamageContainer container = new DamageContainer(self, source, amount);
        FargoSoulHooks.INCOMING_DAMAGE.invoker().onIncomingDamage(container);
        if (container.isCanceled()) {
            return false;
        }
        float damage = container.getReducedDamage();
        this.fargoSoul$currentDamageSource = source;
        boolean hurt;
        try {
            hurt = original.call(source, damage);
        } finally {
            this.fargoSoul$currentDamageSource = null;
        }
        if (hurt) {
            FargoSoulHooks.DAMAGE_POST.invoker().afterDamage(self, source, damage);
        }
        return hurt;
    }

    @WrapMethod(method = "heal")
    private void fargoSoul$heal(float amount, Operation<Void> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()) {
            original.call(amount);
            return;
        }
        FargoSoulHooks.Value value = new FargoSoulHooks.Value(amount);
        FargoSoulHooks.HEAL.invoker().onHeal(self, value);
        original.call(value.get());
    }

    @WrapMethod(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z")
    private boolean fargoSoul$addEffect(MobEffectInstance effect, @Nullable Entity source, Operation<Boolean> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.level().isClientSide()) {
            FargoSoulHooks.Value canceled = new FargoSoulHooks.Value(0.0F);
            FargoSoulHooks.MOB_EFFECT_APPLICABLE.invoker().onEffectApplicable(self, effect, canceled);
            if (canceled.isCanceled()) {
                return false;
            }
        }
        return original.call(effect, source);
    }

    @WrapMethod(method = "hurtCurrentlyUsedShield")
    private void fargoSoul$hurtCurrentlyUsedShield(float amount, Operation<Void> original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && !player.level().isClientSide() && this.fargoSoul$currentDamageSource != null) {
            FargoSoulHooks.SHIELD_BLOCK.invoker().onShieldBlock(player, this.fargoSoul$currentDamageSource, amount);
        }
        original.call(amount);
    }

    @Inject(method = "take", at = @At("TAIL"))
    private void fargoSoul$take(Entity entity, int amount, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof Player player && !player.level().isClientSide() && entity instanceof ItemEntity itemEntity) {
            FargoSoulHooks.ITEM_PICKUP.invoker().afterPickup(player, itemEntity);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void fargoSoul$tick(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        // The soul state runs on both sides: the server owns the cooldowns and the states that are sent
        // to the clients, the client owns the infos that only exist there.
        SoulInfoData.tick(self);
        if (self.level().isClientSide()) {
            return;
        }
        Attachments.get(self, FargoSoulAttachmentRegister.INVINCIBLE_DATA).tick();
        FargoSoulHooks.ENTITY_TICK.invoker().afterEntityTick(self);
    }

    @ModifyArg(method = "hurt", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"), index = 0)
    private double fargoSoul$knockback(double strength, @Local(argsOnly = true) DamageSource source) {
        if (source instanceof AttachmentEntityDamageSource summonSource
                && summonSource.getAttachmentEntity() instanceof Minion minion) {
            LivingEntity owner = minion.getOwner();
            AttributeInstance instance = owner != null ? owner.getAttribute(FargoSoulAttributeRegister.SummonKnockback) : null;
            double scale = instance != null ? instance.getValue() : 1.0D;
            if (owner != null) {
                scale *= 0.8D + 0.4D * owner.getRandom().nextDouble();
            }
            return minion.getKnockback() * scale;
        }
        return strength;
    }

    @WrapWithCondition(method = "dropAllDeathLoot", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;dropExperience(Lnet/minecraft/world/entity/Entity;)V"))
    private boolean fargoSoul$dropExperience(LivingEntity instance, Entity killer,
                                             @Local(argsOnly = true) DamageSource source) {
        if (!(source instanceof AttachmentEntityDamageSource summonSource)) {
            return true;
        }
        Player owner = null;
        if (summonSource.getAttachmentEntity() instanceof Minion minion && minion.getOwner() instanceof Player player) {
            owner = player;
        } else if (summonSource.getAttachmentEntity() instanceof Projectile projectile
                && projectile.getOwner() instanceof Player player) {
            owner = player;
        }
        if (owner == null || !(instance.level() instanceof ServerLevel level) || this.wasExperienceConsumed()) {
            return true;
        }
        boolean alwaysDrops = this.isAlwaysExperienceDropper();
        boolean mobDrops = this.lastHurtByPlayerTime > 0 && instance.shouldDropExperience()
                && level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        if (!alwaysDrops && !mobDrops) {
            return true;
        }
        int reward = this.getExperienceReward(level, killer);
        Vec3 pos = instance.position();
        int takeXpDelay = owner.takeXpDelay;
        List<ExperienceOrb> orbs = new ArrayList<>();
        while (reward > 0) {
            int value = ExperienceOrb.getExperienceValue(reward);
            reward -= value;
            if (!ExperienceOrbAccessor.callTryMergeToExisting(level, pos, value)) {
                ExperienceOrb orb = new ExperienceOrb(level, owner.getX(), owner.getY(), owner.getZ(), value);
                owner.takeXpDelay = 0;
                orb.playerTouch(owner);
                owner.takeXpDelay = takeXpDelay;
                orbs.add(orb);
            }
        }
        return false;
    }
}
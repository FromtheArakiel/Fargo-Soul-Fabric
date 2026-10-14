package dev.arakiel.fargosoulfabric.common.item.terraSoul.spiritPower;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.SoulKey;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.entity.Vortex;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForbiddenSoul extends SoulItem {

    public ForbiddenSoul(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable ResourceLocation keyAction(Player player, SoulKey key) {
        if (key == SoulKey.Forbidden) {
            return FSF.rl("forbidden_storm");
        }
        return super.keyAction(player, key);
    }

    @Override
    public void handleKeyAction(Player player, ResourceLocation location) {
        if (location.equals(FSF.rl("forbidden_storm"))) {
            Info info = SoulStates.getSoulState(player, SoulStateRegister.FORBIDDEN_SOUL_INFO);
            if (info == null) {
                info = new Info();
                SoulStates.putSoulState(player, info);
            }
            if (info.cooldown <= 0) {
                info.cooldown = 600;
                Vec3 eye = player.getEyePosition();
                Vec3 end = eye.add(player.getLookAngle().scale(64));
                BlockHitResult hit = player.level().clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
                Vec3 pos = hit.getType() == HitResult.Type.MISS ? eye.add(player.getLookAngle().scale(10)) : hit.getLocation().add(hit.getDirection().getStepX(), hit.getDirection().getStepY(), hit.getDirection().getStepZ());
                Vortex vortex = new Vortex();
                vortex.setDamageSourceSupplier(owner -> owner.damageSources().magic());
                vortex.setPos(pos);
                vortex.setDamage(0);
                vortex.join(player);
                // 风暴: the storm rips open where the vortex lands.
                FSFEffects.burst(player.level(), pos, ParticleTypes.CLOUD, 40, 1.0D, 0.15D);
                FSFEffects.burst(player.level(), pos, ParticleTypes.ELECTRIC_SPARK, 30, 0.6D, 0.5D);
            }
        }
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.FORBIDDEN_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.ForbiddenSoulItem)) {
                if (cooldown > 0) {
                    cooldown--;
                }
            } else {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putInt("cooldown", cooldown);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            cooldown = tag.getInt("cooldown");
        }
    }
}

package dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

import dev.arakiel.fargosoulfabric.common.SoulKey;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.entity.Vortex;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import dev.arakiel.fargosoulfabric.common.sound.Playable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class VortexSoul extends SoulItem {

    public VortexSoul(Properties properties) {
        super(properties);
    }

    @Override
    public ResourceLocation keyAction(Player player, SoulKey key) {
        if (key == SoulKey.Vortex) {
            return FSF.rl("vortex_soul");
        }
        return null;
    }

    @Override
    public void handleKeyAction(Player player, ResourceLocation key) {
        if (key.equals(FSF.rl("vortex_soul"))) {
            Info info = SoulStates.getSoulState(player, SoulStateRegister.VORTEX_SOUL_INFO);
            if (info == null) {
                info = new Info();
                info.cooldown = 20;
                SoulStates.putSoulState(player, info);
                Level level = player.level();
                ClipContext clipContext = new ClipContext(player.getEyePosition(), player.getEyePosition().add(player.getLookAngle().scale(256)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
                HitResult hitResult = player.level().clip(clipContext);
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    BlockPos pos = blockHitResult.getBlockPos();
                    if (!level.getBlockState(pos).is(Blocks.AIR) && pos.getY() > level.getMinBuildHeight()) {
                        Direction hitFace = blockHitResult.getDirection();
                        double adjustX = pos.getX() + hitFace.getStepX();
                        double adjustY = pos.getY() + hitFace.getStepY();
                        double adjustZ = pos.getZ() + hitFace.getStepZ();
                        player.teleportTo(adjustX, adjustY, adjustZ);
                        Playable.play(SoundEvents.ENDERMAN_TELEPORT, level, player.position(), player.getSoundSource());
                        // 传送: leave a swirl where the player came out.
                        FSFEffects.burst(level, new Vec3(adjustX, adjustY + 0.5D, adjustZ),
                                ParticleTypes.PORTAL, 60, 0.5D, 0.4D);
                        Vortex vortex = new Vortex();
                        vortex.setDamageSourceSupplier(owner -> owner.damageSources().playerAttack(owner));
                        vortex.setPos(new Vec3(adjustX, adjustY, adjustZ));
                        vortex.setDamage(0.5f);
                        vortex.join(player);
                    }
                }
            }
        }
    }

    public static final class Info extends SoulState {

        public int cooldown = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.VORTEX_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (--cooldown <= 0) {
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

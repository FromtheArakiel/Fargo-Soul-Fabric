package dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower;

import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.entity.Vortex;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
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
    public ResourceLocation keyPressed(Player player, SoulKey key) {
        if (key == SoulKey.Vortex) {
            return FargoSoul.rl("vortex_soul");
        }
        return null;
    }

    @Override
    public void keyHandle(Player player, ResourceLocation key) {
        if (key.equals(FargoSoul.rl("vortex_soul"))) {
            Info info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.VORTEX_SOUL_INFO);
            if (info == null) {
                info = new Info();
                info.cooldown = 20;
                SoulInfoData.putSoulInfo(player, info);
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

    public static final class Info extends SoulInfo {

        public int cooldown = 0;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.VORTEX_SOUL_INFO;
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

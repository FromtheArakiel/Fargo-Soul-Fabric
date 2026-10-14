package dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulInfoType;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StardustSoul extends SoulItem {

    private static int frozenRemaining = -1;

    public StardustSoul(Properties properties) {
        super(properties);
    }

    @Override
    public ResourceLocation keyPressed(Player player, SoulKey key) {
        if (key == SoulKey.Stardust) {
            return FargoSoul.rl("stardust_soul");
        }
        return super.keyPressed(player, key);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (attacker.getServer() instanceof MinecraftServer server && server.tickRateManager().isFrozen()) {
            modifiers.add(new ValueModifier(1, ValueOperation.ADD_VALUE));
        }
    }

    @Override
    public void keyHandle(Player player, ResourceLocation key) {
        if (key.equals(FargoSoul.rl("stardust_soul")) && player.getServer() instanceof MinecraftServer server) {
            ServerTickRateManager serverTickRateManager = server.tickRateManager();
            if (!serverTickRateManager.isFrozen()) {
                Info soulInfo = new Info();
                soulInfo.cooldown = 120 * 20;
                SoulInfoData.putSoulInfo(player, soulInfo);
                serverTickRateManager.setFrozen(true);
                frozenRemaining = 6 * 20;
            }
        }
    }

    public static void serverTick(MinecraftServer server) {
        if (--frozenRemaining == 0) {
            server.tickRateManager().setFrozen(false);
        }
    }

    public static final class Info extends SoulInfo {

        public int cooldown = 0;

        @Override
        public SoulInfoType<? extends SoulInfo> getType() {
            return FargoSoulSoulInfoRegister.STARDUST_SOUL_INFO;
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

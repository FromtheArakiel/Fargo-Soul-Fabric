package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulInfo.PenetratingSoulInfo;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PenetratingNinjaSoul extends SoulItem {

    public PenetratingNinjaSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canSprint(Player player) {
        return true;
    }

    @Override
    public void sprintClient(Player player, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(0.25f, ValueOperation.ADD_MULTIPLIED_BASE));
        PenetratingSoulInfo info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.PENETRATING_SOUL_INFO);
        if (info != null && info.pending) {
            info.pending = false;
            info.penetrate = 20;
        }
    }

    @Override
    public void sprintServer(Player player) {
        PenetratingSoulInfo info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.PENETRATING_SOUL_INFO);
        if (info != null && info.pending) {
            info.pending = false;
            info.penetrate = 20;
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        PenetratingSoulInfo info = SoulInfoData.getSoulInfo(target, FargoSoulSoulInfoRegister.PENETRATING_SOUL_INFO);
        if (info != null && info.penetrate > 0) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public @Nullable ResourceLocation keyPressed(Player player, SoulKey key) {
        if (key == SoulKey.PenetratingNinja) {
            PenetratingSoulInfo info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.PENETRATING_SOUL_INFO);
            if (info == null) {
                info = new PenetratingSoulInfo();
                SoulInfoData.putSoulInfo(player, info);
            }
            if (info.cooldown <= 0) {
                info.pending = true;
                info.cooldown = 600;
            }
            return FargoSoul.rl("penetrating_sprint");
        }
        return super.keyPressed(player, key);
    }

    @Override
    public void keyHandle(Player player, ResourceLocation location) {
        if (location.equals(FargoSoul.rl("penetrating_sprint"))) {
            PenetratingSoulInfo info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.PENETRATING_SOUL_INFO);
            if (info == null) {
                info = new PenetratingSoulInfo();
                SoulInfoData.putSoulInfo(player, info);
            }
            if (info.cooldown <= 0) {
                info.pending = true;
                info.cooldown = 600;
            }
        }
    }
}

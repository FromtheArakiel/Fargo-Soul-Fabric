package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

import dev.arakiel.fargosoulfabric.utils.FSFEffects;
import net.minecraft.core.particles.ParticleTypes;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.SoulKey;
import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulState.PenetratingSoulState;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
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
        PenetratingSoulState info = SoulStates.getSoulState(player, SoulStateRegister.PENETRATING_SOUL_INFO);
        if (info != null && info.pending) {
            info.pending = false;
            info.penetrate = 20;
        }
    }

    @Override
    public void sprintServer(Player player) {
        PenetratingSoulState info = SoulStates.getSoulState(player, SoulStateRegister.PENETRATING_SOUL_INFO);
        if (info != null && info.pending) {
            info.pending = false;
            info.penetrate = 20;
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        PenetratingSoulState info = SoulStates.getSoulState(target, SoulStateRegister.PENETRATING_SOUL_INFO);
        if (info != null && info.penetrate > 0) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public @Nullable ResourceLocation keyAction(Player player, SoulKey key) {
        if (key == SoulKey.PenetratingNinja) {
            PenetratingSoulState info = SoulStates.getSoulState(player, SoulStateRegister.PENETRATING_SOUL_INFO);
            if (info == null) {
                info = new PenetratingSoulState();
                SoulStates.putSoulState(player, info);
            }
            if (info.cooldown <= 0) {
                info.pending = true;
                info.cooldown = 600;
                // 渗透: a quiet shimmer, the sprint itself is what the player watches.
                FSFEffects.aura(player.level(), player, ParticleTypes.ENCHANT, 40, 0.6D, 0.6D);
            }
            return FSF.rl("penetrating_sprint");
        }
        return super.keyAction(player, key);
    }

    @Override
    public void handleKeyAction(Player player, ResourceLocation location) {
        if (location.equals(FSF.rl("penetrating_sprint"))) {
            PenetratingSoulState info = SoulStates.getSoulState(player, SoulStateRegister.PENETRATING_SOUL_INFO);
            if (info == null) {
                info = new PenetratingSoulState();
                SoulStates.putSoulState(player, info);
            }
            if (info.cooldown <= 0) {
                info.pending = true;
                info.cooldown = 600;
            }
        }
    }
}

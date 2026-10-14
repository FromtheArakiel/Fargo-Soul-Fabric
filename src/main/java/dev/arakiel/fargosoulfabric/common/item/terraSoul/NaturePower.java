package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.entity.ChlorophyteOrb;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.FSFPlayerSummons;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.summon.SummonDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FSFItemRegister.*;

public class NaturePower extends SoulItem {

    public NaturePower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(CrimsonSoulItem, LavaSoulItem, RainCloudSoulItem, FrostSoulItem, GreenSoulItem, MushroomSoulItem);
    }

    @Override
    public void tick(Player player) {
        List<ChlorophyteOrb> orbs = FSFPlayerSummons.get(player).getSummons().get(SummonTypeRegister.CHLOROPHYTE_ORB).stream().filter(orb -> orb.getKind() == ChlorophyteOrb.NATURE).toList();
        if (orbs.size() < 5) {
            ChlorophyteOrb orb = new ChlorophyteOrb();
            orb.setKind(ChlorophyteOrb.NATURE);
            FSFPlayerSummons.get(player).add(orb);
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (container.getSource() instanceof SummonDamageSource damageSource && damageSource.getSummon().getType() == SummonTypeRegister.CHLOROPHYTE_ORB) {
            modifiers.add(new ValueModifier(3, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(-15, ValueOperation.ADD_VALUE));
    }
}

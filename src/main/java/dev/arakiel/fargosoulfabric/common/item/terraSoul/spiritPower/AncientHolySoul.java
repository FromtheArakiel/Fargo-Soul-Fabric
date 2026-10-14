package dev.arakiel.fargosoulfabric.common.item.terraSoul.spiritPower;

import dev.arakiel.fargosoulfabric.common.entity.TerraBlade;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.FSFPlayerSummons;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class AncientHolySoul extends SoulItem {

    public AncientHolySoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        List<TerraBlade> blades = FSFPlayerSummons.get(player).getSummons().get(SummonTypeRegister.TERRA_BLADE);
        if (blades.stream().noneMatch(terraBlade -> terraBlade.ancientHolySoul)) {
            TerraBlade blade = new TerraBlade();
            blade.setOwner(player);
            blade.init(blade.getInterpolatedIdleState(0));
            blade.ancientHolySoul = true;
            FSFPlayerSummons.get(player).add(blade);
        }
    }
}

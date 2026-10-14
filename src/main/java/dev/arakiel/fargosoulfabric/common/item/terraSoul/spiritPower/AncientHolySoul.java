package dev.arakiel.fargosoulfabric.common.item.terraSoul.spiritPower;

import dev.arakiel.fargosoulfabric.common.entity.TerraBlade;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class AncientHolySoul extends SoulItem {

    public AncientHolySoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        List<TerraBlade> blades = FargoSoulHelper.get(player).getEntityData().get(SummonerAttachmentEntityRegister.TERRA_BLADE);
        if (blades.stream().noneMatch(terraBlade -> terraBlade.ancientHolySoul)) {
            TerraBlade blade = new TerraBlade();
            blade.setOwner(player);
            blade.init(blade.getInterpolatedIdleState(0));
            blade.ancientHolySoul = true;
            FargoSoulHelper.get(player).add(blade);
        }
    }
}

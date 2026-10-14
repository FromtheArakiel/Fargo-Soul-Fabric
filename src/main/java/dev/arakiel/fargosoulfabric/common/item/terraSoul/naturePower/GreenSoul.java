package dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower;

import dev.arakiel.fargosoulfabric.common.entity.ChlorophyteOrb;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.IEntityCollision;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Set;

public class GreenSoul extends SoulItem {

    public GreenSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canFly(Player player) {
        return true;
    }

    @Override
    public boolean canSprint(Player player) {
        return true;
    }

    @Override
    public void sprintCollision(Player player, List<IEntityCollision.HitContext> list, Set<LivingEntity> hits) {
        for (IEntityCollision.HitContext context : list) {
            if (hits.add(context.entity())) {
                context.entity().addEffect(new MobEffectInstance(MobEffects.POISON, 200));
            }
        }
    }

    @Override
    public void tick(Player player) {
        List<ChlorophyteOrb> orbs = FargoSoulHelper.get(player).getEntityData().get(SummonerAttachmentEntityRegister.CHLOROPHYTE_ORB).stream().filter(orb -> orb.getKind() == ChlorophyteOrb.GREEN).toList();
        if (orbs.isEmpty()) {
            ChlorophyteOrb orb = new ChlorophyteOrb();
            orb.setKind(ChlorophyteOrb.GREEN);
            FargoSoulHelper.get(player).add(orb);
        }
    }
}

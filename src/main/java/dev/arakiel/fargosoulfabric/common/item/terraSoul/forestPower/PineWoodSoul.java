package dev.arakiel.fargosoulfabric.common.item.terraSoul.forestPower;

import dev.arakiel.fargosoulfabric.common.attachment.SoulTargetCache;
import dev.arakiel.fargosoulfabric.common.entity.SnowBall;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PineWoodSoul extends SoulItem {

    public PineWoodSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        if (player.tickCount % 100 == 0) {
            List<LivingEntity> targets = SoulTargetCache.get(player).getEntitiesInRadius(player.getBoundingBox().getCenter(), 12, null);
            if (!targets.isEmpty()) {
                Vec3 pos = player.position().add(0, player.getBoundingBox().getYsize(), 0).offsetRandom(player.getRandom(), 4);
                LivingEntity target = targets.get(player.getRandom().nextInt(targets.size()));
                Vec3 dir = target.getBoundingBox().getCenter().subtract(pos).normalize();
                SnowBall ball = new SnowBall();
                ball.setDamageSourceSupplier(owner -> owner.damageSources().playerAttack(owner));
                ball.setPos(pos);
                ball.setVelocity(dir.scale(2));
                ball.setDamage(6);
                ball.join(player);
            }
        }
    }
}

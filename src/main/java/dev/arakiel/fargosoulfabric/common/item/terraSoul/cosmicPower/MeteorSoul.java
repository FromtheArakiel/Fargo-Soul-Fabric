package dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.entity.Meteor;
import dev.arakiel.fargosoulfabric.common.MovementState;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeteorSoul extends SoulItem {

    public MeteorSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        float length = (float) attacker.getDeltaMovement().length() * 0.2f;
        modifiers.add(new ValueModifier(Math.min(length, 0.25f), ValueOperation.ADD_MULTIPLIED_BASE));
        if (attacker.getRandom().nextFloat() < 0.1) {
            Vec3 targetPos = target.getBoundingBox().getCenter();
            Vec3 pos = targetPos.add(0, 8, 0).offsetRandom(attacker.getRandom(), 3);
            Vec3 dir = targetPos.add(0, 0.5f, 0).subtract(pos).normalize();
            Meteor meteor = new Meteor();
            meteor.setDamageSourceSupplier(owner -> owner.damageSources().playerAttack(owner));
            meteor.setPos(pos);
            meteor.setVelocity(dir);
            meteor.setDamage(container.getOriginalDamage() * 1.33f);
            meteor.join(attacker);
        }
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (container.getSource().is(DamageTypes.FALL)) {
            modifiers.add(new ValueModifier(-1, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    @Override
    public void movement(Player player, MovementState input) {
        Vec3 deltaMovement = player.getDeltaMovement();
        if (input.shiftKeyDown() && deltaMovement.y() > -1) {
            player.setDeltaMovement(new Vec3(deltaMovement.x(), -0.75, deltaMovement.z()));
        }
    }
}

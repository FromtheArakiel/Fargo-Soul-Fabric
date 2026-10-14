package dev.arakiel.fargosoulfabric.common.soulInfo;

import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.world.entity.projectile.Projectile;

public class ProjectileState extends SoulState {

    public static void set(Projectile projectile) {
        SoulStates.putSoulState(projectile, new ProjectileState());
    }

    @Override
    public SoulStateType<? extends SoulState> getType() {
        return SoulStateRegister.PROJECTILE_INFO;
    }
}

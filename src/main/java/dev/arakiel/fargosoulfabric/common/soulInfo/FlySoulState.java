package dev.arakiel.fargosoulfabric.common.soulInfo;

import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.world.entity.LivingEntity;

public class FlySoulState extends SoulState {

    public int flyTime = 0;

    @Override
    public void tick(LivingEntity living) {
        if (living.onGround()) {
            setRemove(true);
        }
    }

    @Override
    public SoulStateType<? extends SoulState> getType() {
        return SoulStateRegister.FLY_SOUL_INFO;
    }
}

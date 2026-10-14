package dev.arakiel.fargosoulfabric.common.soulState;

import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.world.entity.LivingEntity;

public class SprintSoulState extends SoulState {

    public int cooldown = 40;

    @Override
    public void tick(LivingEntity living) {
        if (--cooldown <= 0) {
            setRemove(true);
        }
    }

    @Override
    public SoulStateType<? extends SoulState> getType() {
        return SoulStateRegister.SPRINT_SOUL_INFO;
    }
}

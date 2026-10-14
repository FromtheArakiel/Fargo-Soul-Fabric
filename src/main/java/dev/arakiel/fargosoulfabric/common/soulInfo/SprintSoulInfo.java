package dev.arakiel.fargosoulfabric.common.soulInfo;

import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.world.entity.LivingEntity;

public class SprintSoulInfo extends SoulInfo {

    public int cooldown = 40;

    @Override
    public void tick(LivingEntity living) {
        if (--cooldown <= 0) {
            setRemove(true);
        }
    }

    @Override
    public SoulInfoType<? extends SoulInfo> getType() {
        return FargoSoulSoulInfoRegister.SPRINT_SOUL_INFO;
    }
}

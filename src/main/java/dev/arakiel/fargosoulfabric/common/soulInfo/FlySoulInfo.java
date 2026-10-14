package dev.arakiel.fargosoulfabric.common.soulInfo;

import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.world.entity.LivingEntity;

public class FlySoulInfo extends SoulInfo {

    public int flyTime = 0;

    @Override
    public void tick(LivingEntity living) {
        if (living.onGround()) {
            setRemove(true);
        }
    }

    @Override
    public SoulInfoType<? extends SoulInfo> getType() {
        return FargoSoulSoulInfoRegister.FLY_SOUL_INFO;
    }
}

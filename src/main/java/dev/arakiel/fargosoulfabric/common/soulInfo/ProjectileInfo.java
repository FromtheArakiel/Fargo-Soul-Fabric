package dev.arakiel.fargosoulfabric.common.soulInfo;

import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import net.minecraft.world.entity.projectile.Projectile;

public class ProjectileInfo extends SoulInfo {

    public static void set(Projectile projectile) {
        SoulInfoData.putSoulInfo(projectile, new ProjectileInfo());
    }

    @Override
    public SoulInfoType<? extends SoulInfo> getType() {
        return FargoSoulSoulInfoRegister.PROJECTILE_INFO;
    }
}

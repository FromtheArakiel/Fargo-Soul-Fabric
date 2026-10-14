package dev.arakiel.fargosoulfabric.client.network;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.api.Attachments;
import dev.arakiel.fargosoulfabric.common.attachment.DamageInfoData;
import dev.arakiel.fargosoulfabric.common.damageInfo.DamageInfo;
import dev.arakiel.fargosoulfabric.common.damageInfo.DamageInfoStyle;
import dev.arakiel.fargosoulfabric.common.damageInfo.DamageInfoStyleManager;
import dev.arakiel.fargosoulfabric.common.network.BatchedDamageInfoPayload;
import dev.arakiel.fargosoulfabric.common.network.BatchedParticlesPayload;
import java.util.ArrayList;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * The client end of the packets the summon library sends.
 *
 * <p>The payload types themselves live on the common side; only the handlers that need a client are
 * here.
 */
public final class FargoSoulClientPackets {

    private FargoSoulClientPackets() {
    }

    /** Registers the receivers. Called once, from the client initializer. */
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(BatchedParticlesPayload.TYPE, FargoSoulClientPackets::onParticles);
        ClientPlayNetworking.registerGlobalReceiver(BatchedDamageInfoPayload.TYPE, FargoSoulClientPackets::onDamageInfo);
    }

    private static void onParticles(BatchedParticlesPayload payload, ClientPlayNetworking.Context context) {
        Player player = context.player();
        Level level = player.level();

        for (BatchedParticlesPayload.Entry entry : payload.entries()) {
            level.addParticle(entry.options(), false, entry.x(), entry.y(), entry.z(),
                    entry.vx(), entry.vy(), entry.vz());
        }
    }

    private static void onDamageInfo(BatchedDamageInfoPayload payload, ClientPlayNetworking.Context context) {
        Player player = context.player();
        Level level = player.level();
        DamageInfoData damageInfoData = Attachments.get(level, FargoSoulAttachmentRegister.DAMAGE_INFO_DATA);

        for (BatchedDamageInfoPayload.Entry entry : payload.entries()) {
            // tryParse: a packet is allowed to name a damage type this client does not know, and a
            // malformed id must not throw while handling it.
            ResourceLocation damageType = ResourceLocation.tryParse(entry.damageType());
            if (damageType == null) {
                continue;
            }
            DamageInfoStyle style = DamageInfoStyleManager.INSTANCE.getStyle(damageType);
            if (style == null) {
                continue;
            }
            DamageInfo info = new DamageInfo(style, entry.damageAmount(),
                    new Vec3(entry.x(), entry.y(), entry.z()),
                    new Vec3(entry.vx(), entry.vy(), entry.vz()), entry.critical());
            Vec3 pos = info.getRenderPos(0.0F);
            Vec3 eyePosition = player.getEyePosition(0.0F);
            if (pos.distanceToSqr(eyePosition) < 4096.0) {
                damageInfoData.getActiveInfos()
                        .computeIfAbsent(info.getTexture(), key -> new ArrayList<>())
                        .add(info);
            }
        }
    }
}

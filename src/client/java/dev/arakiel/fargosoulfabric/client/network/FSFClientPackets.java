package dev.arakiel.fargosoulfabric.client.network;

import dev.arakiel.fargosoulfabric.client.config.FSFClientConfig;
import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.attachment.DamageNumbers;
import dev.arakiel.fargosoulfabric.common.damageNumbers.DamageInfo;
import dev.arakiel.fargosoulfabric.common.damageNumbers.DamageInfoStyle;
import dev.arakiel.fargosoulfabric.common.damageNumbers.DamageInfoStyleManager;
import dev.arakiel.fargosoulfabric.common.network.DamageNumbersPayload;
import dev.arakiel.fargosoulfabric.common.network.ParticleBatchPayload;
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
public final class FSFClientPackets {

    private FSFClientPackets() {
    }

    /** Registers the receivers. Called once, from the client initializer. */
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ParticleBatchPayload.TYPE, FSFClientPackets::onParticles);
        ClientPlayNetworking.registerGlobalReceiver(DamageNumbersPayload.TYPE, FSFClientPackets::onDamageInfo);
    }

    private static void onParticles(ParticleBatchPayload payload, ClientPlayNetworking.Context context) {
        Player player = context.player();
        Level level = player.level();

        for (ParticleBatchPayload.Entry entry : payload.entries()) {
            level.addParticle(entry.options(), false, entry.x(), entry.y(), entry.z(),
                    entry.vx(), entry.vy(), entry.vz());
        }
    }

    private static void onDamageInfo(DamageNumbersPayload payload, ClientPlayNetworking.Context context) {
        // Nothing to decode or keep when the numbers are switched off.
        if (!FSFClientConfig.damageNumbers()) {
            return;
        }
        Player player = context.player();
        Level level = player.level();
        DamageNumbers damageInfoData = Attachments.get(level, FSFAttachmentRegister.DAMAGE_INFO_DATA);

        for (DamageNumbersPayload.Entry entry : payload.entries()) {
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
            if (pos.distanceToSqr(eyePosition) < FSFClientConfig.damageNumberRangeSquared()) {
                damageInfoData.getActiveInfos()
                        .computeIfAbsent(info.getTexture(), key -> new ArrayList<>())
                        .add(info);
            }
        }
    }
}

package dev.arakiel.fargosoulfabric.client;

import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.client.damageNumbers.DamageInfoRenderDispatcher;
import dev.arakiel.fargosoulfabric.client.tooltip.TooltipHandler;
import dev.arakiel.fargosoulfabric.client.render.SummonRenderDispatcher;
import dev.arakiel.fargosoulfabric.common.attachment.DamageNumbers;
import dev.arakiel.fargosoulfabric.common.damageNumbers.DamageInfoStyleManager;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.packs.PackType;

/** The client side hooks of the summon library. */
public final class FSFClientListeners {

    private FSFClientListeners() {
    }

    public static void init() {
        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            var player = Minecraft.getInstance().player;
            lines.addAll(TooltipHandler.getMinionWeaponItemTooltip(stack, player));
            lines.addAll(TooltipHandler.getCustomTooltip(stack, player));
        });
        ClientTickEvents.END_CLIENT_TICK.register(FSFClientListeners::tick);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(DamageInfoStyleManager.INSTANCE);
        WorldRenderEvents.AFTER_ENTITIES.register(FSFClientListeners::afterEntities);
    }

    /** Ages the damage numbers of the local client. */
    private static void tick(Minecraft minecraft) {
        ClientLevel level = minecraft.level;
        if (level == null) {
            return;
        }
        DamageNumbers data = Attachments.get(level, FSFAttachmentRegister.DAMAGE_INFO_DATA);
        data.tickActiveInfos();
    }

    /** Draws the summons and the floating damage numbers of every player. */
    private static void afterEntities(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        PoseStack poseStack = context.matrixStack();
        if (poseStack == null) {
            return;
        }
        float partialTick = context.tickCounter().getGameTimeDeltaPartialTick(true);
        SummonRenderDispatcher.render(level, context.camera(), poseStack,
                context.consumers(), partialTick);
        DamageInfoRenderDispatcher.render(level, context.camera(), context.consumers(), partialTick);
    }
}

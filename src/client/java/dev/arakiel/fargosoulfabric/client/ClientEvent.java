package dev.arakiel.fargosoulfabric.client;

import dev.arakiel.fargosoulfabric.api.Attachments;
import dev.arakiel.fargosoulfabric.api.SoulKey;
import dev.arakiel.fargosoulfabric.client.api.FargoSoulClientHooks;
import dev.arakiel.fargosoulfabric.common.MovementState;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.soulInfo.FlySoulInfo;
import dev.arakiel.fargosoulfabric.common.soulInfo.SprintSoulInfo;
import dev.arakiel.fargosoulfabric.network.KeyHandlePacket;
import dev.arakiel.fargosoulfabric.network.SprintPacket;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import dev.arakiel.fargosoulfabric.client.utils.KeyUtils;
import dev.arakiel.fargosoulfabric.api.FargoSoulHooks;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

/** Everything the souls do on the client. */
public final class ClientEvent {

    private ClientEvent() {
    }

    public static void init() {
        FargoSoulHooks.KEY_INPUT.register(ClientEvent::keyInput);
        FargoSoulClientHooks.MOVEMENT_INPUT.register(ClientEvent::movementInput);
        FargoSoulClientHooks.FOG.register(ClientEvent::renderFog);
        FargoSoulHooks.FIRE_OVERLAY.register(ClientEvent::fireOverlay);
        ItemTooltipCallback.EVENT.register(ClientEvent::addSoulTooltips);
    }

    /** Appends the soul descriptions to the tooltip of the soul items. */
    private static void addSoulTooltips(net.minecraft.world.item.ItemStack stack,
                                        net.minecraft.world.item.Item.TooltipContext context,
                                        TooltipFlag flag,
                                        List<Component> lines) {
        if (stack.getItem() instanceof SoulItem soulItem) {
            lines.addAll(soulItem.getTooltip(stack, Screen.hasShiftDown(), true));
        }
    }

    private static void keyInput(Player player, SoulKey key) {
        // The binding is all the server needs; it resolves the soul itself.
        ClientPlayNetworking.send(new KeyHandlePacket(key));
    }

    private static void movementInput(Player player, Input input) {
        MovementState state = new MovementState(input.up, input.jumping, input.shiftKeyDown);
        SoulItemData.forEach(player, soulItem -> soulItem.movement(player, state));
        SoulItemData soulItemData = Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA);

        if (KeyUtils.isDoubleTappingForward(input) && soulItemData.isCanSprint()) {
            SprintSoulInfo info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.SPRINT_SOUL_INFO);
            if (info == null) {
                info = new SprintSoulInfo();
                SoulInfoData.putSoulInfo(player, info);

                List<ValueModifier> modifiers = new ArrayList<>();
                SoulItemData.forEach(player, soulItem -> soulItem.sprintClient(player, modifiers));
                float scale = ValueModifier.getModifierAfter(1f, modifiers);
                Vec3 lookAngle = player.getLookAngle();
                player.addDeltaMovement(new Vec3(lookAngle.x, lookAngle.y * 0.25f, lookAngle.z).scale(scale));
                ClientPlayNetworking.send(new SprintPacket());
            }
        }

        if (soulItemData.getMaxFlyTime() > 0) {
            player.fallDistance = 0;
            if (input.jumping) {
                FlySoulInfo info = SoulInfoData.getSoulInfo(player, FargoSoulSoulInfoRegister.FLY_SOUL_INFO);
                if (info == null) {
                    info = new FlySoulInfo();
                    SoulInfoData.putSoulInfo(player, info);
                }
                Vec3 deltaMovement = player.getDeltaMovement();
                double addedX = 0;
                double addedY = 0;
                double addedZ = 0;
                if (info.flyTime < soulItemData.getMaxFlyTime()) {
                    info.flyTime++;
                    if (deltaMovement.y() < 0.5) {
                        addedY = Math.min(0.25, 0.5 - deltaMovement.y());
                    }
                } else {
                    if (deltaMovement.y() < -0.05) {
                        addedY = Math.min(0.05, -0.05 - deltaMovement.y());
                    }
                }
                if (!player.onGround() && input.up) {
                    Vec3 lookAngle = player.getLookAngle().normalize();
                    addedX = lookAngle.x() * 0.1;
                    addedZ = lookAngle.z() * 0.1;
                }
                player.addDeltaMovement(new Vec3(addedX, addedY, addedZ));
            }
        }
    }

    private static void renderFog(Camera camera, FargoSoulHooks.Value nearPlane, FargoSoulHooks.Value farPlane,
                                  FargoSoulHooks.Value cancel) {
        Minecraft minecraft = Minecraft.getInstance();
        BlockPos blockPos = camera.getBlockPosition();
        if (!(minecraft.level instanceof net.minecraft.world.level.Level level)) {
            return;
        }
        if (!(minecraft.player instanceof LocalPlayer player)) {
            return;
        }
        if (camera.getEntity().isSpectator()) {
            return;
        }
        SoulItemData soulItemData = Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA);
        FluidState fluidState = level.getFluidState(blockPos);
        Fluid fluid = fluidState.getType();
        if (!soulItemData.isRenderLavaFog() && fluid.isSame(Fluids.LAVA)
                && camera.getPosition().y < blockPos.getY() + fluidState.getHeight(level, blockPos)) {
            nearPlane.set(-4.0F);
            farPlane.set(20.0F);
            cancel.cancel();
        }
    }

    private static void fireOverlay(Player player, FargoSoulHooks.Value cancel) {
        SoulItemData soulItemData = Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA);
        if (!soulItemData.isRenderFireOverlay()) {
            cancel.cancel();
        }
    }
}

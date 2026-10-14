package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.curios.api.type.inventory.IItemHandlerModifiable;
import dev.arakiel.fargosoulfabric.api.Attachments;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.arakiel.curios.api.CuriosApi;
import dev.arakiel.curios.api.type.inventory.IItemHandlerModifiable;
import dev.arakiel.curios.api.type.capability.ICuriosItemHandler;

import java.util.*;
import java.util.function.Consumer;

public class SoulItemData implements StreamCodec<RegistryFriendlyByteBuf, SoulItemData> {

    private final Set<SoulItem> cache = new HashSet<>();
    private boolean canSprint = false;
    private int maxFlyTime = 0;
    private boolean renderLavaFog = true;
    private boolean renderFireOverlay = true;

    public Set<SoulItem> getCache() {
        return cache;
    }

    public int getMaxFlyTime() {
        return maxFlyTime;
    }

    public boolean isRenderLavaFog() {
        return renderLavaFog;
    }

    public boolean isCanSprint() {
        return canSprint;
    }

    public boolean isRenderFireOverlay() {
        return renderFireOverlay;
    }

    public static boolean isEquipped(LivingEntity living, ItemLike itemLike) {
        if (living instanceof Player player && player.isAlive() && itemLike.asItem() instanceof SoulItem soulItem) {
            SoulItemData data = Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA);
            return data.getCache().contains(soulItem);
        }
        return false;
    }

    public static void forEach(Player player, Consumer<SoulItem> consumer) {
        SoulItemData data = Attachments.get(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA);
        data.getCache().forEach(consumer);
    }

    public void tick(Player player){
        canSprint = false;
        maxFlyTime = 0;
        renderLavaFog = true;
        renderFireOverlay = true;
        Collection<AttributeInstance> attributes = player.getAttributes().getSyncableAttributes();
        for (AttributeInstance attribute : attributes) {
            Set<AttributeModifier> modifiers = attribute.getModifiers();
            for (AttributeModifier modifier : modifiers) {
                if (modifier.id().getPath().contains("_volatile")) {
                    attribute.removeModifier(modifier);
                }
            }
        }
        boolean anyFly = false;
        List<ValueModifier> flyModifiers = new ArrayList<>();
        for (SoulItem soulItem : cache) {
            if (!canSprint && soulItem.canSprint(player)) {
                canSprint = true;
            }
            if (soulItem.canFly(player)) {
                anyFly = true;
            }
            soulItem.getMaxFlyTime(player, flyModifiers);
            if (renderLavaFog && !soulItem.renderLavaFog(player)) {
                renderLavaFog = false;
            }
            if (renderFireOverlay && !soulItem.renderFireOverlay(player)) {
                renderFireOverlay = false;
            }
            soulItem.getAttributeModifiers(player).forEach((soulAttribute, attributeModifier) -> {
                AttributeInstance attribute = player.getAttribute(soulAttribute);
                if (attribute != null) {
                    AttributeModifier volatileModifier = new AttributeModifier(attributeModifier.id().withSuffix("_volatile"), attributeModifier.amount(), attributeModifier.operation());
                    if (attribute.hasModifier(volatileModifier.id())) {
                        attribute.removeModifier(volatileModifier.id());
                    }
                    attribute.addPermanentModifier(volatileModifier);
                }
            });
        }
        if (anyFly) {
            player.fallDistance = 0;
            maxFlyTime = (int) ValueModifier.getModifierAfter(100f, flyModifiers);
        }
        Attachments.sync(player, FargoSoulAttachmentRegister.SOUL_ITEM_DATA);
    }

    public void update(Player player) {
        cache.clear();
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(player).orElse(null);
        if (handler != null) {
            IItemHandlerModifiable curios = handler.getEquippedCurios();
            int slots = handler.getSlots();
            Set<SoulItem> set = new HashSet<>();
            for (int i = 0; i < slots; i++) {
                ItemStack itemStack = curios.getStackInSlot(i);
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof SoulItem soulItem) {
                    set.add(soulItem);
                }
            }
            Set<SoulItem> flattened = new HashSet<>();
            for (SoulItem soulItem : set) {
                flatten(soulItem, flattened);
            }
            cache.addAll(flattened);
        }
    }

    private static void flatten(SoulItem soulItem, Set<SoulItem> result) {
        if (!result.add(soulItem)) {
            return;
        }
        for (SoulItem child : soulItem.getSoulItemList()) {
            flatten(child, result);
        }
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf registryFriendlyByteBuf, @NotNull SoulItemData soulItemData) {
        // The soul list travels with every packet. A Fabric attachment value is replaced by whatever
        // the codec decodes, so a packet that carried only the flags would wipe the list the client
        // already had - and with it every active soul key, which is read from that list.
        DefaultedRegistry<Item> item = BuiltInRegistries.ITEM;
        registryFriendlyByteBuf.writeInt(soulItemData.cache.size());
        for (SoulItem soulItem : soulItemData.cache) {
            registryFriendlyByteBuf.writeResourceLocation(item.getKey(soulItem));
        }
        registryFriendlyByteBuf.writeBoolean(soulItemData.canSprint);
        registryFriendlyByteBuf.writeInt(soulItemData.maxFlyTime);
        registryFriendlyByteBuf.writeBoolean(soulItemData.renderLavaFog);
        registryFriendlyByteBuf.writeBoolean(soulItemData.renderFireOverlay);
    }

    @Override
    public @NotNull SoulItemData decode(@NotNull RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        SoulItemData data = new SoulItemData();
        DefaultedRegistry<Item> item = BuiltInRegistries.ITEM;
        int size = registryFriendlyByteBuf.readInt();
        for (int i = 0; i < size; i++) {
            if (item.get(registryFriendlyByteBuf.readResourceLocation()) instanceof SoulItem soulItem) {
                data.cache.add(soulItem);
            }
        }
        data.canSprint = registryFriendlyByteBuf.readBoolean();
        data.maxFlyTime = registryFriendlyByteBuf.readInt();
        data.renderLavaFog = registryFriendlyByteBuf.readBoolean();
        data.renderFireOverlay = registryFriendlyByteBuf.readBoolean();
        return data;
    }
}

package dev.arakiel.fargosoulfabric.common.attachment;

import dev.arakiel.fargosoulfabric.common.soulState.SoulState;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FSFRegisters;
import dev.arakiel.fargosoulfabric.common.Attachments;
import dev.arakiel.fargosoulfabric.common.NbtSerializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The soul state of one entity.
 *
 * <p>The entries are keyed by the soul info type, are written to NBT when the world is saved and are
 * sent to the clients that track the entity.
 */
public record SoulStates(Map<SoulStateType<?>, SoulState> data)
        implements StreamCodec<RegistryFriendlyByteBuf, SoulStates>, NbtSerializable<CompoundTag> {

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends SoulState> T getSoulState(Entity entity, Supplier<SoulStateType<T>> type) {
        return getSoulState(entity, type.get());
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends SoulState> T getSoulState(Entity entity, SoulStateType<T> type) {
        return (T) dataOf(entity, type).data().get(type);
    }

    public static void putSoulState(Entity entity, SoulState soulInfo) {
        dataOf(entity, soulInfo.getType()).data().put(soulInfo.getType(), soulInfo);
    }

    public static <T extends SoulState> void removeSoulState(Entity entity, Supplier<SoulStateType<T>> type) {
        dataOf(entity, type.get()).data().remove(type.get());
    }

    /**
     * The attachment a soul info of {@code type} is stored in.
     *
     * <p>Client sided infos never leave the client, so they use their own attachment; everything else is
     * the authoritative value the server keeps and sends to the clients that track the entity.
     */
    private static SoulStates dataOf(Entity entity, SoulStateType<?> type) {
        return Attachments.get(entity, type.isClientSide()
                ? FSFAttachmentRegister.LOCAL_SOUL_INFO_DATA
                : FSFAttachmentRegister.SOUL_INFO_DATA);
    }

    /**
     * Ticks the soul state of one living entity.
     *
     * <p>Called for every living entity that ticks, on both sides: the server decreases the cooldowns of
     * the authoritative infos and sends the result to the clients, the client only decreases the infos it
     * owns itself. Without this call a cooldown never runs out and a state is never cleaned up, so an
     * ability would fire exactly once.
     */
    public static void tick(LivingEntity living) {
        if (living.level().isClientSide()) {
            SoulStates local = Attachments.getOrNull(living, FSFAttachmentRegister.LOCAL_SOUL_INFO_DATA);
            if (local != null) {
                tick(living, local.data());
            }
            return;
        }
        SoulStates value = Attachments.getOrNull(living, FSFAttachmentRegister.SOUL_INFO_DATA);
        if (value == null) {
            return;
        }
        boolean hadEntries = !value.data().isEmpty();
        tick(living, value.data());
        // Writing the value again is what makes Fabric send it to the clients that track the entity.
        if (hadEntries || !value.data().isEmpty()) {
            Attachments.sync(living, FSFAttachmentRegister.SOUL_INFO_DATA);
        }
    }

    /** Ticks every entry of one map and drops the ones that asked to be removed. */
    private static void tick(LivingEntity living, Map<SoulStateType<?>, SoulState> data) {
        data.values().removeIf(soulInfo -> {
            soulInfo.tick(living);
            boolean remove = soulInfo.isRemove();
            if (remove) {
                soulInfo.onRemove(living);
            }
            return remove;
        });
    }

    /** Reads the value a saved attachment was written with. */
    public static SoulStates load(CompoundTag tag) {
        SoulStates infoData = new SoulStates(new HashMap<>());
        infoData.deserializeNBT(null, tag);
        return infoData;
    }

    /** The value that is written when the world is saved. */
    public CompoundTag save() {
        return this.serializeNBT(null);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        for (Map.Entry<SoulStateType<?>, SoulState> entry : data.entrySet()) {
            if (entry.getKey().isClientSide()) {
                continue;
            }
            ResourceLocation key = FSFRegisters.SOUL_INFO_TYPE.getKey(entry.getKey());
            if (key == null) {
                continue;
            }
            tag.put(key.toString(), entry.getValue().serializeNBT(provider));
        }

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        data.keySet().removeIf(soulInfoType -> !soulInfoType.isClientSide());

        for (String key : tag.getAllKeys()) {
            SoulStateType<?> type = FSFRegisters.SOUL_INFO_TYPE.get(ResourceLocation.tryParse(key));
            if (type == null) {
                continue;
            }
            SoulState soulInfo = type.supplier().get();
            soulInfo.deserializeNBT(provider, tag.getCompound(key));
            data.put(type, soulInfo);
        }
    }

    @Override
    public void encode(@NotNull RegistryFriendlyByteBuf registryFriendlyByteBuf,
                       @NotNull SoulStates soulInfoData) {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<SoulStateType<?>, SoulState> entry : soulInfoData.data().entrySet()) {
            if (entry.getKey().isClientSide()) {
                continue;
            }
            ResourceLocation key = FSFRegisters.SOUL_INFO_TYPE.getKey(entry.getKey());
            if (key == null) {
                continue;
            }
            tag.put(key.toString(), entry.getValue().serializeNBT(registryFriendlyByteBuf.registryAccess()));
        }
        registryFriendlyByteBuf.writeNbt(tag);
    }

    @Override
    public @NotNull SoulStates decode(@NotNull RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        SoulStates infoData = new SoulStates(new HashMap<>());
        CompoundTag tag = registryFriendlyByteBuf.readNbt();
        infoData.deserializeNBT(registryFriendlyByteBuf.registryAccess(), tag != null ? tag : new CompoundTag());
        return infoData;
    }
}

package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachment.DamageInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.HealthData;
import dev.arakiel.fargosoulfabric.common.attachment.ImmunityData;
import dev.arakiel.fargosoulfabric.common.attachment.InvincibleData;
import dev.arakiel.fargosoulfabric.common.attachment.ParticlesData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulInfoData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargetCache;
import dev.arakiel.fargosoulfabric.common.attachment.SummonMarkTracker;
import dev.arakiel.fargosoulfabric.common.attachment.TargetCache;
import java.util.HashMap;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;

/** Everything this mod attaches to an entity or a level. */
public final class FargoSoulAttachmentRegister {

    public static final AttachmentType<SoulItemData> SOUL_ITEM_DATA = AttachmentRegistry.create(
            FargoSoul.rl("soul_item_data"),
            builder -> builder.initializer(SoulItemData::new)
                    .syncWith(new SoulItemData(), AttachmentSyncPredicate.all()));

    public static final AttachmentType<SoulInfoData> SOUL_INFO_DATA = AttachmentRegistry.create(
            FargoSoul.rl("soul_info_data"),
            builder -> builder.initializer(() -> new SoulInfoData(new HashMap<>()))
                    .persistent(CompoundTag.CODEC.xmap(SoulInfoData::load, SoulInfoData::save))
                    .syncWith(new SoulInfoData(new HashMap<>()), AttachmentSyncPredicate.all()));

    /**
     * The soul infos that only exist on the client.
     *
     * <p>They deliberately live in their own attachment: Fabric replaces a synced attachment with the
     * value that arrives from the server, so client side state written into the synced attachment would
     * be wiped as soon as the server sends an update. This one is neither persisted nor synced.
     */
    public static final AttachmentType<SoulInfoData> LOCAL_SOUL_INFO_DATA = AttachmentRegistry.create(
            FargoSoul.rl("local_soul_info_data"),
            builder -> builder.initializer(() -> new SoulInfoData(new HashMap<>())));

    public static final AttachmentType<SoulTargetCache> SOUL_TARGET_CACHE = AttachmentRegistry.create(
            FargoSoul.rl("soul_target_cache"), builder -> builder.initializer(SoulTargetCache::new));

    public static final AttachmentType<AttachmentEntityData> ENTITY_DATA = AttachmentRegistry.create(
            FargoSoul.rl("attachment_entity_data"),
            builder -> builder.initializer(AttachmentEntityData::new)
                    .syncWith(new AttachmentEntityData.SyncHandler(), AttachmentSyncPredicate.all()));

    public static final AttachmentType<SummonMarkTracker> SUMMON_MARK_DATA = AttachmentRegistry.create(
            FargoSoul.rl("summon_mark_tracker"),
            builder -> builder.initializer(SummonMarkTracker::new)
                    .syncWith(new SummonMarkTracker.SyncHandler(), AttachmentSyncPredicate.all()));

    public static final AttachmentType<ImmunityData> IMMUNITY_DATA = AttachmentRegistry.create(
            FargoSoul.rl("immunity_data"), builder -> builder.initializer(ImmunityData::new));

    public static final AttachmentType<InvincibleData> INVINCIBLE_DATA = AttachmentRegistry.create(
            FargoSoul.rl("invincible_data"), builder -> builder.initializer(InvincibleData::new));

    public static final AttachmentType<HealthData> HEALTH_DATA = AttachmentRegistry.create(
            FargoSoul.rl("health_data"), builder -> builder.initializer(HealthData::new));

    public static final AttachmentType<TargetCache> TARGET_CACHE = AttachmentRegistry.create(
            FargoSoul.rl("target_cache"), builder -> builder.initializer(TargetCache::new));

    public static final AttachmentType<ParticlesData> BATCHED_PARTICLES = AttachmentRegistry.create(
            FargoSoul.rl("batched_particles"), builder -> builder.initializer(ParticlesData::new));

    public static final AttachmentType<DamageInfoData> DAMAGE_INFO_DATA = AttachmentRegistry.create(
            FargoSoul.rl("damage_info_data"), builder -> builder.initializer(DamageInfoData::new));

    private FargoSoulAttachmentRegister() {
    }

    public static void init() {
        // Registration happens in the field initializers above.
    }
}

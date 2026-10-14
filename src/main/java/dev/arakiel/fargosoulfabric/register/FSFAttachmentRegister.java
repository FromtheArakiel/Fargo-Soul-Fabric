package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.attachment.DamageNumbers;
import dev.arakiel.fargosoulfabric.common.attachment.SoulHealth;
import dev.arakiel.fargosoulfabric.common.attachment.Immunities;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import dev.arakiel.fargosoulfabric.common.attachment.ParticleBatch;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.common.attachment.SummonMarks;
import dev.arakiel.fargosoulfabric.common.attachment.RecentTargets;
import java.util.HashMap;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;

/** Everything this mod attaches to an entity or a level. */
public final class FSFAttachmentRegister {

    public static final AttachmentType<PlayerSouls> SOUL_ITEM_DATA = AttachmentRegistry.create(
            FSF.rl("player_souls"),
            builder -> builder.initializer(PlayerSouls::new)
                    .syncWith(new PlayerSouls(), AttachmentSyncPredicate.all()));

    public static final AttachmentType<SoulStates> SOUL_INFO_DATA = AttachmentRegistry.create(
            FSF.rl("soul_states"),
            builder -> builder.initializer(() -> new SoulStates(new HashMap<>()))
                    .persistent(CompoundTag.CODEC.xmap(SoulStates::load, SoulStates::save))
                    .syncWith(new SoulStates(new HashMap<>()), AttachmentSyncPredicate.all()));

    /**
     * The soul infos that only exist on the client.
     *
     * <p>They deliberately live in their own attachment: Fabric replaces a synced attachment with the
     * value that arrives from the server, so client side state written into the synced attachment would
     * be wiped as soon as the server sends an update. This one is neither persisted nor synced.
     */
    public static final AttachmentType<SoulStates> LOCAL_SOUL_INFO_DATA = AttachmentRegistry.create(
            FSF.rl("local_soul_states"),
            builder -> builder.initializer(() -> new SoulStates(new HashMap<>())));

    public static final AttachmentType<SoulTargets> SOUL_TARGET_CACHE = AttachmentRegistry.create(
            FSF.rl("soul_target_cache"), builder -> builder.initializer(SoulTargets::new));

    public static final AttachmentType<SummonData> ENTITY_DATA = AttachmentRegistry.create(
            FSF.rl("summon_data"),
            builder -> builder.initializer(SummonData::new)
                    .syncWith(new SummonData.SyncHandler(), AttachmentSyncPredicate.all()));

    public static final AttachmentType<SummonMarks> SUMMON_MARK_DATA = AttachmentRegistry.create(
            FSF.rl("summon_marks"),
            builder -> builder.initializer(SummonMarks::new)
                    .syncWith(new SummonMarks.SyncHandler(), AttachmentSyncPredicate.all()));

    public static final AttachmentType<Immunities> IMMUNITY_DATA = AttachmentRegistry.create(
            FSF.rl("immunities"), builder -> builder.initializer(Immunities::new));

    public static final AttachmentType<Invulnerability> INVINCIBLE_DATA = AttachmentRegistry.create(
            FSF.rl("invulnerability"), builder -> builder.initializer(Invulnerability::new));

    public static final AttachmentType<SoulHealth> HEALTH_DATA = AttachmentRegistry.create(
            FSF.rl("soul_health"), builder -> builder.initializer(SoulHealth::new));

    public static final AttachmentType<RecentTargets> TARGET_CACHE = AttachmentRegistry.create(
            FSF.rl("recent_targets"), builder -> builder.initializer(RecentTargets::new));

    public static final AttachmentType<ParticleBatch> BATCHED_PARTICLES = AttachmentRegistry.create(
            FSF.rl("particle_batch"), builder -> builder.initializer(ParticleBatch::new));

    public static final AttachmentType<DamageNumbers> DAMAGE_INFO_DATA = AttachmentRegistry.create(
            FSF.rl("damage_numbers"), builder -> builder.initializer(DamageNumbers::new));

    private FSFAttachmentRegister() {
    }

    public static void init() {
        // Registration happens in the field initializers above.
    }
}

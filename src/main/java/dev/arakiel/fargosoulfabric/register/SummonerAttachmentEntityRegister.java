package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.entity.BloodDrop;
import dev.arakiel.fargosoulfabric.common.entity.ChlorophyteOrb;
import dev.arakiel.fargosoulfabric.common.entity.GhostOrb;
import dev.arakiel.fargosoulfabric.common.entity.TerraBlade;
import dev.arakiel.fargosoulfabric.common.entity.LightningOrb;
import dev.arakiel.fargosoulfabric.common.entity.Meteor;
import dev.arakiel.fargosoulfabric.common.entity.Petal;
import dev.arakiel.fargosoulfabric.common.entity.ShadowOrb;
import dev.arakiel.fargosoulfabric.common.entity.SnowBall;
import dev.arakiel.fargosoulfabric.common.entity.Star;
import dev.arakiel.fargosoulfabric.common.entity.TrackingBlood;
import dev.arakiel.fargosoulfabric.common.entity.Sprint;
import dev.arakiel.fargosoulfabric.common.entity.Vortex;
import net.minecraft.resources.ResourceLocation;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntity;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityType;

import java.util.function.Supplier;

public class SummonerAttachmentEntityRegister {

    public static final AttachmentEntityType<Meteor> METEOR =
            register("meteor", Meteor::new);

    public static final AttachmentEntityType<Vortex> VORTEX =
            register("vortex", Vortex::new);

    public static final AttachmentEntityType<Sprint> SPRINT =
            register("sprint", Sprint::new);

    public static final AttachmentEntityType<ShadowOrb> SHADOW_ORB =
            register("shadow_orb", ShadowOrb::new);

    public static final AttachmentEntityType<LightningOrb> LIGHTNING_ORB =
            register("lightning_orb", LightningOrb::new);

    public static final AttachmentEntityType<SnowBall> SNOW_BALL =
            register("snow_ball", SnowBall::new);

    public static final AttachmentEntityType<TrackingBlood> TRACKING_BLOOD =
            register("tracking_blood", TrackingBlood::new);

    public static final AttachmentEntityType<BloodDrop> BLOOD_DROP =
            register("blood_drop", BloodDrop::new);

    public static final AttachmentEntityType<Star> STAR =
            register("star", Star::new);

    public static final AttachmentEntityType<Petal> PETAL =
            register("petal", Petal::new);

    public static final AttachmentEntityType<GhostOrb> GHOST_ORB =
            register("ghost_orb", GhostOrb::new);

    public static final AttachmentEntityType<ChlorophyteOrb> CHLOROPHYTE_ORB =
            register("chlorophyte_orb", ChlorophyteOrb::new);

    public static final AttachmentEntityType<TerraBlade> TERRA_BLADE =
            register("terra_blade", TerraBlade::new);

    private static <T extends AttachmentEntity> AttachmentEntityType<T> register(String name, Supplier<T> supplier) {
        ResourceLocation id = FargoSoul.rl(name);
        AttachmentEntityType<T> type = new AttachmentEntityType<>(id, supplier);
        FargoSoulRegisters.ATTACHMENT_ENTITY_TYPES.register(id, type);
        return type;
    }

    public static void init() {
        // Registered by the field initializers above.
    }
}
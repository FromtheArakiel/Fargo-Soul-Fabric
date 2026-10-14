package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower.BlazeSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower.NebulaSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower.StardustSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower.VortexSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower.DarkArtistSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower.NecromancerSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower.NinjaSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.earthPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.forestPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.lifePower.BeetleSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower.CrimsonSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower.FrostSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower.RainCloudSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.spiritPower.ForbiddenSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.spiritPower.GhostSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.spiritPower.HolySoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower.CopperSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower.IronSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower.SilverSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower.TinSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.willPower.GoldSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.willPower.PlatinumSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.willPower.RedRidingSoul;
import dev.arakiel.fargosoulfabric.common.soulInfo.*;

import java.util.function.Supplier;

public class SoulStateRegister {

    public static final SoulStateType<SprintSoulState> SPRINT_SOUL_INFO =
            register("sprint_state", SprintSoulState::new, true);
    public static final SoulStateType<FlySoulState> FLY_SOUL_INFO =
            register("fly_state", FlySoulState::new, true);
    public static final SoulStateType<ProjectileState> PROJECTILE_INFO =
            register("projectile_state", ProjectileState::new, true);

    public static final SoulStateType<CosmicPower.Info> COSMIC_POWER_INFO =
            register("cosmic_power_info", CosmicPower.Info::new);
    public static final SoulStateType<BlazeSoul.Info> BLAZE_SOUL_INFO =
            register("blaze_state", BlazeSoul.Info::new);
    public static final SoulStateType<NebulaSoul.Info> NEBULA_SOUL_INFO =
            register("nebula_state", NebulaSoul.Info::new);
    public static final SoulStateType<StardustSoul.Info> STARDUST_SOUL_INFO =
            register("stardust_state", StardustSoul.Info::new);
    public static final SoulStateType<VortexSoul.Info> VORTEX_SOUL_INFO =
            register("vortex_state", VortexSoul.Info::new);

    public static final SoulStateType<DarkArtistSoul.Info> DARK_ARTIST_SOUL_INFO =
            register("dark_artist_state", DarkArtistSoul.Info::new);

    public static final SoulStateType<NecromancerSoul.Info> NECROMANCER_SOUL_INFO =
            register("necromancer_state", NecromancerSoul.Info::new);

    public static final SoulStateType<PenetratingSoulState> PENETRATING_SOUL_INFO =
            register("penetrating_state", PenetratingSoulState::new);

    public static final SoulStateType<NinjaSoul.Info> NINJA_SOUL_INFO =
            register("ninja_state", NinjaSoul.Info::new);

    public static final SoulStateType<CopperSoul.Info> COPPER_SOUL_INFO =
            register("copper_state", CopperSoul.Info::new);

    public static final SoulStateType<IronSoul.Info> IRON_SOUL_INFO =
            register("iron_state", IronSoul.Info::new);

    public static final SoulStateType<SilverSoul.Info> SILVER_SOUL_INFO =
            register("silver_state", SilverSoul.Info::new);

    public static final SoulStateType<TinSoul.Info> TIN_SOUL_INFO =
            register("tin_state", TinSoul.Info::new);

    public static final SoulStateType<TerraPower.Info> TERRA_POWER_INFO =
            register("terra_power_info", TerraPower.Info::new);

    public static final SoulStateType<WoodSoul.Info> WOOD_SOUL_INFO =
            register("wood_state", WoodSoul.Info::new);

    public static final SoulStateType<RoseWoodSoul.Info> ROSE_WOOD_SOUL_INFO =
            register("rose_wood_state", RoseWoodSoul.Info::new);

    public static final SoulStateType<EbonyWoodSoul.Info> EBONY_WOOD_SOUL_INFO =
            register("ebony_wood_state", EbonyWoodSoul.Info::new);

    public static final SoulStateType<ShadowWoodSoul.Info> SHADOW_WOOD_SOUL_INFO =
            register("shadow_wood_state", ShadowWoodSoul.Info::new);

    public static final SoulStateType<PearlWoodSoul.Info> PEARL_WOOD_SOUL_INFO =
            register("pearl_wood_state", PearlWoodSoul.Info::new);

    public static final SoulStateType<ForestPower.Info> FOREST_POWER_INFO =
            register("forest_power_info", ForestPower.Info::new);

    public static final SoulStateType<EarthPower.Info> EARTH_POWER_INFO =
            register("earth_power_info", EarthPower.Info::new);

    public static final SoulStateType<LifePower.Info> LIFE_POWER_INFO =
            register("life_power_info", LifePower.Info::new);

    public static final SoulStateType<WillPower.Info> WILL_POWER_INFO =
            register("will_power_info", WillPower.Info::new);

    public static final SoulStateType<CobaltSoul.Info> COBALT_SOUL_INFO =
            register("cobalt_state", CobaltSoul.Info::new);

    public static final SoulStateType<FrostSoul.Info> FROST_SOUL_INFO =
            register("frost_state", FrostSoul.Info::new);

    public static final SoulStateType<CrimsonSoul.Info> CRIMSON_SOUL_INFO =
            register("crimson_state", CrimsonSoul.Info::new);

    public static final SoulStateType<PlatinumSoul.Info> PLATINUM_SOUL_INFO =
            register("platinum_state", PlatinumSoul.Info::new);

    public static final SoulStateType<GoldSoul.Info> GOLD_SOUL_INFO =
            register("gold_state", GoldSoul.Info::new);

    public static final SoulStateType<RainCloudSoul.Info> RAIN_CLOUD_SOUL_INFO =
            register("rain_cloud_state", RainCloudSoul.Info::new);

    public static final SoulStateType<HolySoul.Info> HOLY_SOUL_INFO =
            register("holy_state", HolySoul.Info::new);

    public static final SoulStateType<GhostSoul.Info> GHOST_SOUL_INFO =
            register("ghost_state", GhostSoul.Info::new);

    public static final SoulStateType<ForbiddenSoul.Info> FORBIDDEN_SOUL_INFO =
            register("forbidden_state", ForbiddenSoul.Info::new);

    public static final SoulStateType<MithrilSoul.Info> MITHRIL_SOUL_INFO =
            register("mithril_state", MithrilSoul.Info::new);

    public static final SoulStateType<OrichalcumSoul.Info> ORICHALCUM_SOUL_INFO =
            register("orichalcum_state", OrichalcumSoul.Info::new);

    public static final SoulStateType<PalladiumSoul.Info> PALLADIUM_SOUL_INFO =
            register("palladium_state", PalladiumSoul.Info::new);

    public static final SoulStateType<TitaniumSoul.Info> TITANIUM_SOUL_INFO =
            register("titanium_state", TitaniumSoul.Info::new);

    public static final SoulStateType<RedRidingSoul.Info> RED_RIDING_SOUL_INFO =
            register("red_riding_state", RedRidingSoul.Info::new);

    public static final SoulStateType<BeetleSoul.Info> BEETLE_SOUL_INFO =
            register("beetle_state", BeetleSoul.Info::new);

    private static <T extends SoulState> SoulStateType<T> register(String name, Supplier<T> factory) {
        return register(name, factory, false);
    }

    @SuppressWarnings("unchecked")
    private static <T extends SoulState> SoulStateType<T> register(String name, Supplier<T> factory, boolean clientSide) {
        return (SoulStateType<T>) FSFRegisters.SOUL_INFO_TYPE.register(FSF.rl(name),
                new SoulStateType<>(factory, clientSide));
    }

    public static void init() {
        // Registered by the field initializers above.
    }
}

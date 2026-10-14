package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FargoSoul;
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

public class FargoSoulSoulInfoRegister {

    public static final SoulInfoType<SprintSoulInfo> SPRINT_SOUL_INFO =
            register("sprint_soul_info", SprintSoulInfo::new, true);
    public static final SoulInfoType<FlySoulInfo> FLY_SOUL_INFO =
            register("fly_soul_info", FlySoulInfo::new, true);
    public static final SoulInfoType<ProjectileInfo> PROJECTILE_INFO =
            register("projectile_info", ProjectileInfo::new, true);

    public static final SoulInfoType<CosmicPower.Info> COSMIC_POWER_INFO =
            register("cosmic_power_info", CosmicPower.Info::new);
    public static final SoulInfoType<BlazeSoul.Info> BLAZE_SOUL_INFO =
            register("blaze_soul_info", BlazeSoul.Info::new);
    public static final SoulInfoType<NebulaSoul.Info> NEBULA_SOUL_INFO =
            register("nebula_soul_info", NebulaSoul.Info::new);
    public static final SoulInfoType<StardustSoul.Info> STARDUST_SOUL_INFO =
            register("stardust_soul_info", StardustSoul.Info::new);
    public static final SoulInfoType<VortexSoul.Info> VORTEX_SOUL_INFO =
            register("vortex_soul_info", VortexSoul.Info::new);

    public static final SoulInfoType<DarkArtistSoul.Info> DARK_ARTIST_SOUL_INFO =
            register("dark_artist_soul_info", DarkArtistSoul.Info::new);

    public static final SoulInfoType<NecromancerSoul.Info> NECROMANCER_SOUL_INFO =
            register("necromancer_soul_info", NecromancerSoul.Info::new);

    public static final SoulInfoType<PenetratingSoulInfo> PENETRATING_SOUL_INFO =
            register("penetrating_soul_info", PenetratingSoulInfo::new);

    public static final SoulInfoType<NinjaSoul.Info> NINJA_SOUL_INFO =
            register("ninja_soul_info", NinjaSoul.Info::new);

    public static final SoulInfoType<CopperSoul.Info> COPPER_SOUL_INFO =
            register("copper_soul_info", CopperSoul.Info::new);

    public static final SoulInfoType<IronSoul.Info> IRON_SOUL_INFO =
            register("iron_soul_info", IronSoul.Info::new);

    public static final SoulInfoType<SilverSoul.Info> SILVER_SOUL_INFO =
            register("silver_soul_info", SilverSoul.Info::new);

    public static final SoulInfoType<TinSoul.Info> TIN_SOUL_INFO =
            register("tin_soul_info", TinSoul.Info::new);

    public static final SoulInfoType<TerraPower.Info> TERRA_POWER_INFO =
            register("terra_power_info", TerraPower.Info::new);

    public static final SoulInfoType<WoodSoul.Info> WOOD_SOUL_INFO =
            register("wood_soul_info", WoodSoul.Info::new);

    public static final SoulInfoType<RoseWoodSoul.Info> ROSE_WOOD_SOUL_INFO =
            register("rose_wood_soul_info", RoseWoodSoul.Info::new);

    public static final SoulInfoType<EbonyWoodSoul.Info> EBONY_WOOD_SOUL_INFO =
            register("ebony_wood_soul_info", EbonyWoodSoul.Info::new);

    public static final SoulInfoType<ShadowWoodSoul.Info> SHADOW_WOOD_SOUL_INFO =
            register("shadow_wood_soul_info", ShadowWoodSoul.Info::new);

    public static final SoulInfoType<PearlWoodSoul.Info> PEARL_WOOD_SOUL_INFO =
            register("pearl_wood_soul_info", PearlWoodSoul.Info::new);

    public static final SoulInfoType<ForestPower.Info> FOREST_POWER_INFO =
            register("forest_power_info", ForestPower.Info::new);

    public static final SoulInfoType<EarthPower.Info> EARTH_POWER_INFO =
            register("earth_power_info", EarthPower.Info::new);

    public static final SoulInfoType<LifePower.Info> LIFE_POWER_INFO =
            register("life_power_info", LifePower.Info::new);

    public static final SoulInfoType<WillPower.Info> WILL_POWER_INFO =
            register("will_power_info", WillPower.Info::new);

    public static final SoulInfoType<CobaltSoul.Info> COBALT_SOUL_INFO =
            register("cobalt_soul_info", CobaltSoul.Info::new);

    public static final SoulInfoType<FrostSoul.Info> FROST_SOUL_INFO =
            register("frost_soul_info", FrostSoul.Info::new);

    public static final SoulInfoType<CrimsonSoul.Info> CRIMSON_SOUL_INFO =
            register("crimson_soul_info", CrimsonSoul.Info::new);

    public static final SoulInfoType<PlatinumSoul.Info> PLATINUM_SOUL_INFO =
            register("platinum_soul_info", PlatinumSoul.Info::new);

    public static final SoulInfoType<GoldSoul.Info> GOLD_SOUL_INFO =
            register("gold_soul_info", GoldSoul.Info::new);

    public static final SoulInfoType<RainCloudSoul.Info> RAIN_CLOUD_SOUL_INFO =
            register("rain_cloud_soul_info", RainCloudSoul.Info::new);

    public static final SoulInfoType<HolySoul.Info> HOLY_SOUL_INFO =
            register("holy_soul_info", HolySoul.Info::new);

    public static final SoulInfoType<GhostSoul.Info> GHOST_SOUL_INFO =
            register("ghost_soul_info", GhostSoul.Info::new);

    public static final SoulInfoType<ForbiddenSoul.Info> FORBIDDEN_SOUL_INFO =
            register("forbidden_soul_info", ForbiddenSoul.Info::new);

    public static final SoulInfoType<MithrilSoul.Info> MITHRIL_SOUL_INFO =
            register("mithril_soul_info", MithrilSoul.Info::new);

    public static final SoulInfoType<OrichalcumSoul.Info> ORICHALCUM_SOUL_INFO =
            register("orichalcum_soul_info", OrichalcumSoul.Info::new);

    public static final SoulInfoType<PalladiumSoul.Info> PALLADIUM_SOUL_INFO =
            register("palladium_soul_info", PalladiumSoul.Info::new);

    public static final SoulInfoType<TitaniumSoul.Info> TITANIUM_SOUL_INFO =
            register("titanium_soul_info", TitaniumSoul.Info::new);

    public static final SoulInfoType<RedRidingSoul.Info> RED_RIDING_SOUL_INFO =
            register("red_riding_soul_info", RedRidingSoul.Info::new);

    public static final SoulInfoType<BeetleSoul.Info> BEETLE_SOUL_INFO =
            register("beetle_soul_info", BeetleSoul.Info::new);

    private static <T extends SoulInfo> SoulInfoType<T> register(String name, Supplier<T> factory) {
        return register(name, factory, false);
    }

    @SuppressWarnings("unchecked")
    private static <T extends SoulInfo> SoulInfoType<T> register(String name, Supplier<T> factory, boolean clientSide) {
        return (SoulInfoType<T>) FargoSoulRegisters.SOUL_INFO_TYPE.register(FargoSoul.rl(name),
                new SoulInfoType<>(factory, clientSide));
    }

    public static void init() {
        // Registered by the field initializers above.
    }
}

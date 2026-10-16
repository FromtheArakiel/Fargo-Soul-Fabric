package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.item.TerraSoul;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.earthPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.forestPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.lifePower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.spiritPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower.*;
import dev.arakiel.fargosoulfabric.common.item.terraSoul.willPower.*;
import dev.arakiel.fargosoulfabric.common.component.FSFRarity;
import dev.arakiel.fargosoulfabric.register.FSFItemBuilder;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.function.Function;

public class FSFItemRegister {

    public static final TerraSoul TerraSoulItem =
            register("terra_soul", TerraSoul::new, FSFRarity.Rainbow)
                    .recipeWithLookup((provider, output) -> shapeless(output, "terra_soul", FSFItemRegister.TerraSoulItem, FSFItemRegister.CosmicPowerItem, FSFItemRegister.DeathPowerItem, FSFItemRegister.TerraPowerItem, FSFItemRegister.ForestPowerItem, FSFItemRegister.EarthPowerItem, FSFItemRegister.NaturePowerItem, FSFItemRegister.LifePowerItem, FSFItemRegister.SpiritPowerItem, FSFItemRegister.WillPowerItem))
                    .itemLanguage("Terra Soul", "泰拉之魂")
                    .itemLanguageTooltip(-1, " ", "“泰拉之主，天地共证”")
                    .itemTag(FSFItemTagsRegister.SectionSoul)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final CosmicPower CosmicPowerItem =
            register("cosmic_power", CosmicPower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "cosmic_power", FSFItemRegister.CosmicPowerItem, FSFItemRegister.BlazeSoulItem, FSFItemRegister.METEOR_SOUL, FSFItemRegister.NebulaSoulItem, FSFItemRegister.StardustSoulItem, FSFItemRegister.VortexSoulItem, FSFItemRegister.WizardSoulItem))
                    .itemLanguage("Cosmic Power", "宇宙之力")
                    .itemLanguageTooltip(1, "Absorb 1 Cosmic Energy per second from the world, up to 100 stored", "每秒从世界中汲取1点宇宙能量，最多积累100点宇宙能量")
                    .itemLanguageTooltip(2, "Damage dealt up to +80% and damage taken up to -20% based on stored energy", "根据积累比例，造成的伤害最多提升80%，受到的伤害最多减少20%")
                    .itemLanguageTooltip(3, "When taking damage exceeding 50% of your max HP, spend 50 Cosmic Energy to reduce it by 50%", "受到伤害时，如果伤害超过你最大生命值的50%，则尝试消耗50宇宙能量使伤害减少50%")
                    .itemLanguageTooltip(4, "On death, erupt in a supernova dealing magic damage equal to 3600% of your max HP", "死亡时释放超新星爆炸，对周围敌人造成你最大生命值3600%的魔法伤害")
                    .itemLanguageTooltip(-1, "\"Existing since the beginning of the universe\"", "“自宇宙大爆炸以来就一直存在”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final BlazeSoul BlazeSoulItem =
            register("blaze_soul", BlazeSoul::new, 0xFF691F)
                    .jeiInfo(1, "Obtained from killing: Blaze (1%)", "获取：击杀烈焰人(1%)")
                    .lootTable(vanilla("entities/blaze"), table -> pool(FSFItemRegister.BlazeSoulItem, 0.01F))
                    .itemLanguage("Blaze Enchantment", "耀斑魔石")
                    .itemLanguageTooltip(1, "Attacks build Solar Energy equal to 15% of damage dealt (up to 1000); spending 1 per second heals 1% of max HP", "攻击时积累相当于15%伤害值的日耀能量，最多积累1000点日耀能量，每秒消耗1点日耀能量恢复你1%最大生命值")
                    .itemLanguageTooltip(2, "A Solar Shield forms as energy builds, reducing damage taken by up to 40%", "日耀能量积累比例会产生日曜护盾，使你受到的伤害最多减少40%")
                    .itemLanguageTooltip(3, "Above 300 energy, gain the Solar Dash; passing through enemies causes Solar Eruption dealing fire damage equal to 12% of your max HP", "日耀能量超过300点时，赋予你日耀冲刺，穿过敌人时造成日耀喷发，对敌人造成你12%最大生命值的火焰伤害")
                    .itemLanguageTooltip(4, "Above 600 energy, dash speed is increased by 100% and Solar Eruption damage by 120%", "日耀能量超过600点时，冲刺初速度提升100%，日耀喷发的伤害提升120%")
                    .itemLanguageTooltip(5, "Above 900 energy, nearby enemies take fire damage equal to 4% of your max HP each second and are set ablaze", "日耀能量超过900点时，附近的敌人每秒会受到你4%最大生命值的火焰伤害并被点燃")
                    .itemLanguageTooltip(6, "Above 900 energy, press the Solar key to unleash Solar Force, draining 20 extra energy per second until empty", "日耀能量大于900时，按下“日耀”键会释放日耀之力，每秒额外消耗20点日耀能量，日耀能量耗尽时会失去日耀能量")
                    .itemLanguageTooltip(7, "While in Solar Force, spend 50 energy when dealing damage to boost it by 375%", "日耀之力期间，造成伤害时消耗50日耀能量使伤害提升375%")
                    .itemLanguageTooltip(8, "While in Solar Force, the shield's damage reduction doubles and extra damage is reduced by an amount equal to 15% of your max HP", "日耀之力期间，日曜护盾减伤效果翻倍，并使受到的伤害降低，降低值相当于你15%最大生命值")
                    .itemLanguageTooltip(9, "While in Solar Force, attacks do not build Solar Energy", "日耀之力期间，攻击不会积累日耀能量")
                    .itemLanguageTooltip(-1, "\"A soul that burns to the touch\"", "“烫手魔石”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final MeteorSoul METEOR_SOUL =
            register("meteor_soul", MeteorSoul::new, 0x654A52)
                    .jeiInfo(1, "Obtained from killing: Phantom (1%)", "获取：击杀幻翼(1%)")
                    .jeiInfo(2, "Found in: Nether Fortress (15%)", "获取：开启下界要塞(15%)")
                    .lootTable(vanilla("entities/phantom"), table -> pool(FSFItemRegister.METEOR_SOUL, 0.01F))
                    .lootTable(vanilla("chests/nether_bridge"), table -> pool(FSFItemRegister.METEOR_SOUL, 0.15F))
                    .itemLanguage("Meteor Enchantment", "流星魔石")
                    .itemLanguageTooltip(1, "Press the sneak key to greatly increase falling speed", "按下潜行键大幅增加下落速度")
                    .itemLanguageTooltip(2, "Deal up to +125% damage based on current movement speed", "根据当前移动速度，造成的伤害最高提升至125%")
                    .itemLanguageTooltip(3, "10% chance to summon a meteor that strikes enemies on attack", "攻击时有10%概率召唤流星攻击敌人")
                    .itemLanguageTooltip(4, "Immune to fall damage", "免疫摔落伤害")
                    .itemLanguageTooltip(-1, "\"Drop a draco on 'em\"", "《Drop a draco on 'em》")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final NebulaSoul NebulaSoulItem =
            register("nebula_soul", NebulaSoul::new, 0xFF7AE7)
                    .jeiInfo(1, "Obtained from killing: Shulker (1%)", "获取：击杀潜影贝(1%)")
                    .jeiInfo(2, "Found in: End City Treasure (20%)", "获取：开启末地城宝藏(20%)")
                    .lootTable(vanilla("entities/shulker"), table -> pool(FSFItemRegister.NebulaSoulItem, 0.01F))
                    .lootTable(vanilla("chests/end_city_treasure"), table -> pool(FSFItemRegister.NebulaSoulItem, 0.2F))
                    .itemLanguage("Nebula Enchantment", "星云魔石")
                    .itemLanguageTooltip(1, "Deal bonus damage equal to 4% of your max HP and build Nebula Energy equal to 15% of damage dealt", "造成伤害的伤害提升，提升值相当于你4%最大生命值，并且会积累相当于15%伤害值的星云能量")
                    .itemLanguageTooltip(2, "Spend 10% of current Nebula Energy per second to heal yourself", "每秒消耗当前10%星云能量为你恢复生命值")
                    .itemLanguageTooltip(-1, "\"The pillar of genesis watches over you\"", "“创生之柱照耀着你”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final StardustSoul StardustSoulItem =
            register("stardust_soul", StardustSoul::new, 0x00ADF1)
                    .jeiInfo(1, "Obtained from killing: Enderman (1%)", "获取：击杀末影人(1%)")
                    .jeiInfo(2, "Found in: End City Treasure (15%)", "获取：开启末地城宝藏(15%)")
                    .lootTable(vanilla("entities/enderman"), table -> pool(FSFItemRegister.StardustSoulItem, 0.01F))
                    .lootTable(vanilla("chests/end_city_treasure"), table -> pool(FSFItemRegister.StardustSoulItem, 0.15F))
                    .itemLanguage("Stardust Enchantment", "星尘魔石")
                    .itemLanguageTooltip(1, "Press the Freeze key to stop time for 6 seconds, with a 120-second cooldown", "按下“冻结”键后会冻结时间，持续6秒，该效果有120秒冷却时间")
                    .itemLanguageTooltip(2, "While time is frozen, damage dealt is increased to 200%", "时间冻结期间，造成的伤害提升至200%")
                    .itemLanguageTooltip(-1, "\"You have become a Stand User\"", "“你成为了替身使者”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final VortexSoul VortexSoulItem =
            register("vortex_soul", VortexSoul::new, 0x00F4A8)
                    .jeiInfo(1, "Obtained from killing: Enderman (1%)", "获取：击杀末影人(1%)")
                    .jeiInfo(2, "Found in: End City Treasure (10%)", "获取：开启末地城宝藏(10%)")
                    .lootTable(vanilla("entities/enderman"), table -> pool(FSFItemRegister.VortexSoulItem, 0.01F))
                    .lootTable(vanilla("chests/end_city_treasure"), table -> pool(FSFItemRegister.VortexSoulItem, 0.1F))
                    .itemLanguage("Vortex Enchantment", "星旋魔石")
                    .itemLanguageTooltip(1, "Press the Teleport key to blink up to 512 blocks away (any farther fails), with a 20-second cooldown", "按下“传送”键传送，最大传送距离为512格，大于此距离无法传送，传送冷却时间为20秒")
                    .itemLanguageTooltip(2, "Summon a vortex at the destination that pulls in and damages nearby enemies for 5 seconds", "在传送位置召唤一个漩涡持续吸引并伤害周围敌人，持续5秒")
                    .itemLanguageTooltip(-1, "\"Tearing reality apart\"", "“撕裂现实”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final WizardSoul WizardSoulItem =
            register("wizard_soul", WizardSoul::new, FSFRarity.Blue)
                    .jeiInfo(1, "Obtained from killing: Witch (1%)", "获取：击杀女巫(1%)")
                    .lootTable(vanilla("entities/witch"), table -> pool(FSFItemRegister.WizardSoulItem, 0.01F))
                    .itemLanguage("Wizard Enchantment", "巫师魔石")
                    .itemLanguageTooltip(1, "Increases magic damage dealt by 60%", "造成的魔法伤害提升60%")
                    .itemLanguageTooltip(-1, "\"We love casting spells!\"", "“我们爱施放魔法!”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final DeathPower DeathPowerItem =
            register("death_power", DeathPower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "death_power", FSFItemRegister.DeathPowerItem, FSFItemRegister.AncientShadowSoulItem, FSFItemRegister.NinjaSoulItem, FSFItemRegister.CrystalAssassinSoulItem, FSFItemRegister.DarkArtistSoulItem, FSFItemRegister.GloomySoulItem, FSFItemRegister.NecromancerSoulItem, FSFItemRegister.PenetratingNinjaSoulItem))
                    .itemLanguage("Death Power", "死亡之力")
                    .itemLanguageTooltip(1, "Grants the Shadow Dash with 50% extra starting speed", "赋予暗影冲刺，冲刺初速度提升50%")
                    .itemLanguageTooltip(2, "Shadow Dash applies Death Mark for 10s: -20% max HP, -40% armor, -10% movement speed", "暗影冲刺冲过敌人施加10秒死亡标记效果，死亡标记减少20%最大生命值，减少40%护甲，减少10%移动速度")
                    .itemLanguageTooltip(3, "+200% damage against Death Marked enemies", "对被死亡标记影响的敌人，造成的伤害提升200%")
                    .itemLanguageTooltip(4, "Against enemies below 12% max HP, deal bonus damage equal to up to 25% of their missing health", "对低于最大生命值12%的敌人，造成的伤害提升，提升值至相当于25%敌人已损生命值")
                    .itemLanguageTooltip(5, "Immune to Death Mark", "免疫死亡标记")
                    .itemLanguageTooltip(-1, "\"Darkness, darker, yet darker\"", "“黑暗，更黑暗，还是更黑暗”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final AncientShadowSoul AncientShadowSoulItem =
            register("ancient_shadow_soul", AncientShadowSoul::new, 0x5E55DC)
                    .jeiInfo(1, "Obtained from killing: Warden (1%)", "获取：击杀守卫者(1%)")
                    .jeiInfo(2, "Found in: Ancient City (20%)", "获取：开启远古城市(20%)")
                    .lootTable(vanilla("entities/warden"), table -> pool(FSFItemRegister.AncientShadowSoulItem, 0.01F))
                    .lootTable(vanilla("chests/ancient_city"), table -> pool(FSFItemRegister.AncientShadowSoulItem, 0.2F))
                    .itemLanguage("Ancient Shadow Enchantment", "远古暗影魔石")
                    .itemLanguageTooltip(1, "20% chance to inflict Darkness for 5 seconds on attack", "攻击时，有20%概率为目标施加5秒黑暗效果")
                    .itemLanguageTooltip(2, "Summon three magic shadow orbs that orbit you and provide light", "召唤三颗魔法暗影球围绕你旋转，提供照明效果")
                    .itemLanguageTooltip(3, "Immune to Darkness", "免疫黑暗")
                    .itemLanguageTooltip(-1, "\"Ancient, yet remarkably practical\"", "“十分古老，却非常实用”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final NinjaSoul NinjaSoulItem =
            register("ninja_soul", NinjaSoul::new, 0x48494D)
                    .jeiInfo(1, "Obtained from killing: Pillager (1%)", "获取：击杀掠夺者(1%)")
                    .jeiInfo(2, "Found in: Pillager Outpost (15%)", "获取：开启掠夺者前哨站(15%)")
                    .lootTable(vanilla("entities/pillager"), table -> pool(FSFItemRegister.NinjaSoulItem, 0.01F))
                    .lootTable(vanilla("chests/pillager_outpost"), table -> pool(FSFItemRegister.NinjaSoulItem, 0.15F))
                    .itemLanguage("Ninja Enchantment", "忍者魔石")
                    .itemLanguageTooltip(1, "While not attacking, build 20 Ambush Energy per second, up to 600", "不攻击时，每秒积累20点伏击能量，最多积累600点伏击能量")
                    .itemLanguageTooltip(2, "Each point of Ambush Energy grants +1% damage; attacking spends it all", "每点伏击能量使伤害提升1%，攻击后清空伏击能量")
                    .itemLanguageTooltip(-1, "\"Waiting for the right moment...\"", "“等待正确的时机……”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final CrystalAssassinSoul CrystalAssassinSoulItem =
            register("crystal_assassin_soul", CrystalAssassinSoul::new, 0xCF249D)
                    .jeiInfo(1, "Obtained from killing: Breeze (1%)", "获取：击杀旋风人(1%)")
                    .lootTable(vanilla("entities/breeze"), table -> pool(FSFItemRegister.CrystalAssassinSoulItem, 0.01F))
                    .itemLanguage("Crystal Assassin Enchantment", "水晶刺客魔石")
                    .itemLanguageTooltip(1, "Grants the Crystal Dash", "赋予水晶冲刺")
                    .itemLanguageTooltip(2, "After dashing, gain 1 second of Preemptive Strike: next attack +60% damage and applies Crystal Shatter, reducing target defense by 20", "冲刺后，赋予你1秒先发至人效果，使下次攻击造成的伤害提升60%并施加水晶碎甲效果，降低目标20点防御力")
                    .itemLanguageTooltip(-1, "\"Reaching the summit\"", "“登顶”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final DarkArtistSoul DarkArtistSoulItem =
            register("dark_artist_soul", DarkArtistSoul::new, 0x9C59B2)
                    .jeiInfo(1, "Obtained from killing: Evoker (1%)", "获取：击杀唤魔者(1%)")
                    .jeiInfo(2, "Found in: Woodland Mansion (10%)", "获取：开启林地府邸(10%)")
                    .lootTable(vanilla("entities/evoker"), table -> pool(FSFItemRegister.DarkArtistSoulItem, 0.01F))
                    .lootTable(vanilla("chests/woodland_mansion"), table -> pool(FSFItemRegister.DarkArtistSoulItem, 0.1F))
                    .itemLanguage("Dark Artist Enchantment", "暗黑艺术家魔石")
                    .itemLanguageTooltip(1, "+40% damage vs Shadowflame-affected enemies and absorb Dark Energy equal to 20% of damage dealt (max 400)", "对被暗影焰影响的敌人，造成的伤害提升40%，并吸收相当于20%伤害值的暗黑能量，最多吸收400点暗黑能量")
                    .itemLanguageTooltip(2, "At max Dark Energy, spend it all to gain 20s of Shadow Gift: +40% max HP, +15% speed, +40% attack damage", "暗黑能量达到最大值时，消耗全部暗黑能量，使你获得20秒暗影之赐效果，最大生命值提升40%，移动速度提升15%，攻击力提升40%")
                    .itemLanguageTooltip(3, "While under Shadow Gift, 20% chance to dodge harmful effects, restoring 12% max HP", "在暗影之赐效果下，有20%概率闪避负面效果，闪避后恢复你12%最大生命值")
                    .itemLanguageTooltip(-1, "\"What lurks in shadow is far more than it seems\"", "“阴影蕴含之物远超其表象”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final GloomySoul GloomySoulItem =
            register("gloomy_soul", GloomySoul::new, 0x644D75)
                    .jeiInfo(1, "Obtained from killing: Enderman (1%)", "获取：击杀末影人(1%)")
                    .jeiInfo(2, "Found in: Ancient City (15%)", "获取：开启远古城市(15%)")
                    .lootTable(vanilla("entities/enderman"), table -> pool(FSFItemRegister.GloomySoulItem, 0.01F))
                    .lootTable(vanilla("chests/ancient_city"), table -> pool(FSFItemRegister.GloomySoulItem, 0.15F))
                    .itemLanguage("Gloomy Enchantment", "阴森魔石")
                    .itemLanguageTooltip(1, "Attacks inflict Shadowflame for 10 seconds", "攻击施加10秒暗影焰效果")
                    .itemLanguageTooltip(2, "Attacks against Shadowflame-affected enemies have a 5% chance to apply Death Mark for 2 seconds", "对被暗影焰影响的敌人，攻击有5%概率施加2秒死亡标记效果")
                    .itemLanguageTooltip(-1, "\"A soul that has been melting since 1902\"", "“自1902年以来融化的灵魂”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final NecromancerSoul NecromancerSoulItem =
            register("necromancer_soul", NecromancerSoul::new, 0x565642)
                    .jeiInfo(1, "Obtained from killing: Skeleton (1%), Wither Skeleton (1%)", "获取：击杀骷髅(1%)、凋零骷髅(1%)")
                    .lootTable(vanilla("entities/skeleton"), table -> pool(FSFItemRegister.NecromancerSoulItem, 0.01F))
                    .lootTable(vanilla("entities/wither_skeleton"), table -> pool(FSFItemRegister.NecromancerSoulItem, 0.01F))
                    .itemLanguage("Necromancer Enchantment", "死灵魔石")
                    .itemLanguageTooltip(1, "Killing undead grants 1 Necrotic Energy (max 100)", "击杀亡灵生物掠夺1点死灵能量，死灵能量最大值为100点")
                    .itemLanguageTooltip(2, "On death with full Necrotic Energy, spend it all to revive with 20% max HP", "死亡时，如果死灵能量达到最大值，则消耗全部死灵能量使你复活，并恢复20%最大生命值")
                    .itemLanguageTooltip(3, "Killing skeletons or wither skeletons may drop their heads and grants 2 extra Necrotic Energy", "击杀骷髅或凋零骷髅额外掉落其头颅，并额外掠夺2点死灵能量")
                    .itemLanguageTooltip(-1, "\"Welcome to the bone zone\"", "“欢迎来到骸骨领域”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final PenetratingNinjaSoul PenetratingNinjaSoulItem =
            register("penetrating_ninja_soul", PenetratingNinjaSoul::new, 0x945B0A)
                    .jeiInfo(1, "Obtained from killing: Vindicator (1%)", "获取：击杀卫道士(1%)")
                    .lootTable(vanilla("entities/vindicator"), table -> pool(FSFItemRegister.PenetratingNinjaSoulItem, 0.01F))
                    .itemLanguage("Penetrating Ninja Enchantment", "渗透忍者魔石")
                    .itemLanguageTooltip(1, "Grants the Penetrating Dash with 25% extra starting speed", "赋予渗透冲刺，冲刺初速度提升25%")
                    .itemLanguageTooltip(2, "Press the Penetrate key: your next dash makes you immune and able to pass through walls for 1 second, 30s cooldown", "按下“渗透”键，可以使下一次冲刺后的1秒内免疫一切伤害并可以穿过墙壁，该效果有30秒冷却时间")
                    .itemLanguageTooltip(-1, "\"A village hidden in the walls\"", "“藏匿于墙中的村庄”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final TerraPower TerraPowerItem =
            register("terra_power", TerraPower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "terra_power", FSFItemRegister.TerraPowerItem, FSFItemRegister.CopperSoulItem, FSFItemRegister.TinSoulItem, FSFItemRegister.IronSoulItem, FSFItemRegister.LeadSoulItem, FSFItemRegister.SilverSoulItem, FSFItemRegister.TungstenSoulItem, FSFItemRegister.ObsidianSoulItem))
                    .itemLanguage("Terra Power", "泰拉之力")
                    .itemLanguageTooltip(1, "20% chance to release 3-6 thunder orbs that shock nearby enemies and apply Lead Poisoning, 1s cooldown", "攻击时有20%概率释放3~6颗雷电球，雷电球会电击附近的敌人并施加铅中毒，该效果有1秒冷却时间")
                    .itemLanguageTooltip(2, "More active orbs raise the release chance and shorten the cooldown, up to +40% chance and -60% cooldown", "在场的雷电球越多，释放雷电球的概率越高，冷却越短，概率最多提升40%，冷却时间最多降低60%")
                    .itemLanguageTooltip(3, "5% chance for +100% damage and 5 seconds of Terra Resonance (+20% orb chance, +80% orb damage)", "造成伤害时有5%概率使伤害提升100%，并获得5秒泰拉共鸣效果，使释放雷电球的概率提升20%，雷电球造成的伤害提升80%")
                    .itemLanguageTooltip(4, "Damage taken is reduced by [4 + 2% of your max HP]", "受到的伤害减少，减少值相当于[4+你2%最大生命值]")
                    .itemLanguageTooltip(-1, "\"The earth grants it strength\"", "“大地赐予它力量”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final CopperSoul CopperSoulItem =
            register("copper_soul", CopperSoul::new, 0xD56617)
                    .jeiInfo(1, "Obtained from killing: Drowned (1%)", "获取：击杀溺尸(1%)")
                    .jeiInfo(2, "Found in: Abandoned Mineshaft (8%)", "获取：开启废弃矿井(8%)")
                    .lootTable(vanilla("entities/drowned"), table -> pool(FSFItemRegister.CopperSoulItem, 0.01F))
                    .lootTable(vanilla("chests/abandoned_mineshaft"), table -> pool(FSFItemRegister.CopperSoulItem, 0.08F))
                    .itemLanguage("Copper Enchantment", "铜魔石")
                    .itemLanguageTooltip(1, "10% chance to release a thunder orb that shocks nearby enemies, 5s cooldown", "攻击时有10%概率释放雷电球，雷电球会电击附近的敌人，该效果有5秒冷却时间")
                    .itemLanguageTooltip(2, "+20% chance to release lightning when attacking targets in rain or water", "攻击雨中或水中的目标时释放闪电的概率增加20%")
                    .itemLanguageTooltip(-1, "\"Its music is still electronic\"", "“它的音乐还是电音”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final TinSoul TinSoulItem =
            register("tin_soul", TinSoul::new, 0xA28B4E)
                    .jeiInfo(1, "Obtained from killing: Skeleton (1%)", "获取：击杀骷髅(1%)")
                    .jeiInfo(2, "Found in: Desert Pyramid (10%)", "获取：开启沙漠神殿(10%)")
                    .lootTable(vanilla("entities/skeleton"), table -> pool(FSFItemRegister.TinSoulItem, 0.01F))
                    .lootTable(vanilla("chests/desert_pyramid"), table -> pool(FSFItemRegister.TinSoulItem, 0.1F))
                    .itemLanguage("Tin Enchantment", "锡魔石")
                    .itemLanguageTooltip(1, "+20% critical damage", "暴击伤害提升20%")
                    .itemLanguageTooltip(2, "Each critical hit grants +10% bonus critical damage, up to 80%", "每次暴击时都会增加10%额外暴击伤害，额外暴击伤害的最大值为80%")
                    .itemLanguageTooltip(3, "Taking damage halves your bonus critical damage", "受到伤害会使额外暴击伤害减半")
                    .itemLanguageTooltip(-1, "\"Crits are back\"", "“暴击回归”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final IronSoul IronSoulItem =
            register("iron_soul", IronSoul::new, 0x988E83)
                    .jeiInfo(1, "Obtained from killing: Iron Golem (1%)", "获取：击杀铁傀儡(1%)")
                    .jeiInfo(2, "Found in: Abandoned Mineshaft (10%)", "获取：开启废弃矿井(10%)")
                    .lootTable(vanilla("entities/iron_golem"), table -> pool(FSFItemRegister.IronSoulItem, 0.01F))
                    .lootTable(vanilla("chests/abandoned_mineshaft"), table -> pool(FSFItemRegister.IronSoulItem, 0.1F))
                    .itemLanguage("Iron Enchantment", "铁魔石")
                    .itemLanguageTooltip(1, "Attracts nearby items to you", "吸引周围的物品")
                    .itemLanguageTooltip(2, "-20% damage taken for 5 seconds after picking up an item", "拾取物品后的5秒内受到的伤害减少20%")
                    .itemLanguageTooltip(-1, "\"Strike while the iron is hot\"", "“趁热打铁”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final LeadSoul LeadSoulItem =
            register("lead_soul", LeadSoul::new, 0x46485D)
                    .jeiInfo(1, "Obtained from killing: Cave Spider (1%)", "获取：击杀洞穴蜘蛛(1%)")
                    .lootTable(vanilla("entities/cave_spider"), table -> pool(FSFItemRegister.LeadSoulItem, 0.01F))
                    .itemLanguage("Lead Enchantment", "铅魔石")
                    .itemLanguageTooltip(1, "Attacks apply Lead Poisoning for 5 seconds", "攻击施加5秒铅中毒")
                    .itemLanguageTooltip(2, "Lead-poisoned enemies spread the effect to nearby creatures", "被铅中毒影响的敌人会向周围的敌人传染铅中毒")
                    .itemLanguageTooltip(3, "Immune to Lead Poisoning", "免疫铅中毒")
                    .itemLanguageTooltip(-1, "\"Not recommended for consumption\"", "“不建议食用”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final SilverSoul SilverSoulItem =
            register("silver_soul", SilverSoul::new, 0xB4B4CD)
                    .jeiInfo(1, "Obtained from killing: Silverfish (1%)", "获取：击杀蠹虫(1%)")
                    .jeiInfo(2, "Found in: Dungeon (10%)", "获取：开启地牢(10%)")
                    .lootTable(vanilla("entities/silverfish"), table -> pool(FSFItemRegister.SilverSoulItem, 0.01F))
                    .lootTable(vanilla("chests/simple_dungeon"), table -> pool(FSFItemRegister.SilverSoulItem, 0.1F))
                    .itemLanguage("Silver Enchantment", "银魔石")
                    .itemLanguageTooltip(1, "Blocking within 0.2-0.4s of raising a shield reflects 200% damage and grants 1s of Amazing Moment (+150% damage), 3s cooldown", "举盾后0.2~0.4秒抵挡攻击会反弹敌人200%伤害，并赋予你1秒惊人一刻，伤害提升150%，该效果有3秒冷却时间")
                    .itemLanguageTooltip(2, "Invulnerable for 0.4 seconds after reflecting damage", "反弹伤害后的0.4秒内无敌")
                    .itemLanguageTooltip(-1, "\"Reflection\"", "“反射”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final TungstenSoul TungstenSoulItem =
            register("tungsten_soul", TungstenSoul::new, 0xB0D2B2)
                    .jeiInfo(1, "Obtained from killing: Ravager (1%)", "获取：击杀劫掠兽(1%)")
                    .jeiInfo(2, "Found in: Bastion Treasure (5%)", "获取：开启堡垒遗迹(5%)")
                    .lootTable(vanilla("entities/ravager"), table -> pool(FSFItemRegister.TungstenSoulItem, 0.01F))
                    .lootTable(vanilla("chests/bastion_treasure"), table -> pool(FSFItemRegister.TungstenSoulItem, 0.05F))
                    .itemLanguage("Tungsten Enchantment", "钨魔石")
                    .itemLanguageTooltip(1, "+50% attack reach", "攻击距离提升50%")
                    .itemLanguageTooltip(-1, "\"Bigger is better\"", "“大就是好”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final ObsidianSoul ObsidianSoulItem =
            register("obsidian_soul", ObsidianSoul::new, 0x453C75)
                    .jeiInfo(1, "Obtained from killing: Magma Cube (1%)", "获取：击杀岩浆怪(1%)")
                    .jeiInfo(2, "Found in: Nether Fortress (10%)", "获取：开启下界要塞(10%)")
                    .lootTable(vanilla("entities/magma_cube"), table -> pool(FSFItemRegister.ObsidianSoulItem, 0.01F))
                    .lootTable(vanilla("chests/nether_bridge"), table -> pool(FSFItemRegister.ObsidianSoulItem, 0.1F))
                    .itemLanguage("Obsidian Enchantment", "黑曜石魔石")
                    .itemLanguageTooltip(1, "Gain better vision in fire or lava", "在火焰或熔岩中时获得更好的视野")
                    .itemLanguageTooltip(2, "Reduces damage taken by 1 point", "受到的伤害减少1点")
                    .itemLanguageTooltip(3, "Immune to fire damage", "免疫火焰伤害")
                    .itemLanguageTooltip(4, "Immune to lava damage", "免疫熔岩伤害")
                    .itemLanguageTooltip(-1, "\"The earth calls\"", "“大地在呼唤”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final ForestPower ForestPowerItem =
            register("forest_power", ForestPower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "forest_power", FSFItemRegister.ForestPowerItem, FSFItemRegister.WoodSoulItem, FSFItemRegister.PineWoodSoulItem, FSFItemRegister.RoseWoodSoulItem, FSFItemRegister.EbonyWoodSoulItem, FSFItemRegister.ShadowWoodSoulItem, FSFItemRegister.PalmWoodSoulItem, FSFItemRegister.PearlWoodSoulItem))
                    .itemLanguage("Forest Power", "森林之力")
                    .itemLanguageTooltip(1, "Create a 12-block aura that applies 8s of Wither and Bleeding to enemies each second", "生成一个半径12格的光环，每秒向光环的敌人施加8秒凋零和流血效果")
                    .itemLanguageTooltip(2, "+8 damage against Wither-affected enemies", "攻击受凋零影响的敌人时，造成的伤害提升8点")
                    .itemLanguageTooltip(3, "Attacking Bleeding enemies makes them burst into 3-6 tracking blood orbs that heal 3 HP each; their healing is reduced by 50%", "攻击受流血影响的敌人时，使敌人喷出3~6个追踪鲜血球，每个鲜血球恢复你3点生命值。受流血影响的敌人恢复的生命值减少50%")
                    .itemLanguageTooltip(4, "Spawn snowballs above you that hit aura enemies, applying Chill (-30% speed), 2s cooldown", "在你的头顶产生雪球攻击光环内的敌人，雪球命中时施加寒冷效果，使敌人移动速度减少30%，该效果有2秒冷却时间")
                    .itemLanguageTooltip(5, "Picking up an item boosts critical damage by 40% for 8 seconds", "拾取物品使你在8秒内造成的暴击伤害提升40%")
                    .itemLanguageTooltip(6, "Trading prices reduced by 50%", "交易价格减少50%")
                    .itemLanguageTooltip(-1, "\"Very hard\"", "“很硬”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final WoodSoul WoodSoulItem =
            register("wood_soul", WoodSoul::new, 0x986B48)
                    .jeiInfo(1, "Found in: Village Plains House (20%), Village Savanna House (20%), Village Weaponsmith (15%)", "获取：开启村庄平原房屋(20%)、村庄稀树草原房屋(20%)、村庄武器匠(15%)")
                    .lootTable(vanilla("chests/village/village_plains_house"), table -> pool(FSFItemRegister.WoodSoulItem, 0.2F))
                    .lootTable(vanilla("chests/village/village_savanna_house"), table -> pool(FSFItemRegister.WoodSoulItem, 0.2F))
                    .lootTable(vanilla("chests/village/village_weaponsmith"), table -> pool(FSFItemRegister.WoodSoulItem, 0.15F))
                    .itemLanguage("Wood Enchantment", "木魔石")
                    .itemLanguageTooltip(1, "Trading prices reduced by 20%", "交易价格降低20%")
                    .itemLanguageTooltip(2, "Defeating pillagers further reduces prices by 0.25%-0.5% each time, up to an extra 30%", "每次击败掠夺者，价格额外降低0.25%~0.5%，最多额外降低30%")
                    .itemLanguageTooltip(-1, "\"A trick that shopkeepers hate\"", "“被店主们讨厌的诡计”")
                    .itemLanguageTooltip(-2, "\"A humble beginning...\"", "“卑微的开始……”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final PineWoodSoul PineWoodSoulItem =
            register("pine_wood_soul", PineWoodSoul::new, 0x8B7464)
                    .jeiInfo(1, "Obtained from killing: Stray (1%)", "获取：击杀流浪者(1%)")
                    .lootTable(vanilla("entities/stray"), table -> pool(FSFItemRegister.PineWoodSoulItem, 0.01F))
                    .itemLanguage("Pine Wood Enchantment", "针叶木魔石")
                    .itemLanguageTooltip(1, "Spawn snowballs at nearby enemies, applying Chill (-30% speed) on hit, 5s cooldown", "产生雪球攻击附近内的敌人，雪球命中时施加寒冷效果，使敌人移动速度减少30%，该效果有5秒冷却时间")
                    .itemLanguageTooltip(-1, "\"Cold and cool\"", "“又冷又酷”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final RoseWoodSoul RoseWoodSoulItem =
            register("rose_wood_soul", RoseWoodSoul::new, 0xB56C64)
                    .jeiInfo(1, "Found in: Jungle Temple (10%)", "获取：开启丛林神庙(10%)")
                    .lootTable(vanilla("chests/jungle_temple"), table -> pool(FSFItemRegister.RoseWoodSoulItem, 0.1F))
                    .itemLanguage("Rose Wood Enchantment", "红木魔石")
                    .itemLanguageTooltip(1, "Grants the Jungle Dash with 25% extra starting speed", "赋予丛林冲刺，冲刺初速度提升25%")
                    .itemLanguageTooltip(2, "-20% damage taken while dashing", "在冲刺期间受到的伤害减少20%")
                    .itemLanguageTooltip(-1, "\"It'll hook you for sure\"", "“保证钩到你”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final EbonyWoodSoul EbonyWoodSoulItem =
            register("ebony_wood_soul", EbonyWoodSoul::new, 0x645A8D)
                    .jeiInfo(1, "Found in: Woodland Mansion (10%)", "获取：开启林地府邸(10%)")
                    .lootTable(vanilla("chests/woodland_mansion"), table -> pool(FSFItemRegister.EbonyWoodSoulItem, 0.1F))
                    .itemLanguage("Ebony Wood Enchantment", "乌木魔石")
                    .itemLanguageTooltip(1, "Create a 6-block corruption aura that drains corruption from each enemy per second, up to 250", "产生一个半径6格的腐化光环，每秒从光环内的每个敌人吸取腐化值，最多为250点")
                    .itemLanguageTooltip(2, "Based on corruption, deal up to +5 damage and reduce damage taken by up to 5%", "根据腐化值比例，造成的伤害最多提升5，受到的伤害最多减少5%")
                    .itemLanguageTooltip(-1, "\"Potential not yet fully realized\"", "“潜力未完全开发”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final ShadowWoodSoul ShadowWoodSoulItem =
            register("shadow_wood_soul", ShadowWoodSoul::new, 0x586876)
                    .jeiInfo(1, "Found in: Woodland Mansion (8%)", "获取：开启林地府邸(8%)")
                    .lootTable(vanilla("chests/woodland_mansion"), table -> pool(FSFItemRegister.ShadowWoodSoulItem, 0.08F))
                    .itemLanguage("Shadow Wood Enchantment", "阴影木魔石")
                    .itemLanguageTooltip(1, "Create a 6-block blood aura applying 6s of Hemorrhage, reducing healing by 70%", "产生一个半径6格的鲜血光环，每秒对鲜血光环内敌人施加6秒血如泉涌效果，使恢复的生命值减少70%")
                    .itemLanguageTooltip(2, "Attacking Hemorrhage-affected enemies makes them spray 2-3 damaging blood that tracks nearby foes, 0.5s cooldown", "攻击受血如泉涌影响的敌人时喷出2~3个伤害性血液，追踪周围其他敌人，该效果有0.5秒冷却时间")
                    .itemLanguageTooltip(-1, "\"Surprisingly clean\"", "“出奇的干净”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final PalmWoodSoul PalmWoodSoulItem =
            register("palm_wood_soul", PalmWoodSoul::new, 0xB78D56)
                    .jeiInfo(1, "Found in: Shipwreck Treasure (10%)", "获取：开启沉船宝藏(10%)")
                    .lootTable(vanilla("chests/shipwreck_treasure"), table -> pool(FSFItemRegister.PalmWoodSoulItem, 0.1F))
                    .itemLanguage("Palm Wood Enchantment", "棕榈木魔石")
                    .itemLanguageTooltip(1, "Press the Palm Explosion key to apply 20s of Oiled to nearby enemies (+200% fire damage taken)", "按下“棕榈爆炸”键，对附近敌人施加20秒涂油效果，使敌人受到的火焰伤害提升200%")
                    .itemLanguageTooltip(2, "+20% damage to Oiled enemies and set them on fire", "攻击被涂油影响的敌人时，伤害提升20%并点燃敌人")
                    .itemLanguageTooltip(-1, "\"Surprisingly serene\"", "“出奇的宁静”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final PearlWoodSoul PearlWoodSoulItem =
            register("pearl_wood_soul", PearlWoodSoul::new, 0xAD9A5F)
                    .jeiInfo(1, "Found in: Shipwreck Treasure (15%)", "获取：开启沉船宝藏(15%)")
                    .lootTable(vanilla("chests/shipwreck_treasure"), table -> pool(FSFItemRegister.PearlWoodSoulItem, 0.15F))
                    .itemLanguage("Pearl Wood Enchantment", "珍珠木魔石")
                    .itemLanguageTooltip(1, "+20% critical damage", "暴击伤害提升20%")
                    .itemLanguageTooltip(2, "When picking up items, summon a star that tracks a nearby enemy and explodes, granting 10s of Starlight, 1s cooldown", "拾取物品时，召唤一颗星星追踪周围一个敌人，命中时爆炸，并为你提供10秒星之光辉效果，该效果有1秒冷却时间")
                    .itemLanguageTooltip(3, "While under Starlight, +40% critical damage", "处于星之光辉影响下，暴击伤害提升40%")
                    .itemLanguageTooltip(-1, "\"Too little, too late...\"", "“太少了，太晚了……”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final EarthPower EarthPowerItem =
            register("earth_power", EarthPower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "earth_power", FSFItemRegister.EarthPowerItem, FSFItemRegister.CobaltSoulItem, FSFItemRegister.PalladiumSoulItem, FSFItemRegister.MithrilSoulItem, FSFItemRegister.OrichalcumSoulItem, FSFItemRegister.AdamantiteSoulItem, FSFItemRegister.TitaniumSoulItem))
                    .itemLanguage("Earth Power", "大地之力")
                    .itemLanguageTooltip(1, "After 3s without attacking, build 20 Earth Energy per second (max 1000), spending 10 per attack", "不攻击3秒后，每秒积聚20点大地能量，最多积聚1000点大地能量，攻击时消耗10点大地能量")
                    .itemLanguageTooltip(2, "Based on Earth Energy: up to +100% damage, +100% attack speed, +5 regen, +100% armor", "根据大地能量积累比例，最多提升100%造成伤害，100%攻击速度，5每秒生命恢复，100%护甲")
                    .itemLanguageTooltip(-1, "\"Gaia's blessing shines upon you\"", "“盖亚的祝福照耀着你”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final CobaltSoul CobaltSoulItem =
            register("cobalt_soul", CobaltSoul::new, 0x3DA4C4)
                    .jeiInfo(1, "Obtained from killing: Hoglin (1%)", "获取：击杀猪灵兽(1%)")
                    .jeiInfo(2, "Found in: Nether Fortress (8%)", "获取：开启下界要塞(8%)")
                    .lootTable(vanilla("entities/hoglin"), table -> pool(FSFItemRegister.CobaltSoulItem, 0.01F))
                    .lootTable(vanilla("chests/nether_bridge"), table -> pool(FSFItemRegister.CobaltSoulItem, 0.08F))
                    .itemLanguage("Cobalt Enchantment", "钴蓝魔石")
                    .itemLanguageTooltip(1, "When hurt, apply 2.5s of Oiled to nearby enemies (+200% fire damage taken), 3s cooldown", "受到伤害时，对附近的敌人施加2.5秒涂油效果，使敌人受到的火焰伤害提升200%，该效果有3秒冷却时间")
                    .itemLanguageTooltip(2, "Immune to Oiled", "免疫浸油")
                    .itemLanguageTooltip(-1, "\"I can't believe this isn't Palladium\"", "“真不敢相信这竟然不是钯金”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final PalladiumSoul PalladiumSoulItem =
            register("palladium_soul", PalladiumSoul::new, 0xF5AC28)
                    .jeiInfo(1, "Obtained from killing: Witch (1%)", "获取：击杀女巫(1%)")
                    .lootTable(vanilla("entities/witch"), table -> pool(FSFItemRegister.PalladiumSoulItem, 0.01F))
                    .itemLanguage("Palladium Enchantment", "钯金魔石")
                    .itemLanguageTooltip(1, "20% chance for enemies to burst into 3-5 tracking blood orbs healing 3 HP each, 2s cooldown", "攻击有20%概率使敌人爆出3~5个追踪鲜血球，每个鲜血球恢复你3点生命值，该效果有2秒冷却时间")
                    .itemLanguageTooltip(2, "Gain 25% Life Energy from healing (max 200)", "恢复生命值时积累25%生命能量，最多积累200点")
                    .itemLanguageTooltip(3, "At max Life Energy, spend it all to restore 100% max HP and gain 10s of Life Surge", "生命能量达到最大值时，消耗所有能量，为你恢复100%最大生命值，并使你获得10秒生命流涌效果")
                    .itemLanguageTooltip(4, "Under Life Surge, overhealing spawns damaging blood equal to the overflow that tracks nearby foes", "在生命流涌效果下，超过最大生命值的恢复会产生伤害性血液，伤害值等同溢出的恢复值，追踪周围其他敌人")
                    .itemLanguageTooltip(-1, "\"You feel your wounds slowly closing\"", "“你感到你的伤口在慢慢愈合”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final MithrilSoul MithrilSoulItem =
            register("mithril_soul", MithrilSoul::new, 0x9DD290)
                    .jeiInfo(1, "Obtained from killing: Vex (1%)", "获取：击杀恼鬼(1%)")
                    .jeiInfo(2, "Found in: Stronghold Library (12%)", "获取：开启要塞图书馆(12%)")
                    .lootTable(vanilla("entities/vex"), table -> pool(FSFItemRegister.MithrilSoulItem, 0.01F))
                    .lootTable(vanilla("chests/stronghold_library"), table -> pool(FSFItemRegister.MithrilSoulItem, 0.12F))
                    .itemLanguage("Mithril Enchantment", "秘银魔石")
                    .itemLanguageTooltip(1, "+30% attack speed", "攻击速度提升30%")
                    .itemLanguageTooltip(2, "Attacking grants an extra +5% attack speed (up to +70%), lost after 5s without attacking", "攻击使攻击速度额外提升5%，最多额外提升70%，5秒不攻击使额外攻击速度清空")
                    .itemLanguageTooltip(-1, "\"You feel weapon knowledge seep into your mind\"", "“你感觉武器的知识渗透进你的脑海中”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final OrichalcumSoul OrichalcumSoulItem =
            register("orichalcum_soul", OrichalcumSoul::new, 0xEB3291)
                    .jeiInfo(1, "Obtained from killing: Enderman (1%)", "获取：击杀末影人(1%)")
                    .jeiInfo(2, "Found in: Jungle Temple (8%)", "获取：开启丛林神庙(8%)")
                    .lootTable(vanilla("entities/enderman"), table -> pool(FSFItemRegister.OrichalcumSoulItem, 0.01F))
                    .lootTable(vanilla("chests/jungle_temple"), table -> pool(FSFItemRegister.OrichalcumSoulItem, 0.08F))
                    .itemLanguage("Orichalcum Enchantment", "山铜魔石")
                    .itemLanguageTooltip(1, "Summon 3-6 tracking petals on attack dealing 50% magic damage and 5s of Orichalcum Poisoning, 0.25s cooldown", "攻击时召唤3~6枚追踪花瓣，花瓣造成50%魔法伤害并施加5秒山铜中毒效果，该效果有0.25秒冷却时间")
                    .itemLanguageTooltip(2, "Immune to Orichalcum Poisoning", "免疫山铜中毒")
                    .itemLanguageTooltip(-1, "\"Nature blesses you\"", "“自然祝福着你”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls).build();

    public static final AdamantiteSoul AdamantiteSoulItem =
            register("adamantite_soul", AdamantiteSoul::new, 0xDD557D)
                    .jeiInfo(1, "Obtained from killing: Wither Skeleton (1%)", "获取：击杀凋零骷髅(1%)")
                    .lootTable(vanilla("entities/wither_skeleton"), table -> pool(FSFItemRegister.AdamantiteSoulItem, 0.01F))
                    .itemLanguage("Adamantite Enchantment", "精金魔石")
                    .itemLanguageTooltip(1, "+15% movement speed", "移动速度提升15%")
                    .itemLanguageTooltip(2, "Attacks apply 5s of Armor Break, reducing armor by 40%", "攻击造成5秒盔甲破损效果，减少40%护甲值")
                    .itemLanguageTooltip(3, "Against enemies with fewer than 20 armor: +40% damage and +80% critical damage", "攻击护甲低于20的敌人时，造成的伤害提升40%，暴击伤害提升80%")
                    .itemLanguageTooltip(-1, "\"Chaos\"", "“混乱”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final TitaniumSoul TitaniumSoulItem =
            register("titanium_soul", TitaniumSoul::new, 0x828C88)
                    .jeiInfo(1, "Obtained from killing: Ravager (1%)", "获取：击杀劫掠兽(1%)")
                    .lootTable(vanilla("entities/ravager"), table -> pool(FSFItemRegister.TitaniumSoulItem, 0.01F))
                    .itemLanguage("Titanium Enchantment", "钛金魔石")
                    .itemLanguageTooltip(1, "Reduce damage taken by 15%", "减少15%受到伤害")
                    .itemLanguageTooltip(2, "Taking damage builds 20% Guard Energy (max 600), decaying by 5 per second", "受到伤害会积累20%守护能量，最多积累600点守护能量，每秒流失5点")
                    .itemLanguageTooltip(3, "Reduce damage taken by up to 35% based on Guard Energy", "根据守护能量的比例，减少你受到的伤害，最多减少35%")
                    .itemLanguageTooltip(4, "Above 300 Guard Energy: +25% max HP and +50% armor", "守护能量超过300点时，提升25%最大生命值，提升50%护甲值")
                    .itemLanguageTooltip(5, "Below 50% max HP, Guard Energy damage reduction doubles", "低于50%最大生命值时，守护能量的减伤效果翻倍")
                    .itemLanguageTooltip(-1, "\"With absolute defense, who needs to dodge?\"", "“有了绝对防御后，谁还需要躲避呢？”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final NaturePower NaturePowerItem =
            register("nature_power", NaturePower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "nature_power", FSFItemRegister.NaturePowerItem, FSFItemRegister.CrimsonSoulItem, FSFItemRegister.LavaSoulItem, FSFItemRegister.RainCloudSoulItem, FSFItemRegister.FrostSoulItem, FSFItemRegister.GreenSoulItem, FSFItemRegister.MushroomSoulItem))
                    .itemLanguage("Nature Power", "自然之力")
                    .itemLanguageTooltip(1, "Summon 5 chlorophyte crystals that orbit you and fire lasers at enemies", "召唤5个围绕持有者旋转的叶绿水晶，叶绿水晶会向敌怪发射激光")
                    .itemLanguageTooltip(2, "Chlorophyte crystal damage increased by 300%", "叶绿水晶造成的伤害提升300%")
                    .itemLanguageTooltip(3, "Reduce damage taken by 15 points", "受到的伤害降低15点")
                    .itemLanguageTooltip(-1, "\"Travel every secret corner of the wilderness\"", "“走遍荒野的每一个秘密角落”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final CrimsonSoul CrimsonSoulItem =
            register("crimson_soul", CrimsonSoul::new, 0xCA3448)
                    .jeiInfo(1, "Obtained from killing: Zombie (1%)", "获取：击杀僵尸(1%)")
                    .lootTable(vanilla("entities/zombie"), table -> pool(FSFItemRegister.CrimsonSoulItem, 0.01F))
                    .itemLanguage("Crimson Enchantment", "猩红魔石")
                    .itemLanguageTooltip(1, "Attacks make enemies burst into 2-4 tracking blood orbs healing 3 HP, 0.25s per enemy cooldown", "攻击使敌人爆出2~4个追踪鲜血球，为你恢复3点生命值，每个敌人有0.25秒冷却时间")
                    .itemLanguageTooltip(2, "Regenerate 1% max HP per second; +2% more when no enemies are within 4 blocks", "每秒恢复1%最大生命值，周围4格没有敌人时，每秒额外恢复2%最大生命值")
                    .itemLanguageTooltip(3, "Killing enemies restores 20% of their max HP as health", "击杀敌人使你恢复敌人20%最大生命值的生命值")
                    .itemLanguageTooltip(-1, "\"You are reborn from the blood of your foes\"", "“你从敌人的鲜血中重生”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final LavaSoul LavaSoulItem =
            register("lava_soul", LavaSoul::new, 0xC32925)
                    .jeiInfo(1, "Obtained from killing: Magma Cube (1%)", "获取：击杀岩浆怪(1%)")
                    .jeiInfo(2, "Found in: Nether Fortress (10%)", "获取：开启下界要塞(10%)")
                    .lootTable(vanilla("entities/magma_cube"), table -> pool(FSFItemRegister.LavaSoulItem, 0.01F))
                    .lootTable(vanilla("chests/nether_bridge"), table -> pool(FSFItemRegister.LavaSoulItem, 0.1F))
                    .itemLanguage("Lava Enchantment", "熔岩魔石")
                    .itemLanguageTooltip(1, "Ignite nearby targets", "点燃附近的目标")
                    .itemLanguageTooltip(2, "Deal up to +120% damage to burning enemies", "对燃烧的敌人，造成的伤害提升至120%")
                    .itemLanguageTooltip(3, "Immune to lava damage", "免疫熔岩伤害")
                    .itemLanguageTooltip(-1, "\"They will feel the wrath of Hell\"", "“他们将感受到地狱的愤怒”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final RainCloudSoul RainCloudSoulItem =
            register("rain_cloud_soul", RainCloudSoul::new, 0xFFFF33)
                    .jeiInfo(1, "Obtained from killing: Phantom (1%)", "获取：击杀幻翼(1%)")
                    .jeiInfo(2, "Found in: Shipwreck Treasure (8%)", "获取：开启沉船宝藏(8%)")
                    .lootTable(vanilla("entities/phantom"), table -> pool(FSFItemRegister.RainCloudSoulItem, 0.01F))
                    .lootTable(vanilla("chests/shipwreck_treasure"), table -> pool(FSFItemRegister.RainCloudSoulItem, 0.08F))
                    .itemLanguage("Rain Cloud Enchantment", "雨云魔石")
                    .itemLanguageTooltip(1, "Chance to summon lightning on the attacker when hurt, 2s cooldown", "受到伤害时有概率召唤雷电劈向目标，该效果有2秒冷却时间")
                    .itemLanguageTooltip(2, "Immune to lightning damage", "免疫雷电伤害")
                    .itemLanguageTooltip(-1, "\"Come again another day\"", "“改日再来”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final FrostSoul FrostSoulItem =
            register("frost_soul", FrostSoul::new, 0x78BDB9)
                    .jeiInfo(1, "Obtained from killing: Stray (1%)", "获取：击杀流浪者(1%)")
                    .lootTable(vanilla("entities/stray"), table -> pool(FSFItemRegister.FrostSoulItem, 0.01F))
                    .itemLanguage("Frost Enchantment", "冰霜魔石")
                    .itemLanguageTooltip(1, "Summon 2 snowballs at enemies on attack, 1s cooldown", "攻击时召唤2枚雪球攻击敌人，该效果有1秒冷却时间")
                    .itemLanguageTooltip(2, "-60% snowball cooldown in cold biomes", "在寒冷群系时，雪球的冷却时间减少60%")
                    .itemLanguageTooltip(-1, "\"Let's wrap the world in a thick coat of ice\"", "“让我们给这个世界披上一层厚厚的冰衣”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final GreenSoul GreenSoulItem =
            register("green_soul", GreenSoul::new, 0x1D8A00)
                    .jeiInfo(1, "Obtained from killing: Spider (1%)", "获取：击杀蜘蛛(1%)")
                    .jeiInfo(2, "Found in: Jungle Temple (5%), Ancient City (8%)", "获取：开启丛林神庙(5%)、远古城市(8%)")
                    .lootTable(vanilla("entities/spider"), table -> pool(FSFItemRegister.GreenSoulItem, 0.01F))
                    .lootTable(vanilla("chests/jungle_temple"), table -> pool(FSFItemRegister.GreenSoulItem, 0.05F))
                    .lootTable(vanilla("chests/ancient_city"), table -> pool(FSFItemRegister.GreenSoulItem, 0.08F))
                    .itemLanguage("Green Enchantment", "叶绿魔石")
                    .itemLanguageTooltip(1, "Grants the Chlorophyte Dash; dashing poisons enemies for 10 seconds", "赋予叶绿冲刺。冲刺会对敌人造成10秒中毒效果")
                    .itemLanguageTooltip(2, "Grants flight", "赋予飞行")
                    .itemLanguageTooltip(3, "A chlorophyte crystal follows you, firing lasers at nearby enemies", "产生一个跟随持有者的叶绿水晶攻击周围敌人，叶绿水晶会向敌怪发射激光")
                    .itemLanguageTooltip(-1, "\"The essence of the jungle crystallizes around you\"", "“丛林的精华在你周围凝结成晶体”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final MushroomSoul MushroomSoulItem =
            register("mushroom_soul", MushroomSoul::new, 0x0089F7)
                    .jeiInfo(1, "Obtained from killing: Zombie (1%)", "获取：击杀僵尸(1%)")
                    .lootTable(vanilla("entities/zombie"), table -> pool(FSFItemRegister.MushroomSoulItem, 0.01F))
                    .itemLanguage("Mushroom Enchantment", "蘑菇魔石")
                    .itemLanguageTooltip(1, "Eating mushroom stew restores an extra 50 HP", "食用蘑菇煲额外恢复50点生命值")
                    .itemLanguageTooltip(2, "Killing enemies drops 2-4 mushrooms", "击杀敌人后掉落2~4个蘑菇")
                    .itemLanguageTooltip(-1, "\"Made with real mushrooms!\"", "“是用真的蘑菇做的！”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final LifePower LifePowerItem =
            register("life_power", LifePower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "life_power", FSFItemRegister.LifePowerItem, FSFItemRegister.BeeSoulItem, FSFItemRegister.BeetleSoulItem, FSFItemRegister.PumpkinSoulItem, FSFItemRegister.SpiderSoulItem, FSFItemRegister.TurtleSoulItem))
                    .itemLanguage("Life Power", "生命之力")
                    .itemLanguageTooltip(1, "Grants flight", "赋予飞行")
                    .itemLanguageTooltip(2, "Flight time increased by 150%", "飞行时间提升150%")
                    .itemLanguageTooltip(3, "Drinking honey grants 10 seconds of Ambrosia", "饮用蜂蜜使你获得10秒仙馔密酒效果")
                    .itemLanguageTooltip(4, "Under Ambrosia: +30% damage dealt, +5 regen per second, +200% summon damage", "处于仙馔密酒效果下时，造成的伤害提升30%伤害，提升5每秒生命恢复，召唤伤害提升200%")
                    .itemLanguageTooltip(5, "Reflect 500% of damage, 0.1s cooldown", "反弹500%伤害，该效果有0.1秒冷却时间")
                    .itemLanguageTooltip(-1, "\"Few creatures dare defy your will\"", "“罕有生灵敢违背你的意愿”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final BeeSoul BeeSoulItem =
            register("bee_soul", BeeSoul::new, 0xFFC300)
                    .jeiInfo(1, "Obtained from killing: Bee (1%)", "获取：击杀蜜蜂(1%)")
                    .lootTable(vanilla("entities/bee"), table -> pool(FSFItemRegister.BeeSoulItem, 0.01F))
                    .itemLanguage("Bee Enchantment", "蜜蜂魔石")
                    .itemLanguageTooltip(1, "Grants flight", "赋予飞行")
                    .itemLanguageTooltip(2, "Flight time increased by 40%", "飞行时间提升40%")
                    .itemLanguageTooltip(3, "+2 health regen per second while near flowers", "接触花朵时提升2每秒生命恢复")
                    .itemLanguageTooltip(-1, "\"By all known principles of aviation, bees should not be able to fly at all\"", "“根据目前所知的所有航空原理，蜜蜂应该根本不可能会飞”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final BeetleSoul BeetleSoulItem =
            register("beetle_soul", BeetleSoul::new, 0x6D5B86)
                    .jeiInfo(1, "Obtained from killing: Creeper (1%)", "获取：击杀苦力怕(1%)")
                    .jeiInfo(2, "Found in: Jungle Temple (15%)", "获取：开启丛林神庙(15%)")
                    .lootTable(vanilla("entities/creeper"), table -> pool(FSFItemRegister.BeetleSoulItem, 0.01F))
                    .lootTable(vanilla("chests/jungle_temple"), table -> pool(FSFItemRegister.BeetleSoulItem, 0.15F))
                    .itemLanguage("Beetle Enchantment", "甲虫魔石")
                    .itemLanguageTooltip(1, "Grants flight", "赋予飞行")
                    .itemLanguageTooltip(2, "Gain 1 Beetle Endurance every 7s (max 3), each reducing damage taken by 15%", "每7秒获得1层甲虫耐力，最多叠加3层，每层甲虫耐力减少15%受到伤害")
                    .itemLanguageTooltip(3, "Taking damage removes one Endurance layer, at most one every 2 seconds", "受到伤害时减少一层甲虫耐力，每2秒最多减少一层甲虫耐力")
                    .itemLanguageTooltip(4, "Attacking grants 1 Beetle Might (max 3), each +30% damage; lose one per second", "攻击时获得1层甲虫力量，最多叠加3层，每层甲虫力量提升30%造成伤害，每秒失去1层甲虫力量")
                    .itemLanguageTooltip(-1, "\"Invisible fecal life flows through your veins\"", "“你的血管里流淌着看不见的粪便生命”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final PumpkinSoul PumpkinSoulItem =
            register("pumpkin_soul", PumpkinSoul::new, 0xE56500)
                    .jeiInfo(1, "Obtained from killing: Zombie (1%)", "获取：击杀僵尸(1%)")
                    .lootTable(vanilla("entities/zombie"), table -> pool(FSFItemRegister.PumpkinSoulItem, 0.01F))
                    .itemLanguage("Pumpkin Enchantment", "南瓜魔石")
                    .itemLanguageTooltip(1, "-15% damage taken while wearing a pumpkin head", "携带南瓜头时减少15%受到伤害")
                    .itemLanguageTooltip(2, "Stepping on mature pumpkins makes them explode, restoring 5% of your max HP", "踩在成熟的南瓜上时会使南瓜产生爆炸并恢复你5%最大生命值")
                    .itemLanguageTooltip(-1, "\"Your sudden craving for pumpkins can never be satisfied\"", "“你对南瓜的突发渴望永远不会得到满足”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final SpiderSoul SpiderSoulItem =
            register("spider_soul", SpiderSoul::new, 0x65483F)
                    .jeiInfo(1, "Obtained from killing: Spider (1%)", "获取：击杀蜘蛛(1%)")
                    .lootTable(vanilla("entities/spider"), table -> pool(FSFItemRegister.SpiderSoulItem, 0.01F))
                    .itemLanguage("Spider Enchantment", "蜘蛛魔石")
                    .itemLanguageTooltip(1, "+15% summon damage and 24 summon armor pierce", "提升15%召唤伤害，24点召唤穿透")
                    .itemLanguageTooltip(-1, "\"Arachnophobe? As punishment, let the spiders get him!\"", "“蜘蛛恐惧者？作为惩罚，让他被蜘蛛干掉吧！”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final TurtleSoul TurtleSoulItem =
            register("turtle_soul", TurtleSoul::new, 0xE86024)
                    .jeiInfo(1, "Obtained from killing: Turtle (1%)", "获取：击杀海龟(1%)")
                    .lootTable(vanilla("entities/turtle"), table -> pool(FSFItemRegister.TurtleSoulItem, 0.01F))
                    .itemLanguage("Turtle Enchantment", "乌龟魔石")
                    .itemLanguageTooltip(1, "Reflect 40% of damage", "反弹40%伤害")
                    .itemLanguageTooltip(2, "Below 50% HP, reflected damage increases to 80%", "生命值低于50%时，反弹的伤害提升至80%伤害")
                    .itemLanguageTooltip(3, "Below 25% HP, damage taken is reduced by an amount equal to 4% of your max HP", "生命值低于25%时，受到的伤害减少，减少值相当于你4%最大生命值")
                    .itemLanguageTooltip(4, "Immune to cactus damage", "免疫仙人掌伤害")
                    .itemLanguageTooltip(-1, "\"You suddenly feel the urge to hide inside a shell\"", "“你突然有一种想躲进壳里的冲动”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final SpiritPower SpiritPowerItem =
            register("spirit_power", SpiritPower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "spirit_power", FSFItemRegister.SpiritPowerItem, FSFItemRegister.ForbiddenSoulItem, FSFItemRegister.HolySoulItem, FSFItemRegister.AncientHolySoulItem, FSFItemRegister.TekeSoulItem, FSFItemRegister.GhostSoulItem))
                    .itemLanguage("Spirit Power", "心灵之力")
                    .itemLanguageTooltip(1, "Healing received increased by 70%", "受到的治疗效果提升70%")
                    .itemLanguageTooltip(2, "Flight time increased by 100%", "飞行时间提升100%")
                    .itemLanguageTooltip(3, "Summon 8 extra Blades of Sorrows with +200% damage", "额外召唤8柄胜利与誓约之刃，胜利与誓约之刃造成的伤害提升200%")
                    .itemLanguageTooltip(-1, "\"Drifting away like a transcended immortal\"", "“飘飘乎如遗世独立，羽化而登仙”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final ForbiddenSoul ForbiddenSoulItem =
            register("forbidden_soul", ForbiddenSoul::new, 0xE8B300)
                    .jeiInfo(1, "Obtained from killing: Evoker (1%)", "获取：击杀唤魔者(1%)")
                    .lootTable(vanilla("entities/evoker"), table -> pool(FSFItemRegister.ForbiddenSoulItem, 0.01F))
                    .itemLanguage("Forbidden Enchantment", "禁戒魔石")
                    .itemLanguageTooltip(1, "Press the Storm key to summon a forbidden storm that pulls nearby targets for 5s, 30s cooldown", "按下“风暴”键召唤禁戒风暴，吸引附近的目标，持续5秒，该效果有30秒冷却时间")
                    .itemLanguageTooltip(-1, "\"Walking like an Egyptian\"", "“走路像个埃及人”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final HolySoul HolySoulItem =
            register("holy_soul", HolySoul::new, 0x968562)
                    .jeiInfo(1, "Found in: Desert Pyramid (20%)", "获取：开启沙漠神殿(20%)")
                    .lootTable(vanilla("chests/desert_pyramid"), table -> pool(FSFItemRegister.HolySoulItem, 0.2F))
                    .itemLanguage("Holy Enchantment", "神圣魔石")
                    .itemLanguageTooltip(1, "Healing received increased by 80%", "提升80%受到治疗")
                    .itemLanguageTooltip(2, "Being healed sends a shockwave knocking back nearby targets, 1s cooldown", "受到治疗时会产生冲击波，击退附近的目标，该效果有1秒冷却时间")
                    .itemLanguageTooltip(-1, "\"Bring it on\"", "“尽管放马过来”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final AncientHolySoul AncientHolySoulItem =
            register("ancient_holy_soul", AncientHolySoul::new, 0x968564)
                    .jeiInfo(1, "Obtained from killing: Vex (1%)", "获取：击杀恼鬼(1%)")
                    .jeiInfo(2, "Found in: Woodland Mansion (12%)", "获取：开启林地府邸(12%)")
                    .lootTable(vanilla("entities/vex"), table -> pool(FSFItemRegister.AncientHolySoulItem, 0.01F))
                    .lootTable(vanilla("chests/woodland_mansion"), table -> pool(FSFItemRegister.AncientHolySoulItem, 0.12F))
                    .itemLanguage("Ancient Holy Enchantment", "远古神圣魔石")
                    .itemLanguageTooltip(1, "Summon Blades of Sorrows to fight for you", "召唤胜利与誓约之刃为你而战")
                    .itemLanguageTooltip(-1, "\"Are you strong enough to wield me?\"", "“你有足够的力量驾驭我吗？”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final TekeSoul TekeSoulItem =
            register("teke_soul", TekeSoul::new, 0x53A71E)
                    .jeiInfo(1, "Found in: Jungle Temple (20%)", "获取：开启丛林神庙(20%)")
                    .lootTable(vanilla("chests/jungle_temple"), table -> pool(FSFItemRegister.TekeSoulItem, 0.2F))
                    .itemLanguage("Teke Enchantment", "提基魔石")
                    .itemLanguageTooltip(1, "+35% summon damage", "提升35%召唤伤害")
                    .itemLanguageTooltip(2, "Dealing summon damage applies 12s of Teke Mist (-20% speed, -10% armor)", "造成召唤伤害时，施加12秒灵雾迷障效果，减少20%移动速度，10%护甲值")
                    .itemLanguageTooltip(-1, "\"Aku Aku!\"", "“Aku Aku!”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final GhostSoul GhostSoulItem =
            register("ghost_soul", GhostSoul::new, 0xABCCFE)
                    .jeiInfo(1, "Obtained from killing: Ghast (1%)", "获取：击杀恶魂(1%)")
                    .jeiInfo(2, "Found in: Dungeon (8%), Ancient City (10%)", "获取：开启地牢(8%)、远古城市(10%)")
                    .lootTable(vanilla("entities/ghast"), table -> pool(FSFItemRegister.GhostSoulItem, 0.01F))
                    .lootTable(vanilla("chests/simple_dungeon"), table -> pool(FSFItemRegister.GhostSoulItem, 0.08F))
                    .lootTable(vanilla("chests/ancient_city"), table -> pool(FSFItemRegister.GhostSoulItem, 0.1F))
                    .itemLanguage("Ghost Enchantment", "幽魂魔石")
                    .itemLanguageTooltip(1, "Attacks steal 1 Soul Energy (max 100), each granting +2% max HP", "攻击时夺取1点灵魂能量，最多夺取100点灵魂能量，每点灵魂能量提升2%最大生命值")
                    .itemLanguageTooltip(2, "At max Soul Energy, death spends it all to revive at 25% HP and clear harmful effects, 600s cooldown", "灵魂能量达到最大值时，死亡会消耗所有灵魂能量使你复活，恢复你25%最大生命值并清除减益效果，该效果有600秒冷却时间")
                    .itemLanguageTooltip(3, "When Soul Energy overflows, each second it summons a tracking soul orb dealing overflow × 4% of your max HP as damage", "灵魂能量溢出时，每秒消耗所有溢出灵魂能量，召唤一枚追踪灵魂球，灵魂球伤害为溢出灵魂能量*4%最大生命值")
                    .itemLanguageTooltip(-1, "\"Their own vitality will destroy them\"", "“他们的生命力将毁灭他们自己”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final WillPower WillPowerItem =
            register("will_power", WillPower::new, FSFRarity.Purple)
                    .recipeWithLookup((provider, output) -> shapeless(output, "will_power", FSFItemRegister.WillPowerItem, FSFItemRegister.GoldSoulItem, FSFItemRegister.PlatinumSoulItem, FSFItemRegister.GladiatorSoulItem, FSFItemRegister.RedRidingSoulItem, FSFItemRegister.ValhallaKnightSoulItem))
                    .itemLanguage("Will Power", "意志之力")
                    .itemLanguageTooltip(1, "Healing received increased by 25%", "受到的治疗效果提升25%")
                    .itemLanguageTooltip(2, "Killing enemies has a 40% chance to multiply loot by 1600%", "击杀敌人有40%概率提升1600%战利品数量")
                    .itemLanguageTooltip(3, "+80% damage against Midas-affected enemies", "攻击被迈达斯影响的敌人时，造成的伤害提升80%")
                    .itemLanguageTooltip(-1, "\"Unbreakable determination\"", "“坚不可摧的决心”")
                    .itemTag(FSFItemTagsRegister.SectionPower)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final GoldSoul GoldSoulItem =
            register("gold_soul", GoldSoul::new, 0xE7B21C)
                    .jeiInfo(1, "Obtained from killing: Zombified Piglin (1%)", "获取：击杀僵尸猪灵(1%)")
                    .jeiInfo(2, "Found in: Desert Pyramid (5%), Bastion Treasure (15%)", "获取：开启沙漠神殿(5%)、堡垒遗迹(15%)")
                    .lootTable(vanilla("entities/zombified_piglin"), table -> pool(FSFItemRegister.GoldSoulItem, 0.01F))
                    .lootTable(vanilla("chests/desert_pyramid"), table -> pool(FSFItemRegister.GoldSoulItem, 0.05F))
                    .lootTable(vanilla("chests/bastion_treasure"), table -> pool(FSFItemRegister.GoldSoulItem, 0.15F))
                    .itemLanguage("Gold Enchantment", "金魔石")
                    .itemLanguageTooltip(1, "Attacks apply 10s of Midas", "攻击造成10秒迈达斯减益")
                    .itemLanguageTooltip(2, "Killing Midas-affected targets drops 1-4 extra gold ingots", "击杀被迈达斯影响的目标时额外会掉落1~4个金锭")
                    .itemLanguageTooltip(3, "Press the Golden Body key to become invulnerable for 6s, 120s cooldown", "按下“金身”键，使你在6秒内无敌，该效果有120秒冷却")
                    .itemLanguageTooltip(4, "When invulnerability ends, gold ingots in your inventory may extend it", "无敌时间结束时，尝试消耗物品栏中的金锭延长无敌时间")
                    .itemLanguageTooltip(5, "Immune to Midas", "免疫迈达斯")
                    .itemLanguageTooltip(-1, "\"Money can make the devil grind the mill\"", "“有钱能使鬼推磨”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final PlatinumSoul PlatinumSoulItem =
            register("platinum_soul", PlatinumSoul::new, 0x7F96B9)
                    .jeiInfo(1, "Obtained from killing: Piglin Brute (1%)", "获取：击杀猪灵蛮兵(1%)")
                    .jeiInfo(2, "Found in: Bastion Treasure (10%), Buried Treasure (10%)", "获取：开启堡垒遗迹(10%)、埋藏宝藏(10%)")
                    .lootTable(vanilla("entities/piglin_brute"), table -> pool(FSFItemRegister.PlatinumSoulItem, 0.01F))
                    .lootTable(vanilla("chests/bastion_treasure"), table -> pool(FSFItemRegister.PlatinumSoulItem, 0.1F))
                    .lootTable(vanilla("chests/buried_treasure"), table -> pool(FSFItemRegister.PlatinumSoulItem, 0.1F))
                    .itemLanguage("Platinum Enchantment", "铂金魔石")
                    .itemLanguageTooltip(1, "Killing enemies has a 20% chance to multiply loot by 500%", "击杀敌人有20%概率提升500%战利品数量")
                    .itemLanguageTooltip(-1, "\"Priceless treasure\"", "“无价之宝”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final GladiatorSoul GladiatorSoulItem =
            register("gladiator_soul", GladiatorSoul::new, 0x9C924E)
                    .jeiInfo(1, "Obtained from killing: Ravager (1%)", "获取：击杀劫掠兽(1%)")
                    .jeiInfo(2, "Found in: Woodland Mansion (15%)", "获取：开启林地府邸(15%)")
                    .lootTable(vanilla("entities/ravager"), table -> pool(FSFItemRegister.GladiatorSoulItem, 0.01F))
                    .lootTable(vanilla("chests/woodland_mansion"), table -> pool(FSFItemRegister.GladiatorSoulItem, 0.15F))
                    .itemLanguage("Gladiator Enchantment", "角斗士魔石")
                    .itemLanguageTooltip(1, "With fewer than 3 targets within 8 blocks: +30% damage, otherwise -20% damage taken", "周围8格的目标数少于3个时，造成的伤害提升30%，否则减少20%受到伤害")
                    .itemLanguageTooltip(2, "With only one target within 8 blocks: +50% damage dealt but +50% damage taken", "周围8格只有一个目标时，造成的伤害提升50%，受到的伤害提升50%")
                    .itemLanguageTooltip(-1, "\"Isn't that exciting?\"", "“你不觉得刺激吗？”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final RedRidingSoul RedRidingSoulItem =
            register("red_riding_soul", RedRidingSoul::new, 0xC01B3C)
                    .jeiInfo(1, "Obtained from killing: Wolf (1%)", "获取：击杀狼(1%)")
                    .lootTable(vanilla("entities/wolf"), table -> pool(FSFItemRegister.RedRidingSoulItem, 0.01F))
                    .itemLanguage("Red Riding Enchantment", "红色骑术魔石")
                    .itemLanguageTooltip(1, "+40% damage to armored targets", "对有护甲的目标，造成的伤害提升40%")
                    .itemLanguageTooltip(2, "Attacks stack Guerilla (max 10), each granting +1% movement speed", "攻击时叠加1层游击效果，最多叠加10层，每层游击提升1%移动速度")
                    .itemLanguageTooltip(3, "At 10 Guerilla stacks, dash starting speed +50%", "游击层数达到10层时，冲刺初速度提升50%")
                    .itemLanguageTooltip(4, "Taking damage clears all Guerilla stacks", "受到伤害时清空游击层数")
                    .itemLanguageTooltip(-1, "\"Little Red Riding Hood, big bad wolf!\"", "“小红帽，大坏蛋！”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static final ValhallaKnightSoul ValhallaKnightSoulItem =
            register("valhalla_knight_soul", ValhallaKnightSoul::new, 0x93651E)
                    .jeiInfo(1, "Obtained from killing: Vindicator (1%)", "获取：击杀卫道士(1%)")
                    .lootTable(vanilla("entities/vindicator"), table -> pool(FSFItemRegister.ValhallaKnightSoulItem, 0.01F))
                    .itemLanguage("Valhalla Knight Enchantment", "英灵殿骑士魔石")
                    .itemLanguageTooltip(1, "While riding: +40% damage, +50% armor, +2 regen per second", "骑乘坐骑时，造成的伤害提升40%，提升50%护甲值，提升2每秒生命恢复")
                    .itemLanguageTooltip(2, "Healing received increased by 15%", "受到的治疗效果提升15%")
                    .itemLanguageTooltip(-1, "\"The call of Valhalla\"", "“瓦尔哈拉的呼唤”")
                    .itemTag(FSFItemTagsRegister.SectionEnchantment)
                    .itemModel()
                    .itemTag(FSFItemTagsRegister.Souls)
                    .build();

    public static <T extends Item> FSFItemBuilder<T> register(String name, Function<Item.Properties, T> function, Integer color) {
        return register(name, function, new FSFRarity(color));
    }

    public static <T extends Item> FSFItemBuilder<T> register(String name, Function<Item.Properties, T> function, FSFRarity fsfRarity) {
        return FSF.REGISTRIES.build( name, () -> function.apply(new Item.Properties().stacksTo(1).fireResistant().component(FSFRegisters.RARITY, fsfRarity)));
    }

    public static void init() {
        // The items are registered by the field initializers above.
    }

    private static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    private static LootPool.Builder pool(ItemLike item, float chance) {
        return LootPool.lootPool()
                .add(LootItem.lootTableItem(item)
                             .when(LootItemRandomChanceCondition.randomChance(chance))
                             .when(LootItemKilledByPlayerCondition.killedByPlayer()));
    }

    private static void shapeless(RecipeOutput output, String id, ItemLike result, ItemLike... parts) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result);
        for (ItemLike part : parts) {
            builder.requires(part);
        }
        builder.unlockedBy("has_" + parts[0].asItem(), InventoryChangeTrigger.TriggerInstance.hasItems(parts[0]));
        builder.save(output, FSF.rl(id));
    }
}
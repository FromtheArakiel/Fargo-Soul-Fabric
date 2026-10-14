package dev.arakiel.fargosoulfabric.register;

import dev.arakiel.fargosoulfabric.FSF;
import dev.arakiel.fargosoulfabric.common.FSFEntityAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

/** Every attribute this mod adds. */
public final class FSFAttributeRegister {

    public static final Holder<Attribute> ArmorPierce = register("armor_pierce", 0.0D);

    public static final Holder<Attribute> HealthRegen = register("health_regen", 0.0D, -1000000.0D, 1000000.0D);

    public static final Holder<Attribute> MinionMaxCount = register("minion_max_count", 1.0D, 0.0D, 1000.0D);

    public static final Holder<Attribute> SentryMaxCount = register("sentry_max_count", 1.0D, 0.0D, 1000.0D);

    public static final Holder<Attribute> SummonDamage = register("summon_damage", 1.0D, 0.0D, 1000000.0D);

    public static final Holder<Attribute> SummonKnockback = register("summon_knockback", 1.0D, 0.0D, 10.0D);

    public static final Holder<Attribute> SummonArmorPierce = register("summon_armor_pierce", 0.0D, 0.0D, 1000000.0D);

    public static final Holder<Attribute> SummonSearchRange = register("summon_search_range", 1.0D, 0.0D, 10.0D);

    private FSFAttributeRegister() {
    }

    public static void init() {
        FSFEntityAttributes.add(EntityType.PLAYER, ArmorPierce);
    }

    private static Holder<Attribute> register(String name, double defaultValue) {
        RangedAttribute attribute = new RangedAttribute(FSF.rl(name).toString(), defaultValue, 0.0D,
                Double.MAX_VALUE);
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, FSF.rl(name), attribute.setSyncable(true));
    }

    private static Holder<Attribute> register(String name, double defaultValue, double min, double max) {
        RangedAttribute attribute = new RangedAttribute(FSF.rl(name).toString(), defaultValue, min, max);
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, FSF.rl(name), attribute.setSyncable(true));
    }
}

package dev.arakiel.fargosoulfabric.common.item.terraSoul.lifePower;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;

public class SpiderSoul extends SoulItem {

    public SpiderSoul(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(FargoSoulAttributeRegister.SummonDamage, new AttributeModifier(FargoSoul.rl("spider_damage"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        map.put(FargoSoulAttributeRegister.SummonArmorPierce, new AttributeModifier(FargoSoul.rl("spider_pierce"), 24, AttributeModifier.Operation.ADD_VALUE));
        return map;
    }
}
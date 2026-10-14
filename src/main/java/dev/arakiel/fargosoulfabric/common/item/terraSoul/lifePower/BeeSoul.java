package dev.arakiel.fargosoulfabric.common.item.terraSoul.lifePower;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BeeSoul extends SoulItem {

    public BeeSoul(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canFly(Player player) {
        return true;
    }

    @Override
    public void getMaxFlyTime(Player player, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(0.4f, ValueOperation.ADD_MULTIPLIED_BASE));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        if (hasFlowerNear(player)) {
            map.put(FargoSoulAttributeRegister.HealthRegen, new AttributeModifier(FargoSoul.rl("bee_regen"), 2, AttributeModifier.Operation.ADD_VALUE));
        }
        return map;
    }

    private static boolean hasFlowerNear(Player player) {
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        for (BlockPos around : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState state = level.getBlockState(around);
            if (state.is(BlockTags.FLOWERS)) {
                return true;
            }
        }
        return false;
    }
}
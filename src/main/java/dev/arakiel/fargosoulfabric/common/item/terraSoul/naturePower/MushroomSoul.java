package dev.arakiel.fargosoulfabric.common.item.terraSoul.naturePower;

import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class MushroomSoul extends SoulItem {

    public MushroomSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void eat(Player player, ItemStack food) {
        if (food.is(Items.MUSHROOM_STEW)) {
            player.heal(50);
        }
    }

    @Override
    public void kill(Player player, LivingEntity target, DamageSource source) {
        RandomSource random = player.getRandom();
        int count = 2 + random.nextInt(3);
        for (int i = 0; i < count; i++) {
            target.spawnAtLocation(new ItemStack(random.nextBoolean() ? Items.BROWN_MUSHROOM : Items.RED_MUSHROOM));
        }
    }
}
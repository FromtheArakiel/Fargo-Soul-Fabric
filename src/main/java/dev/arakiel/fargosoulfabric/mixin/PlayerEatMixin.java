package dev.arakiel.fargosoulfabric.mixin;

import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEatMixin {

    @Inject(method = "eat", at = @At("HEAD"))
    public void eat(Level level, ItemStack food, FoodProperties foodProperties, CallbackInfoReturnable<ItemStack> cir) {
        Player player = Player.class.cast(this);
        PlayerSouls.forEach(player, PlayerSouls -> PlayerSouls.eat(player, food));
    }
}

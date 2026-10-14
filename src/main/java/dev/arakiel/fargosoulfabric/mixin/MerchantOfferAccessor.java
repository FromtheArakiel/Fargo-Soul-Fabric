package dev.arakiel.fargosoulfabric.mixin;

import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MerchantOffer.class)
public interface MerchantOfferAccessor {
    @Invoker
    int callGetModifiedCostCount(ItemCost itemCost);
}

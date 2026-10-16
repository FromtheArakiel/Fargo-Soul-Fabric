package dev.arakiel.fargosoulfabric.mixin;

import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Villager.class)
public class VillagerMixin {

    @Inject(
            method = "updateSpecialPrices",
            at = @At(
                    value = "TAIL"
            )
    )
    private void updateSpecialPrices(Player player, CallbackInfo ci) {
        Villager villager = Villager.class.cast(this);
        MerchantOffers offers = villager.getOffers();
        List<ValueModifier> modifiers = new ArrayList<>();
        PlayerSouls.forEach(player, soulItem -> soulItem.updateSpecialPrices(player, villager, modifiers));
        float modifierAfter = ValueModifier.getModifierAfter(1, modifiers);
        for (MerchantOffer offer : offers) {
            MerchantOfferAccessor accessor = (MerchantOfferAccessor) offer;
            offer.addToSpecialPriceDiff((int) ((modifierAfter - 1) * accessor.callGetModifiedCostCount(offer.getItemCostA())));
        }
    }
}
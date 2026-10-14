package dev.arakiel.fargosoulfabric.common.item.base;

import dev.arakiel.fargosoulfabric.register.FSFRegisters;
import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.SoulKey;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.common.MovementState;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.component.FSFRarity;
import dev.arakiel.fargosoulfabric.common.summon.IEntityCollision;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import dev.arakiel.curios.api.SlotContext;
import dev.arakiel.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SoulItem extends Item implements ICurioItem {

    public SoulItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return !PlayerSouls.isEquipped(slotContext.entity(), stack.getItem());
    }

    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        return HashMultimap.create();
    }

    public void criticalHit(Player player, LivingEntity target, List<ValueModifier> modifiers) {
    }

    public void pickup(Player player, ItemStack itemStack) {
    }

    public void shieldBlock(Player player, DamageSource source, float blockedDamage) {
    }

    /** 收到治疗时修正治疗量：向 modifiers 添加治疗修正（乘区用 ADD_MULTIPLIED_BASE）。 */
    public void healAmount(Player player, float amount, List<ValueModifier> modifiers) {
    }

    /** 是否赋予飞行能力（决定飞行时长聚合是否启用）。 */
    public boolean canFly(Player player) {
        return false;
    }

    public void getMaxFlyTime(Player player, List<ValueModifier> modifiers) {
    }

    public boolean renderLavaFog(Player player){
        return true;
    }

    public boolean renderFireOverlay(Player player){
        return true;
    }

    public void updateSpecialPrices(Player player, Villager villager, List<ValueModifier> modifiers) {
    }

    public void eat(Player player, ItemStack food) {
    }

    public List<Component> getTooltip(ItemStack itemStack, boolean shiftDown, boolean master) {
        List<Component> list = new ArrayList<>();
        if (master) {
            list.add(Component.translatable("fargo_soul.key.tooltip", Component.literal("Shift").withStyle(shiftDown ? ChatFormatting.WHITE : ChatFormatting.GRAY)).withStyle(ChatFormatting.DARK_GRAY));
        }
        ResourceLocation location = BuiltInRegistries.ITEM.getKey(this);
        String key = "item." + location.getNamespace() + "." + location.getPath() + ".tooltip.";
        Language language = Language.getInstance();
        if (shiftDown) {
            if (master) {
                list.add(Component.empty());
            }
            FSFRarity rarity = (master ? itemStack : this.getDefaultInstance()).get(FSFRegisters.RARITY);
            int rarityColor = rarity != null ? rarity.color() : -1;
            for (int i = 1; language.has(key + i); i++) {
                MutableComponent translatable = Component.translatable(key + i);
                if (rarityColor != -1) {
                    translatable = translatable.withColor(rarityColor);
                }
                list.add(translatable);
            }
            Set<SoulItem> soulItemList = getSoulItemList();
            for (SoulItem soulItem : soulItemList) {
                list.addAll(soulItem.getTooltip(itemStack, shiftDown, false));
            }
        } else {
            for (int i = -1; language.has(key + i); i--) {
                list.add(Component.translatable(key + i).withStyle(ChatFormatting.GRAY));
            }
        }
        return list;
    }

    public Set<SoulItem> getSoulItemList() {
        return new HashSet<>();
    }

    public void tick(Player living){
    }

    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
    }

    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
    }

    public boolean death(Player player, DamageSource source, boolean canceled) {
        return true;
    }

    public void kill(Player attacker, LivingEntity target, DamageSource source) {
    }

    public void movement(Player player, MovementState input) {
    }

    public boolean canSprint(Player player) {
        return false;
    }

    public void sprintCollision(Player player, List<IEntityCollision.HitContext> list, Set<LivingEntity> hits) {
    }

    public void sprintServer(Player player) {
    }


    public void sprintClient(Player player, List<ValueModifier> modifiers) {
    }

    public @Nullable ResourceLocation keyAction(Player player, SoulKey key) {
        return null;
    }

    public void handleKeyAction(Player player, ResourceLocation location) {
    }

    public int fly(Player player, int flyTime) {
        return flyTime;
    }

    public List<ItemStack> dropFromLootTableAfter(Player attacker, LivingEntity target) {
        return new ArrayList<>();
    }

    public float dropFromLootTableScale(Player attacker, LivingEntity target, float scale) {
        return scale;
    }

    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        return true;
    }
}
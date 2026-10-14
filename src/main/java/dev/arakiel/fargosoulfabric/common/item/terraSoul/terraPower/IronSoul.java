package dev.arakiel.fargosoulfabric.common.item.terraSoul.terraPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.soulState.SoulState;
import dev.arakiel.fargosoulfabric.common.soulState.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class IronSoul extends SoulItem {

    public IronSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        List<Entity> items = player.level().getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(8));
        Inventory inventory = player.getInventory();
        Vec3 pos = player.position();
        for (Entity item : items) {
            if (item instanceof ItemEntity itemEntity) {
                boolean place = false;
                if (!player.isCreative()) {
                    for (int i = 0; i < inventory.items.size(); i++) {
                        ItemStack itemStack = inventory.getItem(i);
                        if (itemStack.isEmpty()) {
                            place = true;
                            break;
                        }
                        if (itemStack.getCount() < itemStack.getMaxStackSize() && ItemStack.isSameItem(itemStack, itemEntity.getItem())) {
                            place = true;
                            break;
                        }
                    }
                } else {
                    place = true;
                }
                if (place) {
                    itemEntity.setDeltaMovement(Vec3.ZERO);
                    itemEntity.teleportTo(pos.x, pos.y + 0.1, pos.z);
                    break;
                }
            } else if (item instanceof ExperienceOrb experienceOrb) {
                player.takeXpDelay = 0;
                experienceOrb.playerTouch(player);
            }
        }
    }

    @Override
    public void pickup(Player player, ItemStack itemStack) {
        Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.IRON_SOUL_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(player, soulInfo);
        }
        soulInfo.protectTime = 100;
    }

    @Override
    public void hurt(@Nullable Entity attacker, @NotNull Player target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(target, SoulStateRegister.IRON_SOUL_INFO);
        if (soulInfo != null && soulInfo.protectTime > 0) {
            modifiers.add(new ValueModifier(-0.2f, ValueOperation.ADD_MULTIPLIED_TOTAL));
        }
    }

    public static final class Info extends SoulState {

        public int protectTime = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.IRON_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (PlayerSouls.isEquipped(living, FSFItemRegister.IronSoulItem)) {
                if (protectTime > 0) {
                    protectTime--;
                }
            } else {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putInt("protectTime", protectTime);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            protectTime = tag.getInt("protectTime");
        }
    }
}

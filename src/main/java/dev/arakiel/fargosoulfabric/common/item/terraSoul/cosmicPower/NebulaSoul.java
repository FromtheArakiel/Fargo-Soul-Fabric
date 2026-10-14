package dev.arakiel.fargosoulfabric.common.item.terraSoul.cosmicPower;

import dev.arakiel.fargosoulfabric.common.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.attachment.SoulStates;
import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulState;
import dev.arakiel.fargosoulfabric.common.soulInfo.SoulStateType;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NebulaSoul extends SoulItem {

    public NebulaSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(Player player) {
        Info soulInfo = SoulStates.getSoulState(player, SoulStateRegister.NEBULA_SOUL_INFO);
        if (soulInfo != null) {
            if (player.tickCount % 20 == 0) {
                player.heal(soulInfo.nebulaPower * 0.1f);
                soulInfo.nebulaPower -= soulInfo.nebulaPower * 0.1f;
            }
            if (soulInfo.nebulaPower <= 0.01) {
                soulInfo.setRemove(true);
            }
        }
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        Info soulInfo = SoulStates.getSoulState(attacker, SoulStateRegister.NEBULA_SOUL_INFO);
        if (soulInfo == null) {
            soulInfo = new Info();
            SoulStates.putSoulState(attacker, soulInfo);
        }
        modifiers.add(new ValueModifier(attacker.getMaxHealth() * 0.04f, ValueOperation.ADD_VALUE));
        soulInfo.nebulaPower += container.getOriginalDamage() * 0.15f;
    }

    public static final class Info extends SoulState {

        public float nebulaPower = 0;

        @Override
        public SoulStateType<? extends SoulState> getType() {
            return SoulStateRegister.NEBULA_SOUL_INFO;
        }

        @Override
        public void tick(LivingEntity living) {
            if (!PlayerSouls.isEquipped(living, FSFItemRegister.NebulaSoulItem)) {
                setRemove(true);
            }
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
            CompoundTag tag = super.serializeNBT(provider);
            tag.putFloat("nebulaPower", nebulaPower);
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
            nebulaPower = tag.getFloat("nebulaPower");
        }
    }
}

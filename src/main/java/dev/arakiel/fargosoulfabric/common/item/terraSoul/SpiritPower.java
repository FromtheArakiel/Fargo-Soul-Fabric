package dev.arakiel.fargosoulfabric.common.item.terraSoul;

import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.arakiel.fargosoulfabric.FargoSoul;
import dev.arakiel.fargosoulfabric.common.entity.TerraBlade;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.common.item.base.ValueOperation;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.AttachmentEntityDamageSource;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister.*;

public class SpiritPower extends SoulItem {

    public SpiritPower(Properties properties) {
        super(properties);
    }

    @Override
    public Set<SoulItem> getSoulItemList() {
        return Set.of(ForbiddenSoulItem, HolySoulItem, AncientHolySoulItem, TekeSoulItem, GhostSoulItem);
    }

    @Override
    public void healAmount(Player player, float amount, List<ValueModifier> modifiers) {
        modifiers.add(new ValueModifier(0.7f, ValueOperation.ADD_MULTIPLIED_BASE));
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getAttributeModifiers(Player player) {
        Multimap<Holder<Attribute>, AttributeModifier> map = HashMultimap.create();
        map.put(FargoSoulAttributeRegister.SummonDamage, new AttributeModifier(FargoSoul.rl("spirit_blade_damage"), 2.0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        return map;
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (container.getSource() instanceof AttachmentEntityDamageSource damageSource && damageSource.getAttachmentEntity().getType() == SummonerAttachmentEntityRegister.TERRA_BLADE) {
            modifiers.add(new ValueModifier(2, ValueOperation.ADD_MULTIPLIED_BASE));
        }
    }

    @Override
    public void tick(Player player) {
        List<TerraBlade> blades = FargoSoulHelper.get(player).getEntityData().get(SummonerAttachmentEntityRegister.TERRA_BLADE);
        if (blades.stream().filter(terraBlade -> terraBlade.spiritPower).count() < 8) {
            TerraBlade blade = new TerraBlade();
            blade.setOwner(player);
            blade.init(blade.getInterpolatedIdleState(0));
            blade.spiritPower = true;
            FargoSoulHelper.get(player).add(blade);
        }
    }
}
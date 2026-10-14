package dev.arakiel.fargosoulfabric.common.item.terraSoul.deathPower;

import dev.arakiel.fargosoulfabric.api.damage.DamageContainer;
import dev.arakiel.fargosoulfabric.common.entity.ShadowOrb;
import dev.arakiel.fargosoulfabric.common.item.base.SoulItem;
import dev.arakiel.fargosoulfabric.common.item.base.ValueModifier;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 远古暗影魔石：攻击时概率施加黑暗；免疫失明。
 * 穿戴时维持至多 3 颗环绕玩家的魔法暗影球（不占仆从栏位），
 * 魔石被卸下后暗影球会自行移除。
 */
public class AncientShadowSoul extends SoulItem {

    public AncientShadowSoul(Properties properties) {
        super(properties);
    }

    @Override
    public void attack(@NotNull Player attacker, @NotNull LivingEntity target, @NotNull DamageContainer container, List<ValueModifier> modifiers) {
        if (attacker.getRandom().nextFloat() < 0.2f) {
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100));
        }
    }

    @Override
    public void tick(Player player) {
        List<ShadowOrb> orbs = FargoSoulHelper.get(player).getEntityData().get(SummonerAttachmentEntityRegister.SHADOW_ORB);
        if (orbs.isEmpty()) {
            for (int i = 0; i < 3; i++) {
                FargoSoulHelper.get(player).add(new ShadowOrb());
            }
        }
    }

    @Override
    public boolean effectApplicable(Player player, MobEffectInstance effectInstance) {
        if (effectInstance.is(MobEffects.BLINDNESS)) {
            return false;
        }
        return super.effectApplicable(player, effectInstance);
    }
}

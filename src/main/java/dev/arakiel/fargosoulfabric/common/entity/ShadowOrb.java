package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulItemData;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.api.FargoSoulHelper;
import dev.arakiel.fargosoulfabric.common.attachment.AttachmentEntityData;
import dev.arakiel.fargosoulfabric.common.attachmentEntity.PathNode;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("this-escape")
public class ShadowOrb extends Minion {

    public ShadowOrb() {
        super(SummonerAttachmentEntityRegister.SHADOW_ORB);
        setSlotCost(0);
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            if (!SoulItemData.isEquipped(owner, FargoSoulItemRegister.AncientShadowSoulItem)) {
                setRemove();
                return;
            }
            setCurrentPathNode(getOrbitNode(1.0f));
        }
        super.tick();
    }

    @Override
    public int getOrder() {
        return FargoSoulHelper.get(owner).getEntityData().get(getType()).indexOf(this);
    }

    @Override
    public int getSameSize() {
        return FargoSoulHelper.get(owner).getEntityData().get(getType()).size();
    }

    /**
     * 计算环绕位置：按同组内顺序均分角度，绕玩家腰部高度旋转。
     * 客户端同步使用同一公式插值渲染。
     */
    public PathNode getOrbitNode(float partialTick) {
        Player owner = getOwner();
        int total = Math.max(1, getSameSize());
        int order = getOrder();
        double angle = (owner.tickCount + partialTick) * 0.08f + order * Mth.TWO_PI / total;

        double px = Mth.lerp(partialTick, owner.xo, owner.getX());
        double py = Mth.lerp(partialTick, owner.yo, owner.getY());
        double pz = Mth.lerp(partialTick, owner.zo, owner.getZ());

        Vec3 pos = new Vec3(px, py, pz).add(Math.cos(angle) * 1.8f, owner.getBbHeight() * 0.5, Math.sin(angle) * 1.8f);
        return new PathNode(pos, 0, 0, 0);
    }

    @Override
    public PathNode getRenderNode(float partialTick) {
        return getOrbitNode(partialTick);
    }

    @Override
    public int getSearchDistance() {
        return 0;
    }

}

package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.PlayerSouls;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.FSFPlayerSummons;
import dev.arakiel.fargosoulfabric.common.attachment.SummonData;
import dev.arakiel.fargosoulfabric.common.summon.PathNode;
import dev.arakiel.fargosoulfabric.common.minion.Minion;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("this-escape")
public class ShadowOrb extends Minion {

    public ShadowOrb() {
        super(SummonTypeRegister.SHADOW_ORB);
        setSlotCost(0);
    }

    @Override
    public void tick() {
        if (!owner.level().isClientSide()) {
            if (!PlayerSouls.isEquipped(owner, FSFItemRegister.AncientShadowSoulItem)) {
                setRemove();
                return;
            }
            setCurrentPathNode(getOrbitNode(1.0f));
        }
        super.tick();
    }

    @Override
    public int getOrder() {
        return FSFPlayerSummons.get(owner).getSummons().get(getType()).indexOf(this);
    }

    @Override
    public int getSameSize() {
        return FSFPlayerSummons.get(owner).getSummons().get(getType()).size();
    }

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

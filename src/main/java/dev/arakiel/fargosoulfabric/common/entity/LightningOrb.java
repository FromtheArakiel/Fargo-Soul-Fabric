package dev.arakiel.fargosoulfabric.common.entity;

import dev.arakiel.fargosoulfabric.common.attachment.SoulTargets;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.attachment.Invulnerability;
import dev.arakiel.fargosoulfabric.common.summon.SyncFieldDispatcher;
import dev.arakiel.fargosoulfabric.common.particle.genericParticle.GenericParticleBuilder;
import dev.arakiel.fargosoulfabric.common.projectile.SummonProjectile;
import dev.arakiel.fargosoulfabric.utils.ParticleHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("this-escape")
public class LightningOrb extends SummonProjectile {

    private static final StreamCodec<RegistryFriendlyByteBuf, Set<Integer>> STREAM_CODEC = StreamCodec.of(
            (buf, ids) -> {
                buf.writeVarInt(ids.size());
                for (Integer id : ids) {
                    buf.writeVarInt(id);
                }
            },
            buf -> {
                int size = buf.readVarInt();
                Set<Integer> ids = new HashSet<>(size);
                for (int i = 0; i < size; i++) {
                    ids.add(buf.readVarInt());
                }
                return ids;
            }
    );

    private final Set<Integer> idList = new HashSet<>();

    public LightningOrb() {
        super(SummonTypeRegister.LIGHTNING_ORB);
        setDrag(1f);
        setMaxLife(40);
    }

    @Override
    protected void registerSyncFields(SyncFieldDispatcher fields) {
        super.registerSyncFields(fields);
        fields.field(STREAM_CODEC, this::getIdList, value -> {
            idList.clear();
            idList.addAll(value);
        });
    }

    @Override
    public void tick() {
        Level level = owner.level();
        if (!level.isClientSide()) {
            idList.clear();
            List<LivingEntity> entities = SoulTargets.get(owner).getEntitiesInRadius(getPos(), 3, null);
            for (LivingEntity living : entities) {
                idList.add(living.getId());
                Invulnerability.attack(living)
                        .attacker(getUuid())
                        .damageSource(getDamageSource())
                        .damageAmount(getDamage())
                        .invincibleTime(3)
                        .apply();
            }
        }
        if (level.isClientSide()) {
            for (Integer id : idList) {
                if (level.getEntity(id) instanceof LivingEntity living) {
                    ParticleHelper.create(owner.level())
                            .generic(GenericParticleBuilder.create()
                                    .centerColor(0x38ffec)
                                    .edgeColor(0x2fc1ae)
                                    .lifetime(5)
                                    .lifetimeRandom(5)
                                    .spin(0.3f)
                                    .spinRandom(0.05F)
                                    .friction(0.75F)
                                    .scale(0.025f)
                                    .scaleRandom(0.005f)
                            )
                            .pos(living.getBoundingBox().getCenter())
                            .offset(0.15)
                            .velocity(getVelocity())
                            .count(1)
                            .speed(0.25)
                            .spread(2)
                            .emit();
                }
            }
        }
        super.tick();
    }

    @Override
    public void onRemove() {
        ParticleHelper.create(owner.level())
                .generic(GenericParticleBuilder.create()
                        .centerColor(0x38ffec)
                        .edgeColor(0x2fc1ae)
                        .lifetime(5)
                        .lifetimeRandom(5)
                        .spin(0.3f)
                        .spinRandom(0.05F)
                        .friction(0.75F)
                        .scale(0.025f)
                        .scaleRandom(0.005f)
                )
                .pos(getPos())
                .offset(0.15)
                .velocity(getVelocity())
                .count(4)
                .speed(0.25)
                .spread(2)
                .emit();
    }

    public Set<Integer> getIdList() {
        return idList;
    }
}

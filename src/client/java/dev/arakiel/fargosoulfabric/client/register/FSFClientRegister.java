package dev.arakiel.fargosoulfabric.client.register;

import dev.arakiel.fargosoulfabric.register.FSFRegisters;
import dev.arakiel.fargosoulfabric.client.network.FSFClientPackets;
import dev.arakiel.fargosoulfabric.client.particle.genericParticle.GenericParticleProvider;
import dev.arakiel.fargosoulfabric.common.component.RarityAnimation;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.Minecraft;

/**
 * The client half of the registrations the summon library needs.
 *
 * <p>A dedicated server never runs this: the particle implementation, the packet receivers and the
 * rarity animation stay out of the common code.
 */
public final class FSFClientRegister {

    private FSFClientRegister() {
    }

    public static void init() {
        ParticleFactoryRegistry.getInstance()
                .register(FSFRegisters.GENERIC_PARTICLE, GenericParticleProvider::new);
        FSFClientPackets.register();
        RarityAnimation.setTimeSource(FSFClientRegister::clientTime);
    }

    /** The tick counter of the running client; {@code null} while no level is loaded. */
    private static Double clientTime() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return null;
        }
        return minecraft.level.getGameTime() + (double) minecraft.getTimer().getGameTimeDeltaPartialTick(true);
    }
}
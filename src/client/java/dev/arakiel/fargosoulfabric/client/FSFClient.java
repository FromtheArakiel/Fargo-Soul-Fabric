package dev.arakiel.fargosoulfabric.client;

import dev.arakiel.fargosoulfabric.client.register.FSFKeyRegister;
import dev.arakiel.fargosoulfabric.client.register.FSFModelRegister;
import dev.arakiel.fargosoulfabric.client.register.FSFClientRegister;
import dev.arakiel.fargosoulfabric.client.register.SummonRenderRegister;
import net.fabricmc.api.ClientModInitializer;

/** The client entry point: everything that only exists on a client. */
public class FSFClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FSFClientRegister.init();
        FSFKeyRegister.init();
        FSFModelRegister.init();
        SummonRenderRegister.init();
        ClientEvent.init();
        FSFClientListeners.init();
    }
}

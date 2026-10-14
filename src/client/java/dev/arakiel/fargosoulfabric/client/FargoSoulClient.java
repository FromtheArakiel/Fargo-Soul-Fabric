package dev.arakiel.fargosoulfabric.client;

import dev.arakiel.fargosoulfabric.client.register.FargoSoulKeyRegister;
import dev.arakiel.fargosoulfabric.client.register.FargoSoulModelRegister;
import dev.arakiel.fargosoulfabric.client.register.FargoSoulClientRegister;
import dev.arakiel.fargosoulfabric.client.register.SummonerAttachmentEntityRenderRegister;
import net.fabricmc.api.ClientModInitializer;

/** The client entry point: everything that only exists on a client. */
public class FargoSoulClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FargoSoulClientRegister.init();
        FargoSoulKeyRegister.init();
        FargoSoulModelRegister.init();
        SummonerAttachmentEntityRenderRegister.init();
        ClientEvent.init();
        FargoSoulClientListeners.init();
    }
}

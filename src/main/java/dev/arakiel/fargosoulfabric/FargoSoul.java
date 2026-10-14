package dev.arakiel.fargosoulfabric;

import dev.arakiel.fargosoulfabric.common.Event;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulAttributeRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulCreativeModeTabRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulNetworkPacketRegister;
import dev.arakiel.fargosoulfabric.register.FargoSoulRegisters;
import dev.arakiel.fargosoulfabric.register.FargoSoulSoulInfoRegister;
import dev.arakiel.fargosoulfabric.register.SummonerAttachmentEntityRegister;
import dev.arakiel.fargosoulfabric.common.FargoSoulListeners;
import dev.arakiel.fargosoulfabric.register.FargoSoulItemRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

/**
 * The mod entry point.
 *
 * <p>The summon library this mod grew out of was a mod of its own; it ships inside this one now, so
 * its registrations are part of this initializer and the order is explicit: the library first (the
 * items need its components), then everything the mod adds on top.
 */
public class FargoSoul implements ModInitializer {

    public static final String MODID = "fargosoulfabric";

    public static final FargoSoulItemRegistries REGISTRIES = FargoSoulItemRegistries.create(MODID);

    @Override
    public void onInitialize() {
        FargoSoulAttributeRegister.init();
        FargoSoulListeners.init();
        FargoSoulRegisters.init();
        FargoSoulAttachmentRegister.init();
        FargoSoulMobEffectRegister.init();
        FargoSoulItemRegister.init();
        FargoSoulCreativeModeTabRegister.init();
        SummonerAttachmentEntityRegister.init();
        FargoSoulSoulInfoRegister.init();
        FargoSoulNetworkPacketRegister.init();
        Event.init();
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path.toLowerCase());
    }
}

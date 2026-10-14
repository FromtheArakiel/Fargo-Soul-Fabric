package dev.arakiel.fargosoulfabric;

import dev.arakiel.fargosoulfabric.common.Event;
import dev.arakiel.fargosoulfabric.register.FSFAttachmentRegister;
import dev.arakiel.fargosoulfabric.register.FSFAttributeRegister;
import dev.arakiel.fargosoulfabric.register.FSFCreativeModeTabRegister;
import dev.arakiel.fargosoulfabric.register.FSFItemRegister;
import dev.arakiel.fargosoulfabric.register.FSFMobEffectRegister;
import dev.arakiel.fargosoulfabric.register.FSFNetworkPacketRegister;
import dev.arakiel.fargosoulfabric.register.FSFRegisters;
import dev.arakiel.fargosoulfabric.register.SoulStateRegister;
import dev.arakiel.fargosoulfabric.register.SummonTypeRegister;
import dev.arakiel.fargosoulfabric.common.FSFListeners;
import dev.arakiel.fargosoulfabric.register.FSFItemRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

/**
 * The mod entry point.
 *
 * <p>The summon library this mod grew out of was a mod of its own; it ships inside this one now, so
 * its registrations are part of this initializer and the order is explicit: the library first (the
 * items need its components), then everything the mod adds on top.
 */
public class FSF implements ModInitializer {

    public static final String MODID = "fargosoulfabric";

    public static final FSFItemRegistries REGISTRIES = FSFItemRegistries.create(MODID);

    @Override
    public void onInitialize() {
        FSFAttributeRegister.init();
        FSFListeners.init();
        FSFRegisters.init();
        FSFAttachmentRegister.init();
        FSFMobEffectRegister.init();
        FSFItemRegister.init();
        FSFCreativeModeTabRegister.init();
        SummonTypeRegister.init();
        SoulStateRegister.init();
        FSFNetworkPacketRegister.init();
        Event.init();
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path.toLowerCase());
    }
}

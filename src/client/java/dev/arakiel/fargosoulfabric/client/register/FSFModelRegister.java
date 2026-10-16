package dev.arakiel.fargosoulfabric.client.register;

import dev.arakiel.fargosoulfabric.FSF;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.ResourceLocation;

public final class FSFModelRegister {

    private static final List<ResourceLocation> MODELS = new ArrayList<>();

    public static final ResourceLocation TERRA_BLADE = standalone("minion/terra_blade");

    public static final ResourceLocation CHLOROPHYTE_CRYSTAL = standalone("minion/chlorophyte_crystal");

    public static final ResourceLocation SHADOW_ORB = standalone("minion/shadow_orb");

    private FSFModelRegister() {
    }

    public static void init() {
        ModelLoadingPlugin.register(context -> context.addModels(MODELS));
    }

    private static ResourceLocation standalone(String path) {
        ResourceLocation id = FSF.rl(path);
        MODELS.add(id);
        return id;
    }
}

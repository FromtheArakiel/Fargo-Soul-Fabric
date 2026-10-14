package dev.arakiel.fargosoulfabric.client.register;

import dev.arakiel.fargosoulfabric.FSF;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

/** The standalone models of the summons. */
public final class FSFModelRegister {

    private static final List<ResourceLocation> MODELS = new ArrayList<>();

    public static final ModelResourceLocation TERRA_BLADE = standalone("minion/terra_blade");

    public static final ModelResourceLocation CHLOROPHYTE_CRYSTAL = standalone("minion/chlorophyte_crystal");

    public static final ModelResourceLocation SHADOW_ORB = standalone("minion/shadow_orb");

    private FSFModelRegister() {
    }

    public static void init() {
        ModelLoadingPlugin.register(context -> context.addModels(MODELS));
    }

    private static ModelResourceLocation standalone(String path) {
        ResourceLocation id = FSF.rl(path);
        MODELS.add(id);
        return new ModelResourceLocation(id, "standalone");
    }
}

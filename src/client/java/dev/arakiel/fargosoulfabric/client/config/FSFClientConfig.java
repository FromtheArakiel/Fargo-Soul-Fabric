package dev.arakiel.fargosoulfabric.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The client settings, kept in config/fargosoulfabric-client.json. */
public final class FSFClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("fargosoulfabric");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("fargosoulfabric-client.json");

    private static final FSFClientConfig INSTANCE = load();

    /**
     * Damage numbers are a taste thing rather than a mechanic, so they start switched off and are
     * turned on in the config file.
     */
    private boolean damageNumbers = false;

    /** How far away a damage number may be shown, in blocks. */
    private double damageNumberRange = 32.0;

    public static boolean damageNumbers() {
        return INSTANCE.damageNumbers;
    }

    /** The squared range, ready to compare against a squared distance. */
    public static double damageNumberRangeSquared() {
        double range = INSTANCE.damageNumberRange;
        return range <= 0.0 ? Double.MAX_VALUE : range * range;
    }

    private static FSFClientConfig load() {

        if (Files.exists(FILE)) {

            try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                FSFClientConfig config = GSON.fromJson(reader, FSFClientConfig.class);

                if (config != null) {
                    return config;
                }
            } catch (Exception exception) {
                LOGGER.warn("Could not read {}, using the defaults", FILE, exception);
            }
        }
        FSFClientConfig config = new FSFClientConfig();

        try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (Exception exception) {
            LOGGER.warn("Could not write the default config to {}", FILE, exception);
        }
        return config;
    }

    private FSFClientConfig() {
    }
}

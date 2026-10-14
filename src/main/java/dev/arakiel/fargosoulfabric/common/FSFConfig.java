package dev.arakiel.fargosoulfabric.common;

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

/** The game settings, kept in config/fargosoulfabric.json. */
public final class FSFConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger("fargosoulfabric");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("fargosoulfabric.json");

    private static final FSFConfig INSTANCE = load();

    /**
     * Draws a ring every ten seconds around a soul whose effect reaches that far, one circle at the
     * effect's own radius.
     */
    private boolean auraRings = true;

    public static boolean auraRings() {
        return INSTANCE.auraRings;
    }

    private static FSFConfig load() {

        if (Files.exists(FILE)) {

            try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                FSFConfig config = GSON.fromJson(reader, FSFConfig.class);

                if (config != null) {
                    return config;
                }
            } catch (Exception exception) {
                LOGGER.warn("Could not read {}, using the defaults", FILE, exception);
            }
        }
        FSFConfig config = new FSFConfig();

        try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (Exception exception) {
            LOGGER.warn("Could not write the default config to {}", FILE, exception);
        }
        return config;
    }

    private FSFConfig() {
    }
}

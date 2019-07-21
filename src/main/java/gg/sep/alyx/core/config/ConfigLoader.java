package gg.sep.alyx.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import gg.sep.alyx.util.ModelParser;
import gg.sep.alyx.util.result.Err;
import gg.sep.alyx.util.result.Ok;
import gg.sep.alyx.util.result.Result;

/**
 * Utility class responsible for loading and writing the main bot's config {@link AlyxConfig}.
 */
@Log4j2
@UtilityClass
public class ConfigLoader {
    private static final String APP_NAME = "alyx-discord-bot";
    private static final String APP_AUTHOR = "sep";
    private static final String CONFIG_VERSION = "0.1"; // Config Version will rarely change.
    private static final boolean ROAMING = true;

    private static final AppDirs APP_DIRS = AppDirsFactory.getInstance();
    private static final String DATA_DIR = APP_DIRS.getUserDataDir(APP_NAME, CONFIG_VERSION, APP_AUTHOR, ROAMING);
    private static final String CONFIG_DIR = APP_DIRS.getUserConfigDir(APP_NAME, CONFIG_VERSION, APP_AUTHOR, ROAMING);
    private static final String ALYX_CONFIG_PATH = CONFIG_DIR + "/alyx-discord-bot.json";

    /**
     * Returns the default path to Alyx's config file.
     * @return The default path to Alyx's config file.
     */
    public static File getAlyxConfigFile() {
        return new File(ALYX_CONFIG_PATH);
    }

    /**
     * Returns the default bot entry config data path.
     * @return The default bot entry config data path.
     */
    public static File getBotConfigDir() {
        return new File(DATA_DIR);
    }

    /**
     * Checks if the main Alyx config file exists and is readable.
     * @return {@code true} if the main Alyx config file exists and is readable, else {@code false}.
     */
    public static boolean configFileExists() {
        final File configFile = getAlyxConfigFile();
        return configFile.canRead();
    }

    /**
     * Attempts to load the Alyx config file. If any {@link IOException} gets thrown, will return empty instead.
     * @return Optional of the default config file if successful, otherwise empty.
     */
    public static Optional<AlyxConfig> loadConfig() {
        try {
            final InputStream inputStream = new FileInputStream(ALYX_CONFIG_PATH);
            final String fileContents = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));

            return ModelParser.parseJson(fileContents, AlyxConfig.class);
        } catch (final IOException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    /**
     * Updates a bot entry in the main Alyx config file.
     * =
     * If the bot entry doesn't exist, it is created.
     * @param bot The bot entry to add or update.
     */
    public void updateBotConfig(final BotEntry bot) {
        final Optional<AlyxConfig> currentConfig = loadConfig();
        currentConfig.ifPresentOrElse(
            c -> {
                c.getBots().put(bot.getBotName(), bot);
                writeConfig(c);
            },
            () -> {
                if (!configFileExists()) {
                    createConfigFile();
                }
                final AlyxConfig config = AlyxConfig.builder()
                    .bots(Map.of(bot.getBotName(), bot))
                    .build();
                writeConfig(config);
            });
    }

    /**
     * Writes out the full {@link AlyxConfig} JSON file, overwriting if it already exists.
     * @param alyxConfig The {@link AlyxConfig} to convert to JSON and write out.
     * @return Result with an error message if writing the config failed.
     */
    public Result<?, String> writeConfig(final AlyxConfig alyxConfig) {
        final String jsonString = new GsonBuilder().setPrettyPrinting().create()
            .toJson(alyxConfig, AlyxConfig.class);
        try {
            if (!configFileExists()) {
                final Result<?, String> createConfigFile = createConfigFile();
                if (createConfigFile.isErr()) {
                    return createConfigFile;
                }
            }
            Files.writeString(getAlyxConfigFile().toPath(), jsonString);
            return Ok.of(true);
        } catch (final IOException e) {
            log.error(e);
            return Err.of("Error occurred while attempting to write Alyx config: " + e);
        }
    }

    /**
     * Ensure the master config directory exists and that we can write to it.
     * @return An error message which can be surfaced to the user if there's a problem with the directory.
     */
    private static Result<?, String> ensureConfigDirectory() {
        final File configDir = new File(CONFIG_DIR);
        // if it exists and we can write to it, we're good
        if (configDir.exists()) {
            if (configDir.canWrite()) {
                return Ok.of(true);
            }
            return Err.of("We don't have permissions to write to the config directory: " + configDir);
        } else if (!configDir.mkdirs()) {
            return Err.of("Failed to create the config directory: " + configDir);
        }
        return Ok.of(true);
    }

    /**
     * Returns an {@link Ok} result if the config file was created successfully, otherwise an {@link Err}.
     * @return An {@link Ok} result if the config file was created successfully, otherwise an {@link Err}.
     */
    private static Result<?, String> createConfigFile() {
        final File configFile = getAlyxConfigFile();
        final Result<?, String> ensureConfigDir = ensureConfigDirectory();
        if (ensureConfigDir.isErr()) {
            return ensureConfigDir;
        }

        try {
            configFile.createNewFile();
        } catch (final IOException e) {
            log.error(e);
            return Err.of("Failed to create the config file: " + e.getMessage());
        }
        return Ok.of(true);
    }
}

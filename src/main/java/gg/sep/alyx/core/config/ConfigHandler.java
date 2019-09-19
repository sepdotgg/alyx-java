package gg.sep.alyx.core.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import gg.sep.alyx.model.config.AlyxConfig;
import gg.sep.alyx.model.config.BotEntry;
import gg.sep.alyx.util.ModelParser;

/**
 * {@link ConfigHandler} is responsible for creating, updating, and reading an Alyx config file.
 *
 * The config file used is specified by the user as a CLI argument when launching Alyx,
 * or a default "app dirs" file is created and used based on the user's operating system.
 */
@Log4j2
@RequiredArgsConstructor
public class ConfigHandler {

    private static final String APP_NAME = "alyx-discord-bot";
    private static final String APP_AUTHOR = "sep";
    private static final String CONFIG_VERSION = "0.1"; // Config Version will rarely change.
    private static final boolean ROAMING = true;

    private static final AppDirs APP_DIRS = AppDirsFactory.getInstance();
    private static final String DATA_DIR = APP_DIRS.getUserDataDir(APP_NAME, CONFIG_VERSION, APP_AUTHOR, ROAMING);
    private static final String CONFIG_DIR = APP_DIRS.getUserConfigDir(APP_NAME, CONFIG_VERSION, APP_AUTHOR, ROAMING);
    private static final String ALYX_CONFIG_PATH = CONFIG_DIR + "/alyx-discord-bot.json";

    public static final Path ALYX_DEFAULT_CONFIG_FILE = Path.of(ALYX_CONFIG_PATH);
    public static final Path ALYX_DEFAULT_DATA_DIR = Path.of(DATA_DIR);

    @Getter private final Path configPath;

    /**
     * Attempts to load and parse the config file into an {@link AlyxConfig}.
     * @return Returns an {@link Optional} of the {@link AlyxConfig} if successful, otherwise an empty Optional.
     */
    public Optional<AlyxConfig> loadConfig() {
        try {
            return ModelParser.parseJson(readConfig(), AlyxConfig.class);
        } catch (final IOException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    /**
     * Updates or creates a bot entry in the config file, overwriting any existing bot entry with the same name.
     * @param botEntry Bot Entry to update in the config. If it doesn't exist, it will be created.
     * @throws IOException Exception thrown if writing to the config file failed.
     */
    public void updateBotEntry(final BotEntry botEntry) throws IOException {
        final Optional<AlyxConfig> currentConfig = loadConfig();

        AlyxConfig outputConfig = currentConfig.orElse(null);
        if (outputConfig == null) {
            outputConfig = generateBlankConfig();
        }
        outputConfig.getBots().put(botEntry.getBotName(), botEntry);
        writeConfig(outputConfig);
    }

    /**
     * Write the AlyxConfig to the config file, overwriting any existing configuration.
     * @param alyxConfig Alyx Config to write to disk.
     * @throws IOException Exception thrown if writing to the config file failed.
     */
    public void writeConfig(final AlyxConfig alyxConfig) throws IOException {
        ensureConfigFile();
        Files.writeString(configPath, alyxConfig.toPrettyJson());
    }

    private String readConfig() throws IOException {
        return Files.readString(configPath);
    }

    private AlyxConfig generateBlankConfig() throws IOException {
        final AlyxConfig blankConfig = AlyxConfig.empty();
        writeConfig(blankConfig);
        return blankConfig;
    }

    private void ensureConfigFile() throws IOException {
        final Path parentPath = configPath.getParent();
        if (parentPath == null) {
            throw new IOException("Config file path cannot be a root directory.");
        }
        if (!Files.exists(configPath)) {
            Files.createDirectories(parentPath);
            Files.createFile(configPath);
        }
    }
}

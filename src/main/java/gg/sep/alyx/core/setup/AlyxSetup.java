package gg.sep.alyx.core.setup;

import java.io.File;
import java.util.List;
import java.util.Optional;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

import gg.sep.alyx.core.config.AlyxConfig;
import gg.sep.alyx.core.config.BotEntry;
import gg.sep.alyx.core.config.ConfigLoader;
import gg.sep.alyx.core.storage.StorageType;
import gg.sep.alyx.util.result.Err;
import gg.sep.alyx.util.result.Ok;
import gg.sep.alyx.util.result.Result;

/**
 * Utility class for executing the initial Alyx configuration setups.
 */
@Log4j2
@UtilityClass
public class AlyxSetup {

    private static final String HEADER =
        "    ⣶⣶⣶⣶⡆\n" +
        "    ⠛⠛⢻⣿⣿⡀             WELCOME TO ALYX\n" +
        "      ⢀⣿⣿⣷             ________________\n" +
        "     ⢀⣾⣿⣿⣿⣇\n" +
        "    ⢠⣿⣿⡟⢹⣿⣿⡆           Discord Bot written in Java\n" +
        "   ⣰⣿⣿⠏⠀⠀⢻⣿⣿⡄\n" +
        "  ⣴⣿⡿⠃⠀⠀⠀⠈⢿⣿⣷⣤⣤⡆      GitHub: https://github.com/seputaes/alyx\n" +
        " ⠾⠿⠿⠁⠀⠀⠀⠀⠀⠘⣿⣿⡿⠿⠛\n\n\n";

    /**
     * Starts a new Setup session, returning an {@link Ok} Result if it was successful containing the completed config.
     *
     * If it was not successful, returns an {@link Err} containing the error message.
     * @return An {@link Ok} containing the completed {@link AlyxConfig} if successful, otherwise an {@link Err}.
     */
    public static Result<AlyxConfig, String> enterSetup() {
        // check if the config file exists
        if (!ConfigLoader.configFileExists()) {
            setupNewConfig();
        }

        final Optional<AlyxConfig> loadConfig = ConfigLoader.loadConfig();
        final AlyxConfig alyxConfig = loadConfig.orElseThrow(() -> new RuntimeException("Failed to load Alyx config."));

        final TextIO textIO = TextIoFactory.getTextIO();
        final TextTerminal terminal = textIO.getTextTerminal();

        welcomeSetup(terminal);

        // get a name for the bot, confirm it is not used
        final String botName = getBotName(textIO);
        if (alyxConfig.getBots().containsKey(botName)) {
            final boolean confirmed = confirmOverwriteBot(textIO);
            if (!confirmed) {
                terminal.println("Exiting setup mode.");
                terminal.dispose();
                return Err.of("Could not complete the setup process.");
            }
        }
        terminal.println();

        // get a storage engine for the bot
        final StorageType storageType = getStorageType(textIO);
        terminal.println();

        // finally, get a config location for the bot's files
        final File botConfigPath = getBotConfigDir(textIO, ConfigLoader.getBotConfigDir());
        final boolean configDirConfirmed = confirmBotConfigDir(textIO, botConfigPath);
        terminal.println();

        if (!configDirConfirmed) {
            terminal.println("Existing setup mode");
            terminal.dispose();
            return Err.of("Could not complete the setup process.");
        }

        final BotEntry botEntry = BotEntry.builder()
            .botName(botName)
            .storageType(storageType)
            .configDir(botConfigPath)
            .build();

        ConfigLoader.updateBotConfig(botEntry);
        alyxConfig.getBots().put(botName, botEntry);
        terminal.dispose();
        return Ok.of(alyxConfig);
    }

    /**
     * Writes (overwrites) the config file to an empty config.
     */
    private static void setupNewConfig() {
        ConfigLoader.writeConfig(AlyxConfig.EMPTY);
    }

    private static void welcomeSetup(final TextTerminal terminal) {
        terminal.print(HEADER);

        terminal.print(
            "SETUP MODE\n" +
            "-----------------\n" +
            "This setup will walk you through the process of setting up a new bot configuration.\n\n"
        );

    }

    /**
     * Using the supplied {@code textIO} instance, get the bot's name that is being set up.
     * @param textIO An existing {@link TextIO} instance.
     * @return The chosen name for the bot.
     */
    private static String getBotName(final TextIO textIO) {
        final String stepText =
            "Step 1 - Choosing name for your bot\n" +
            "--------------------------------------------\n";
        final String descText = "This name is what will identify your bot in the config.";

        final String botName = textIO
            .newStringInputReader()
            .withValueChecker((enteredText, n) -> {
                if (enteredText.trim().contains(" ")) {
                    return List.of("The bot name cannot contain spaces.");
                }
                return null;
            })
            .read(List.of(stepText, descText, "Name:"));
        return botName.trim();
    }

    /**
     * Asks the user to confirm overwriting a bot configuration, if one one they chose already exists.
     * @param textIO An existing {@link TextIO} instance.
     * @return Boolean indicating whether the user has confirmed the overwrite.
     */
    private static boolean confirmOverwriteBot(final TextIO textIO) {
        return textIO
            .newBooleanInputReader()
            .read("\n[WARNING] A bot with that name already exists. Confirm overwrite?");
    }

    /**
     * Using the supplied {@code textIO} instance, get the bot's storage type to use.
     * @param textIO An existing {@link TextIO} instance.
     * @return The selected storage type for the bot.
     */
    private static StorageType getStorageType(final TextIO textIO) {
        final String stepText =
            "Step 2 - Choosing a Storage Engine\n" +
            "--------------------------------------------\n";

        final String descText = "Select your preferred engine for storing the bot's config and settings. " +
            "If you're not sure, leave this as 'json'";
        return textIO
            .newEnumInputReader(StorageType.class)
            .read(List.of(stepText, descText));
    }

    /**
     * Using the supplied {@code textIO} instance, get the bot's config storage directory.
     * @param textIO An existing {@link TextIO} instance.
     * @param defaultPath The default path to store the bot's config.
     * @return The user selected path to store the bot's config.
     */
    private static File getBotConfigDir(final TextIO textIO, final File defaultPath) {
        final String stepText =
            "Step 3 - Choosing a Config Storage Directory\n" +
            "--------------------------------------------\n";

        final String descText = "Enter a directory to use for storing the bot's configuration files and settings." +
            "We've selected a default location based on your operating system, but you can change this.\n";

        final String path = textIO
            .newStringInputReader()
            .withDefaultValue(defaultPath.getAbsolutePath())
            .withValueChecker(AlyxSetup::checkConfigFilePath)
            .read(List.of(stepText, descText, "Default: "));
        return new File(path);
    }

    /**
     * Asks the user to confirm the config directory path that was chosen.
     * @param textIO An existing {@link TextIO} instance.
     * @param path The user selected path to store the bot's config.
     * @return Boolean indicating whether the user has confirmed the config directory.
     */
    private static boolean confirmBotConfigDir(final TextIO textIO, final File path) {
        return textIO
            .newBooleanInputReader()
            .read(String.format("%n%nYou have chosen your bot's data directory: %s%n Confirm?", path));
    }

    /**
     * Method which is passed as a {@link org.beryx.textio.InputReader.ValueChecker} to verify the config directory.
     *
     * Confirms whether the directory exists, is a directory, is an absolute path, and that we can write to it.
     * @param path Path to the bot's config directory.
     * @param inputName Name of the input option from {@link org.beryx.textio.InputReader}, but not used here.
     * @return List of strings to print to the user if an error, or {@code null} if success.
     */
    private static List<String> checkConfigFilePath(final String path, final String inputName) {
        final File file = new File(path);
        // make sure it's absolute
        if (!file.isAbsolute()) {
            return List.of("The path must be an absolute directory.");
        }

        if ((!file.exists() && !file.mkdirs()) || (!file.isDirectory() || !file.canWrite())) {
            // error

            return List.of(
                String.format("The chosen path is not valid: %s%n" +
                    "Please check permissions and that it is a directory.", path));
        }
        return null;
    }
}

package gg.sep.alyx.core.setup;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.security.auth.login.LoginException;

import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import gg.sep.alyx.core.config.ConfigHandler;
import gg.sep.alyx.core.storage.StorageType;
import gg.sep.alyx.model.config.AlyxConfig;
import gg.sep.alyx.model.config.BotConfig;
import gg.sep.alyx.model.config.BotEntry;
import gg.sep.result.Err;
import gg.sep.result.Ok;
import gg.sep.result.Result;

/**
 * Utility class for executing the initial Alyx configuration setups.
 */
@Builder
@Log4j2
public class AlyxSetup {

    private ConfigHandler configHandler;
    private Path defaultDataDir;
    private TextIO textIO;

    private static final Character COMMAND_PREFIX_DEFAULT = '!';

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
     * Starts the Setup process using the supplied configuration and data directories passed in the constructor.
     * @return A {@link Result} with the created config {@link BotEntry} record if successful, or a string error
     *         if failure.
     */
    public Result<BotEntry, String> startSetup() {
        if (Files.isDirectory(configHandler.getConfigPath())) {
            return Err.of("The config file must be a file, not a directory.");
        }
        // check if we should generate a new config file
        if (Files.exists(configHandler.getConfigPath()) && !Files.isWritable(configHandler.getConfigPath())) {
            return Err.of("We do not have permission to write to that config file.");
        }

        try {
            // write a new file if it doesn't exist
            if (!Files.exists(configHandler.getConfigPath())) {
                configHandler.writeConfig(AlyxConfig.empty());
            }
            // load the config file back up
            final AlyxConfig alyxConfig = configHandler.loadAlyxConfig().orElseThrow(() ->
                new IOException("Failed to load the config file: " + configHandler.getConfigPath()));

            final Result<BotEntry, String> setupSteps = performSetupSteps(alyxConfig);
            textIO.dispose();
            if (setupSteps.isErr()) {
                return setupSteps;
            }
            configHandler.updateBotEntry(setupSteps.unwrap());
            return Ok.of(setupSteps.unwrap());

        } catch (final IOException e) {
            return Err.of("Error during setup: " + e);
        } finally {
            textIO.dispose();
        }
    }

    private Result<BotEntry, String> performSetupSteps(final AlyxConfig alyxConfig) {
        welcomeSetup(textIO.getTextTerminal());

        final String botName = botName();
        if (alyxConfig.getBots().containsKey(botName)) {
            // duplicate bot exists, confirm that we want to overwrite
            if (!confirmOverwriteBot()) {
                textIO.getTextTerminal().println("Exiting setup mode.");
                textIO.getTextTerminal().dispose();
                return Err.of("Exited setup mode before completion");
            }
        }
        final char botCmdPrefix = botPrefix();

        final StorageType storageType = storageType();
        textIO.getTextTerminal().println();

        // get the config location
        final Path configDirPath = configDirPath();
        final boolean confirmConfigDirPath = confirmConfigDirPath(configDirPath);
        textIO.getTextTerminal().println();

        if (!confirmConfigDirPath) {
            textIO.getTextTerminal().println("Exiting setup mode.");
            textIO.getTextTerminal().dispose();
            return Err.of("Exited setup mode before completion");
        }

        final String discordToken = discordToken();
        final BotEntry botEntry = BotEntry.builder()
            .botName(botName)
            .storageType(storageType)
            .dataDir(configDirPath)
            .build();
        // write the default bot config
        final BotConfig defaultBotConfig = BotConfig.builder()
            .botName(botName)
            .commandPrefix(botCmdPrefix)
            .discordToken(discordToken)
            .build();
        try {
            configHandler.updateBotConfig(botEntry, defaultBotConfig);
        } catch (final IOException e) {
            return Err.of(e.getMessage());
        }
        return Ok.of(botEntry);
    }

    private void welcomeSetup(final TextTerminal terminal) {
        terminal.print(HEADER);
        terminal.print(
            "SETUP MODE\n" +
                "-----------------\n" +
                "This setup will walk you through the process of setting up a new bot configuration.\n\n"
        );
    }

    private String botName() {
        final String stepText =
            "Step 1 - Choosing name for your bot\n" +
                "--------------------------------------------\n";
        final String descText = "This name is what will identify your bot in the config.";

        final String botName = textIO
            .newStringInputReader()
            .withValueChecker(AlyxSetup::botNameChecker)
            .read(List.of(stepText, descText, "Name:"));
        return botName.trim();
    }

    private boolean confirmOverwriteBot() {
        return textIO
            .newBooleanInputReader()
            .read("\n[WARNING] A bot with that name already exists. Confirm overwrite?");
    }

    private char botPrefix() {
        final String stepText =
            "Step 2 - Choose a command prefix for your bot\n" +
                "--------------------------------------------\n";
        final String descText = "This is the character that will be used to prefix bot commands (eg. enter ! for !ping)";
        return textIO
            .newCharInputReader()
            .withDefaultValue(COMMAND_PREFIX_DEFAULT)
            .read(List.of(stepText, descText, "Prefix:"));
    }

    private StorageType storageType() {
        final String stepText =
            "Step 3 - Choosing a Storage Engine\n" +
                "--------------------------------------------\n";
        final String descText = "Select your preferred engine for storing the bot's config and settings. " +
            "If you're not sure, leave this as 'json'";

        return textIO
            .newEnumInputReader(StorageType.class)
            .read(List.of(stepText, descText));
    }

    private Path configDirPath() {
        final String stepText =
            "Step 4 - Choosing a Config Storage Directory\n" +
                "--------------------------------------------\n";
        final String descText = "Enter a directory to use for storing the bot's configuration files and settings." +
            "We've selected a default location based on your operating system, but you can change this.\n";

        final String path = textIO
            .newStringInputReader()
            .withDefaultValue(defaultDataDir.toAbsolutePath().toString())
            .withValueChecker(AlyxSetup::configDataPathValueChecker)
            .read(List.of(stepText, descText, "Default: "));
        return Path.of(path);
    }

    private String discordToken() {
        final String stepText =
            "Step 5 - Enter the bot's Discord token\n" +
                "--------------------------------------------\n";
        final String descText = "Enter the Discord token that the bot will use to authenticate with Discord.\n";

        return textIO
            .newStringInputReader()
            .withInputMasking(true)
            .withInputTrimming(true)
            .withValueChecker(AlyxSetup::discordTokenValueChecker)
            .read(List.of(stepText, descText, "Token: "));
    }

    /**
     * Asks the user to confirm the config directory path that was chosen.
     * @param path The user selected path to store the bot's config.
     * @return Boolean indicating whether the user has confirmed the config directory.
     */
    private boolean confirmConfigDirPath(final Path path) {
        return textIO
            .newBooleanInputReader()
            .read(String.format("%n%nYou have chosen your bot's data directory: %s%n Confirm?",
                path.toAbsolutePath().toString()));
    }

    /**
     * IMPORTANT: Exists for testing purposes only.
     * @param enteredText Text entered by the user.
     * @param itemName Name of the item selected. Not used.
     * @return A list of responses in the case of an error, or null if the check passed.
     */
    public static List<String> botNameChecker(final String enteredText, final String itemName) {
        if (enteredText.trim().contains(" ") || enteredText.trim().length() < 1) {
            return List.of("The bot name cannot contain spaces and must be at least 1 character long.");
        }
        return null;
    }

    /**
     * Tests whether the config data path is valid.
     *
     * @param path Path value of the config data dir path entered by the user.
     * @param inputName Name of the item selected. Not used.
     * @return A list of responses in the case of an error, or null if the check passed.
     */
    private static List<String> configDataPathValueChecker(final String path, final String inputName) {
        final Path configPath = Path.of(path);
        if (!configPath.isAbsolute()) {
            return List.of("The path must be an absolute directory");
        }
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (final IOException e) {
                return List.of("Failed to create that directory. Please check permissions.");
            }
        } else if (!Files.isDirectory(configPath)) {
            return List.of("The supplied path must be a directory");
        } else if (!Files.isWritable(configPath)) {
            return List.of("We do not have permission to write to that directory.");
        }

        return null;
    }

    /**
     * Tests whether the Discord token is valid.
     *
     * @param discordToken Discord token provided by the user.
     * @param inputName Name of the item selected. Not used.
     * @return A list of responses in the case of an error, or null if the check passed.
     */
    private static List<String> discordTokenValueChecker(final String discordToken, final String inputName) {
        if (StringUtils.isAllBlank(discordToken)) {
            return List.of("Invalid token. Received a blank token.");
        }

        try {
            final HttpClient client = HttpClient.newHttpClient();
            final HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bot " + discordToken)
                .GET()
                .uri(new URI("https://discord.com/api/gateway/bot"))
                .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            final int codeCheck = response.statusCode() - 200;
            if (codeCheck < 0 || codeCheck > 100) {
                throw new LoginException();
            }
        } catch (final URISyntaxException | InterruptedException | LoginException | IOException e) {
            return List.of("Invalid token. Unable to connect to Discord.");
        }
        return null;
    }
}

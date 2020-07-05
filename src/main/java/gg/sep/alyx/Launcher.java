package gg.sep.alyx;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import gg.sep.alyx.config.ConfigHandler;
import gg.sep.alyx.setup.AlyxSetup;
import gg.sep.alyx.startup.AlyxCommandLineParser;
import gg.sep.alyx.startup.AlyxStartupArguments;
import gg.sep.alyx.core.model.AlyxConfig;
import gg.sep.alyx.core.model.BotEntry;
import gg.sep.result.Err;
import gg.sep.result.Ok;
import gg.sep.result.Result;

/**
 * Main entry point Launcher for Alyx.
 */
public final class Launcher {

    private Launcher() { }

    /**
     * Main entry point for the Alyx application.
     *
     * @throws ParseException Exception thrown if parsing the CLI arguments failed.
     * @param args Command line arguments passed to the application.
     * @throws Exception Exception thrown during launch of the program.
     */
    public static void main(final String[] args) throws Exception {
        // parse the CLI arguments into our Startup arguments
        final CommandLine commandLine = AlyxCommandLineParser.parseArgs(args);
        final AlyxStartupArguments arguments = AlyxCommandLineParser.buildArguments(commandLine);

        // use the config file path if provided, otherwise use the default
        final Path configFilePath = arguments.getConfigPath().orElse(ConfigHandler.ALYX_DEFAULT_CONFIG_FILE);
        final ConfigHandler configHandler = new ConfigHandler(configFilePath);

        // handle setup mode or existing bot mode
        final String botName = arguments.getBotName().orElse(null);
        final TextIO textIO = TextIoFactory.getTextIO();
        final Result<BotEntry, String> botEntry = arguments.isSetup() ? setup(configHandler, textIO) :
            loadExisting(botName, configHandler, textIO);

        textIO.dispose();

        if (botEntry.isErr()) {
            errorExit(botEntry.unwrapErr());
        }

        AlyxBot.launchBot(botEntry.unwrap());
    }

    /**
     * Attempts to load an existing bot entry from the specified configuration.
     * @param botName Name of the bot to load.
     * @param configHandler Config handler instance for the config file.
     * @param textIO TextIO instance for allowing the user to select a valid bot entry from the ones in the config file.
     * @return Result of a BotEntry of loaded successfully, otherwise an error string.
     */
    public static Result<BotEntry, String> loadExisting(final String botName, final ConfigHandler configHandler,
                                                        final TextIO textIO) {
        // try to load the existing configuration file, or return an error if unable to do so
        final Optional<AlyxConfig> loadedConfig = configHandler.loadAlyxConfig();
        if (loadedConfig.isEmpty()) {
            return Err.of("Unable to find valid configuration at: " + configHandler.getConfigPath());
        }
        final AlyxConfig alyxConfig = loadedConfig.get();

        // if not bot is specified, provide a list of bots to load
        final String loadBotName;
        if (StringUtils.isEmpty(botName)) {
            if (alyxConfig.getBots().isEmpty()) {
                return Err.of("There are no bots configured in that config file. Run --setup");
            }
            loadBotName = textIO.newStringInputReader()
                .withNumberedPossibleValues(new ArrayList<>(alyxConfig.getBots().keySet()))
                .read("Select a bot to start up:");
            textIO.dispose();
        } else {
            loadBotName = botName;
        }
        final BotEntry botEntry = alyxConfig.getBots().get(loadBotName);

        if (botEntry == null) {
            return Err.of(
                String.format("Unable to find a bot with name '%s'. Valid Bot Names: %s", botName,
                    alyxConfig.getBots().keySet()));
        }
        return Ok.of(botEntry);
    }

    /**
     * Enter setup mode and return the newly created Bot Entry.
     *
     * @param configHandler Config handler instance for the config file.
     * @param textIO TextIO instance for running the setup process.
     * @return Result of a BotEntry of the newly configured bot if successful, otherwise an error string.
     */
    public static Result<BotEntry, String> setup(final ConfigHandler configHandler, final TextIO textIO) {
        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(ConfigHandler.ALYX_DEFAULT_DATA_DIR)
            .textIO(textIO)
            .build();

        // run the setup process
        return alyxSetup.startSetup();
    }

    /**
     * Exits the application with an error and prints the specified message out to {@link System#out}.
     * @param error The error message to print out before exiting.
     */
    private static void errorExit(final String error) {
        System.out.println("[Error] " + error);
        System.exit(1);
    }
}

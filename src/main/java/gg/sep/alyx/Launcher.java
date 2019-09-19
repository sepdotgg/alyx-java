package gg.sep.alyx;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import gg.sep.alyx.core.config.ConfigHandler;
import gg.sep.alyx.core.setup.AlyxSetup;
import gg.sep.alyx.core.startup.AlyxCommandLineParser;
import gg.sep.alyx.core.startup.AlyxStartupArguments;
import gg.sep.alyx.model.config.AlyxConfig;
import gg.sep.alyx.model.config.BotEntry;
import gg.sep.alyx.util.result.Err;
import gg.sep.alyx.util.result.Ok;
import gg.sep.alyx.util.result.Result;

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
     */
    public static void main(final String[] args) throws ParseException {
        // parse the CLI arguments into our Startup arguments
        final CommandLine commandLine = AlyxCommandLineParser.parseArgs(args);
        final AlyxStartupArguments arguments = AlyxCommandLineParser.buildArguments(commandLine);

        // use the config file path if provided, otherwise use the default
        final Path configFilePath = arguments.getConfigPath().orElse(ConfigHandler.ALYX_DEFAULT_CONFIG_FILE);
        final ConfigHandler configHandler = new ConfigHandler(configFilePath);

        // handle setup mode or existing bot mode
        final String botName = arguments.getBotName().orElse(null);
        final Result<BotEntry, String> botEntry = arguments.isSetup() ? setup(configHandler) :
            loadExisting(botName, configHandler);

        if (botEntry.isErr()) {
            errorExit(botEntry.unwrapErr());
        }

        System.out.println(botEntry.unwrap());
    }

    private static Result<BotEntry, String> setup(final ConfigHandler configHandler) {
        final AlyxSetup alyxSetup = AlyxSetup.builder()
                .configHandler(configHandler)
                .defaultDataDir(ConfigHandler.ALYX_DEFAULT_DATA_DIR)
                .textIO(TextIoFactory.getTextIO())
                .build();

        // run the setup process
        return alyxSetup.startSetup();
    }

    private static Result<BotEntry, String> loadExisting(final String botName, final ConfigHandler configHandler) {
        // try to load the existing configuration file, or return an error if unable to do so
        final Optional<AlyxConfig> loadedConfig = configHandler.loadConfig();
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
            final TextIO textIO = TextIoFactory.getTextIO();
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
     * Exits the application with an error and prints the specified message out to {@link System#out}.
     * @param error The error message to print out before exiting.
     */
    private static void errorExit(final String error) {
        System.out.println("[Error] " + error);
        System.exit(1);
    }
}

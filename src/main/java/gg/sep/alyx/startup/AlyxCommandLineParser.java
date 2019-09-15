package gg.sep.alyx.startup;

import java.io.File;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import gg.sep.alyx.core.config.AlyxConfig;
import gg.sep.alyx.core.config.BotEntry;
import gg.sep.alyx.core.config.ConfigLoader;
import gg.sep.alyx.core.setup.AlyxSetup;
import gg.sep.alyx.util.result.Err;
import gg.sep.alyx.util.result.Ok;
import gg.sep.alyx.util.result.Result;

/**
 * Utility class for parsing command line arguments passed in from {@link gg.sep.alyx.Launcher#main(String[])}.
 */
@UtilityClass
public class AlyxCommandLineParser {

    private static final Options OPTIONS = new Options();

    static {
        AlyxCLIOption.addAllToOptions(OPTIONS);
    }

    @Builder
    private static final class AlyxStartupArguments {
        @Getter private final boolean setup;
        private final String botName;
        private final String configFilePath;

        Optional<String> getBotName() {
            return Optional.ofNullable(botName);
        }

        Optional<File> getConfigFile() {
            return configFilePath == null ? Optional.empty() : Optional.of(new File(configFilePath));
        }
    }

    /**
     * Parses the command line arguments. TODO
     * @param args Command line arguments passed into the application.
     * @return The built Command Line utility.
     */
    public static Result<CommandLine, String> buildCommandLine(final String[] args) {
        try {
            return Ok.of(new DefaultParser().parse(OPTIONS, args));
        } catch (final ParseException e) {
            return Err.of(String.format("[%s] %s", e.getClass().getSimpleName(), e.getMessage()));
        }
    }

    /**
     * Given a set of command line arguments to the application, perform the startup process.
     *
     * @param args Command line arguments string array.
     * @return Returns a {@link BotEntry} instance, either the new one set up (if setup) or
     *         the bot name passed in.
     * @throws ParseException Exception thrown if parsing the command line arguments failed.
     */
    public static Result<BotEntry, String> alyxStartup(final String[] args) {
        final Result<CommandLine, String> parsedCommands = parseArgs(args);
        if (parsedCommands.isErr()) {
            return Err.of(parsedCommands.unwrapErr());
        }
        final CommandLine cmd = parsedCommands.unwrap();
        final AlyxStartupArguments arguments = buildArguments(cmd);
        final File configFile = arguments.getConfigFile().orElse(ConfigLoader.ALYX_DEFAULT_CONFIG_FILE);

        if (arguments.isSetup()) {
            if (arguments.getBotName().isPresent()) {
                return Err.of("The bot and setup options cannot be passed together.");
            }
            return AlyxSetup.enterSetup(configFile);
        }

        // we're not in setup mode, so load an existing config
        final Optional<AlyxConfig> botConfig = ConfigLoader.loadConfig(configFile);
        if (botConfig.isEmpty()) {
            return Err.of("Failed to load the supplied config file.");
        }
        final String botName = arguments.getBotName().get();
        final BotEntry botEntry = botConfig.get().getBots().get(botName);
        if (botEntry == null) {
            return Err.of("There is no configured bot with the name: " + botName);
        }
        return Ok.of(botEntry);
    }

    private Result<CommandLine, String> parseArgs(final String[] args) {
        try {
            return Ok.of(new DefaultParser().parse(OPTIONS, args));
        } catch (final ParseException e) {
            return Err.of("Error parsing the command line arguments: " + e.getMessage());
        }
    }

    private AlyxStartupArguments buildArguments(final CommandLine commandLine) {
        final AlyxStartupArguments.AlyxStartupArgumentsBuilder builder = AlyxStartupArguments.builder();
        builder.setup(commandLine.hasOption("s"));
        if (commandLine.hasOption("b")) {
            builder.botName(commandLine.getOptionValue("b"));
        }
        if (commandLine.hasOption("config")) {
            builder.configFilePath(commandLine.getOptionValue("config"));
        }
        return builder.build();
    }
}

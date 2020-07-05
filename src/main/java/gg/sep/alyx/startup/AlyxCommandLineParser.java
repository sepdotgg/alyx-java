package gg.sep.alyx.startup;

import java.nio.file.Path;

import lombok.experimental.UtilityClass;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Utility class for parsing command line arguments passed in from {@link gg.sep.alyx.Launcher#main(String[])}.
 */
@UtilityClass
public class AlyxCommandLineParser {

    private static final Options OPTIONS = new Options();

    static {
        AlyxCLIOption.addAllToOptions(OPTIONS);
    }

    /**
     * Parses the command line args string array into a Command Line object.
     *
     * @param args String array of command line args, passed into main()
     * @throws ParseException Exception thrown if parsing the CLI arguments failed,
     *                        or if an unexpected argument was received.
     * @return Command Line object from the supplied string array of args.
     */
    public static CommandLine parseArgs(final String[] args) throws ParseException {
        return new DefaultParser().parse(OPTIONS, args);
    }

    /**
     * Builds the Alyx Startup Arguments object based on the supplied command line options.
     *
     * @param commandLine Command line options.
     * @return Alyx Startup arguments object based on the supplied command line options.
     */
    public static AlyxStartupArguments buildArguments(final CommandLine commandLine) {
        final AlyxStartupArguments.AlyxStartupArgumentsBuilder builder = AlyxStartupArguments.builder();
        builder.setup(commandLine.hasOption("s"));
        if (commandLine.hasOption("b")) {
            builder.botName(commandLine.getOptionValue("b"));
        }
        if (commandLine.hasOption("config")) {
            builder.configPath(Path.of(commandLine.getOptionValue("config")));
        }
        return builder.build();
    }
}

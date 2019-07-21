package gg.sep.alyx.startup;

import lombok.experimental.UtilityClass;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
}

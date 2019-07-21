package gg.sep.alyx;

import org.apache.commons.cli.CommandLine;

import gg.sep.alyx.core.config.AlyxConfig;
import gg.sep.alyx.core.setup.AlyxSetup;
import gg.sep.alyx.startup.AlyxCommandLineParser;
import gg.sep.alyx.util.result.Result;

/**
 * Main entry point Launcher for Alyx.
 */
public final class Launcher {

    private Launcher() { }

    /**
     * Main entry point for the Alyx application.
     * @param args Command line arguments passed to the application.
     */
    public static void main(final String[] args) {
        // TODO: We don't need a result for this, it's just a POC
        final Result<CommandLine, String> cmdResult = AlyxCommandLineParser.buildCommandLine(args);
        final CommandLine cmd = cmdResult.unwrapOrElse(e -> {
            throw new RuntimeException(cmdResult.unwrapErr());
        });

        if (cmd.hasOption("s")) {
            if (cmd.hasOption("b")) {
                errorExit("The bot and setup options cannot be passed together");
            }
            final Result<AlyxConfig, String> setupConfig = AlyxSetup.enterSetup();
            setupConfig.unwrapOrElse(e -> {
                throw new RuntimeException(e);
            });
        }
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

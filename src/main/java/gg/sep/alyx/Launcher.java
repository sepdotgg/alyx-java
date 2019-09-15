package gg.sep.alyx;

import gg.sep.alyx.core.config.BotEntry;
import gg.sep.alyx.core.startup.AlyxCommandLineParser;
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

        final Result<BotEntry, String> startupResult = AlyxCommandLineParser.alyxStartup(args);
        final BotEntry botEntry = startupResult.unwrapOrElse(s -> {
            throw new RuntimeException(s);
        });

        System.out.println(botEntry);
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

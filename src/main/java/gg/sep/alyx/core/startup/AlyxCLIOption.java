package gg.sep.alyx.core.startup;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.Options;

/**
 * An enum representing each of the command line options available to the application.
 */
@Getter
@AllArgsConstructor
public enum AlyxCLIOption {
    SETUP("s", "setup", false, "Setups up a new bot"),
    BOT("b", "bot", true, "Name of the existing bot to launch"),
    CONFIG_FILE("config", "config", true, "Path to the bot's config file.");

    private String opt;
    private String longOpt;
    private boolean hasArg;
    private String description;

    /**
     * Adds the option to the list of Apache CLI options.
     * @param options Apache CLI Command Line Options.
     */
    private void addToOptions(final Options options) {
        options.addOption(opt, longOpt, hasArg, description);
    }

    /**
     * Adds all values of this enum as options in the Apache CLI Command Line Options.
     * @param options Apache CLI Command Line options.
     */
    public static void addAllToOptions(final Options options) {
        for (final AlyxCLIOption clo : AlyxCLIOption.values()) {
            clo.addToOptions(options);
        }
    }
}

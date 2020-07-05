package gg.sep.alyx.core.plugin;

import gg.sep.alyx.core.plugin.commands.ParameterParser;

/**
 * Exception thrown by a {@link ParameterParser} when parsing a string
 * parameter into its type fails.
 */
public class CommandParseException extends AlyxException {
    /**
     * Creates a new CommandParseException with the input value and expected output type class.
     *
     * @param value Parameter's string input value.
     * @param type Expected output type of the {@link ParameterParser}.
     */
    public CommandParseException(final String value, final Class<?> type) {
        super(String.format("Unable to parse value '%s' into type '%s'", value, type));
    }
}

package gg.sep.alyx.core.commands.parsers;

import java.time.DateTimeException;
import java.time.Instant;

/**
 * Handles parsing of String parameters into Instants.
 */
public class InstantParameterParser implements ParameterParser<Instant> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Instant> getType() {
        return Instant.class;
    }

    /**
     * Parses a Long String representing epoch milliseconds into an {@link Instant}.
     * @param value Epoch milliseconds long as a string.
     * @return Instant of the epoch milliseconds.
     * @throws CommandParseException Error thrown if parsing either the Long or Instant fails.
     */
    @Override
    public Instant parse(final String value) throws CommandParseException {
        try {
            return Instant.ofEpochMilli(Long.parseLong(value));
        } catch (final NumberFormatException | DateTimeException e) {
            throw new CommandParseException(value, Instant.class);
        }
    }
}

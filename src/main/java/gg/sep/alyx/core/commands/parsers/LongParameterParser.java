package gg.sep.alyx.core.commands.parsers;

import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.core.plugin.CommandParseException;
import gg.sep.alyx.core.plugin.commands.ParameterParser;

/**
 * Handles parsing of String parameters into Longs.
 */
public class LongParameterParser implements ParameterParser<Long> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Long> getType() {
        return Long.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long parse(final String value, final Event event) throws CommandParseException {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new CommandParseException(value, Long.class);
        }
    }
}

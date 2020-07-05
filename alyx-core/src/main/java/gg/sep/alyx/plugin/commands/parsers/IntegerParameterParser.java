package gg.sep.alyx.plugin.commands.parsers;

import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.plugin.CommandParseException;
import gg.sep.alyx.plugin.commands.ParameterParser;

/**
 * Handles parsing of String parameters into Integers.
 */
public class IntegerParameterParser implements ParameterParser<Integer> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer parse(final String value, final Event event) throws CommandParseException {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new CommandParseException(value, Integer.class);
        }
    }
}

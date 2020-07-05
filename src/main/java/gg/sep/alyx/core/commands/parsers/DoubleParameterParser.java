package gg.sep.alyx.core.commands.parsers;

import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.plugin.CommandParseException;
import gg.sep.alyx.plugin.commands.ParameterParser;

/**
 * Handles parsing of String parameters into Doubles.
 */
public class DoubleParameterParser implements ParameterParser<Double> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Double> getType() {
        return Double.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Double parse(final String value, final Event event) throws CommandParseException {
        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException e) {
            throw new CommandParseException(value, Double.class);
        }
    }
}

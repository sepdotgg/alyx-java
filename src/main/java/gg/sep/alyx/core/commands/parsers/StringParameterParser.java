package gg.sep.alyx.core.commands.parsers;

import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.plugin.CommandParseException;
import gg.sep.alyx.plugin.commands.ParameterParser;

/**
 * Handles parsing of String parameters into ... Strings!
 */
public class StringParameterParser implements ParameterParser<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getType() {
        return String.class;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public String parse(final String value, final Event event) throws CommandParseException {
        return value;
    }
}

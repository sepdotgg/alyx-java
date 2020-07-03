package gg.sep.alyx.core.commands.parsers;

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
    public String parse(final String value) throws CommandParseException {
        return value;
    }
}

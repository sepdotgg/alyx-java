package gg.sep.alyx.core.commands.parsers;

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
    public Integer parse(final String value) throws CommandParseException {
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            throw new CommandParseException(value, Integer.class);
        }
    }
}

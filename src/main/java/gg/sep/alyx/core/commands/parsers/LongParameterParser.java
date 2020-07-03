package gg.sep.alyx.core.commands.parsers;

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
    public Long parse(final String value) throws CommandParseException {
        try {
            return Long.parseLong(value);
        } catch (final NumberFormatException e) {
            throw new CommandParseException(value, Long.class);
        }
    }
}

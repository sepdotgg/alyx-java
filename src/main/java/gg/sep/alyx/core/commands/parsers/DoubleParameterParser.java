package gg.sep.alyx.core.commands.parsers;

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
    public Double parse(final String value) throws CommandParseException {
        try {
            return Double.parseDouble(value);
        } catch (final NumberFormatException e) {
            throw new CommandParseException(value, Double.class);
        }
    }
}

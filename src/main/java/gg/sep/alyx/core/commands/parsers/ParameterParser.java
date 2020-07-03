package gg.sep.alyx.core.commands.parsers;

/**
 * Represents a class which is able to parse a string into the given type {@code T}.
 * @param <T> Type of the output once the input string is parsed with {@link ParameterParser#parse}.
 */
public interface ParameterParser<T> {
    /**
     * Returns the class of {@code T}.
     * @return The output class of this parser.
     */
    Class<T> getType();

    /**
     * Parses the given input string into the parser's type.
     *
     * @param value Input string value to parse.
     * @return String value parsed into the parser's type.
     * @throws CommandParseException Thrown if parsing fails.
     */
    T parse(String value) throws CommandParseException;
}

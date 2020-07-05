package gg.sep.alyx.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import gg.sep.alyx.plugin.util.Strings;

/**
 * Unit tests for {@link Strings}.
 */
public class StringsTest {

    @MethodSource("data")
    @ParameterizedTest
    void splitWithQuotes(final String input, final String[] expected) {
        assertArrayEquals(expected, Strings.splitWithQuotes(input));
    }

    private static Stream<Arguments> data() {
        return Stream.of(
            // single word
            Arguments.arguments("foo", new String[]{"foo"}),
            Arguments.arguments("1", new String[]{"1"}),
            Arguments.arguments("@#abc123**", new String[]{"@#abc123**"}),

            // multiple words
            Arguments.arguments("hello world", new String[]{"hello", "world"}),
            Arguments.arguments("hello @#abc123**", new String[]{"hello", "@#abc123**"}),
            Arguments.arguments("hello @#abc123** world", new String[]{"hello", "@#abc123**", "world"}),

            // double spaces removed
            Arguments.arguments("hello  world", new String[]{"hello", "world"}),
            Arguments.arguments("hello    world", new String[]{"hello", "world"}),
            Arguments.arguments("hello      world", new String[]{"hello", "world"}),

            // beginning and trailing spaces removed
            Arguments.arguments(" hello", new String[]{"hello"}),
            Arguments.arguments("world ", new String[]{"world"}),
            Arguments.arguments(" hello world", new String[]{"hello", "world"}),
            Arguments.arguments("hello world ", new String[]{"hello", "world"}),
            Arguments.arguments(" hello world ", new String[]{"hello", "world"}),

            // simple inner quotes
            Arguments.arguments("hello \"world\"", new String[]{"hello", "world"}),
            Arguments.arguments("\"hello\" world", new String[]{"hello", "world"}),
            Arguments.arguments("hello \"foo\" world", new String[]{"hello", "foo", "world"}),
            Arguments.arguments("hello \"foo bar\" world", new String[]{"hello", "foo bar", "world"}),
            Arguments.arguments("hello \" foo bar\" world", new String[]{"hello", " foo bar", "world"}),
            Arguments.arguments("hello \"foo bar \" world", new String[]{"hello", "foo bar ", "world"}),

            // empty quotes
            Arguments.arguments("hello \"\" \" world\"", new String[]{"hello", "", " world"}),
            Arguments.arguments("hello \" \" \" world\"", new String[]{"hello", " ", " world"})
        );
    }
}

package gg.sep.alyx.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.gson.Gson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import gg.sep.alyx.plugin.util.ModelParser;

/**
 * Tests for {@link ModelParser}.
 */
public class ModelParserTest {

    @ParameterizedTest
    @MethodSource("validJson")
    void parseJson_Valid(final String inputJson, final Class<?> outputClass, final Object expectedOutput) {
        assertEquals(ModelParser.parseJson(inputJson, outputClass), expectedOutput);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\\\\\", "{", "    "})
    void parseJson_Invalid(final String inputJson) {
        assertEquals(Optional.empty(), ModelParser.parseJson(inputJson, Object.class));
    }

    private static Stream<Arguments> validJson() {
        final Map<String, Object> complexMap = Map.of(
            "foo", "bar",
            "baz", List.of("qux", "quux"),
            "quuz", Map.of("corge", "grault")
        );
        final String complexMapJson = new Gson().toJson(complexMap);
        final List<String> listObject = List.of("foo", "bar", "baz");
        final String listObjectJson = new Gson().toJson(listObject);

        final List<Arguments> arguments = new ArrayList<>();
        arguments.add(Arguments.arguments("", Void.class, Optional.empty()));
        arguments.add(Arguments.arguments("Foo", String.class, Optional.of("Foo")));
        arguments.add(Arguments.arguments(complexMapJson, Map.class, Optional.of(complexMap)));
        arguments.add(Arguments.arguments(listObjectJson, List.class, Optional.of(listObject)));
        arguments.add(Arguments.arguments("{}", Object.class, Optional.of(Map.of())));
        return arguments.stream();
    }
}

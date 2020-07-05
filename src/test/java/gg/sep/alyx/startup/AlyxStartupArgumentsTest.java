package gg.sep.alyx.startup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link AlyxStartupArguments}.
 */
public class AlyxStartupArgumentsTest {

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "FooBar", "_", "!", "1"})
    void getBotName_WithValue(final String botName) {
        final AlyxStartupArguments startupArguments = AlyxStartupArguments.builder().botName(botName).build();
        assertEquals(Optional.of(botName), startupArguments.getBotName());
    }

    @Test
    void getBotName_NullValue() {
        final AlyxStartupArguments startupArguments = AlyxStartupArguments.builder().build();
        assertEquals(Optional.empty(), startupArguments.getBotName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/absolute/directory/", "../relativeDir/", "/absolute/file.json",
        "../relative/file.json"})
    void getConfigPath_WithValue(final String configPath) {
        final Path expectedPath = Path.of(configPath);
        final AlyxStartupArguments startupArguments = AlyxStartupArguments.builder().configPath(expectedPath)
            .build();
        assertEquals(Optional.of(expectedPath), startupArguments.getConfigPath());
    }

    @Test
    void getConfigPath_NullValue() {
        final AlyxStartupArguments startupArguments = AlyxStartupArguments.builder().build();
        assertEquals(Optional.empty(), startupArguments.getConfigPath());
    }
}

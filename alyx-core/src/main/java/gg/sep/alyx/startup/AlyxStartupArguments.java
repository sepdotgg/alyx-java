package gg.sep.alyx.startup;

import java.nio.file.Path;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

/**
 * Class for holding the application's startup options and values.
 */
@Builder
public final class AlyxStartupArguments {
    @Getter
    private final boolean setup;
    private final String botName;
    private final Path configPath;

    /**
     * Returns an optional of the requested bot name to start up, if present.
     * @return Optional of the requested bot name to start up, if present.
     */
    public Optional<String> getBotName() {
        return Optional.ofNullable(botName);
    }

    /**
     * Returns optional of the config file supplied at startup, if present.
     * @return Optional of the config file supplied at startup, if present.
     */
    public Optional<Path> getConfigPath() {
        return configPath == null ? Optional.empty() : Optional.of(configPath);
    }
}

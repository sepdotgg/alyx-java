// Generated by delombok at Sun Jul 05 15:37:37 PDT 2020
package gg.sep.alyx.startup;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Class for holding the application's startup options and values.
 */
public final class AlyxStartupArguments {
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

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    AlyxStartupArguments(final boolean setup, final String botName, final Path configPath) {
        this.setup = setup;
        this.botName = botName;
        this.configPath = configPath;
    }


    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static class AlyxStartupArgumentsBuilder {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private boolean setup;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private String botName;
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private Path configPath;

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        AlyxStartupArgumentsBuilder() {
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public AlyxStartupArguments.AlyxStartupArgumentsBuilder setup(final boolean setup) {
            this.setup = setup;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public AlyxStartupArguments.AlyxStartupArgumentsBuilder botName(final String botName) {
            this.botName = botName;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public AlyxStartupArguments.AlyxStartupArgumentsBuilder configPath(final Path configPath) {
            this.configPath = configPath;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public AlyxStartupArguments build() {
            return new AlyxStartupArguments(this.setup, this.botName, this.configPath);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "AlyxStartupArguments.AlyxStartupArgumentsBuilder(setup=" + this.setup + ", botName=" + this.botName + ", configPath=" + this.configPath + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static AlyxStartupArguments.AlyxStartupArgumentsBuilder builder() {
        return new AlyxStartupArguments.AlyxStartupArgumentsBuilder();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public boolean isSetup() {
        return this.setup;
    }
}

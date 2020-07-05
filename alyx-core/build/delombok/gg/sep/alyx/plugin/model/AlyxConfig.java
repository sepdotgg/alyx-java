// Generated by delombok at Sun Jul 05 15:37:37 PDT 2020
package gg.sep.alyx.plugin.model;

import java.util.HashMap;
import java.util.Map;
import gg.sep.alyx.plugin.storage.AbstractJsonObject;
import gg.sep.alyx.plugin.storage.JsonSerializable;

/**
 * Model class for the core Alyx configuration JSON file..
 */
public class AlyxConfig extends AbstractJsonObject implements JsonSerializable {
    private final Map<String, BotEntry> bots;

    /**
     * Returns a new empty instance of AlyxConfig with no bots, suitable for writing to a new config file.
     * @return An empty instance of AlyxConfig with no bots.
     */
    public static AlyxConfig empty() {
        return AlyxConfig.builder().bots(new HashMap<>()).build();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    AlyxConfig(final Map<String, BotEntry> bots) {
        this.bots = bots;
    }


    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static class AlyxConfigBuilder {
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        private Map<String, BotEntry> bots;

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        AlyxConfigBuilder() {
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public AlyxConfig.AlyxConfigBuilder bots(final Map<String, BotEntry> bots) {
            this.bots = bots;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public AlyxConfig build() {
            return new AlyxConfig(this.bots);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        @lombok.Generated
        public java.lang.String toString() {
            return "AlyxConfig.AlyxConfigBuilder(bots=" + this.bots + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public static AlyxConfig.AlyxConfigBuilder builder() {
        return new AlyxConfig.AlyxConfigBuilder();
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public Map<String, BotEntry> getBots() {
        return this.bots;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public java.lang.String toString() {
        return "AlyxConfig(bots=" + this.getBots() + ")";
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof AlyxConfig)) return false;
        final AlyxConfig other = (AlyxConfig) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$bots = this.getBots();
        final java.lang.Object other$bots = other.getBots();
        if (this$bots == null ? other$bots != null : !this$bots.equals(other$bots)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof AlyxConfig;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $bots = this.getBots();
        result = result * PRIME + ($bots == null ? 43 : $bots.hashCode());
        return result;
    }
}

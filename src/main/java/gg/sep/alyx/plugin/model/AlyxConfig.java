package gg.sep.alyx.plugin.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import gg.sep.alyx.config.ConfigHandler;
import gg.sep.alyx.plugin.storage.AbstractJsonObject;
import gg.sep.alyx.plugin.storage.JsonSerializable;

/**
 * Model class for the core Alyx configuration JSON file, loaded via {@link ConfigHandler}.
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class AlyxConfig extends AbstractJsonObject implements JsonSerializable {
    private final Map<String, BotEntry> bots;

    /**
     * Returns a new empty instance of AlyxConfig with no bots, suitable for writing to a new config file.
     * @return An empty instance of AlyxConfig with no bots.
     */
    public static AlyxConfig empty() {
        return AlyxConfig.builder()
            .bots(new HashMap<>())
            .build();
    }
}

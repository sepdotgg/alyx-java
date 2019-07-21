package gg.sep.alyx.core.config;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Model class for the core Alyx configuration JSON file, loaded via {@link ConfigLoader}.
 */
@Builder
@Getter
@ToString
public class AlyxConfig {
    public static final AlyxConfig EMPTY = AlyxConfig.builder().bots(Map.of()).build();

    private final Map<String, BotEntry> bots;
}

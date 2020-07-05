package gg.sep.alyx.model.config;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import gg.sep.alyx.plugin.storage.AbstractJsonObject;
import gg.sep.alyx.plugin.storage.JsonSerializable;

/**
 * Configuration settings for a single Alyx bot.
 */
@Builder
@ToString
@Getter
@EqualsAndHashCode(callSuper = false)
public class BotConfig extends AbstractJsonObject implements JsonSerializable {
    private final String botName;
    private final String discordToken;
    private final Character commandPrefix;
}

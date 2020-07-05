package gg.sep.alyx.model.config;

import java.nio.file.Path;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import gg.sep.alyx.plugin.storage.StorageType;
import gg.sep.alyx.model.AbstractJsonObject;
import gg.sep.alyx.model.JsonSerializable;

/**
 * Model for the BotEntry entities as part of {@link AlyxConfig}.
 */
@Builder
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class BotEntry extends AbstractJsonObject implements JsonSerializable {
    private String botName;
    private StorageType storageType;
    private Path dataDir;
}

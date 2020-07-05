package gg.sep.alyx.core.model;

import java.nio.file.Path;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import gg.sep.alyx.core.plugin.storage.StorageType;
import gg.sep.alyx.core.plugin.storage.AbstractJsonObject;
import gg.sep.alyx.core.plugin.storage.JsonSerializable;

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

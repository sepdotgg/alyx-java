package gg.sep.alyx.core.config;

import java.io.File;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import gg.sep.alyx.core.storage.StorageType;

/**
 * Model for the BotEntry entities as part of {@link AlyxConfig}.
 */
@Builder
@Getter
@ToString
public class BotEntry {
    private String botName;
    private StorageType storageType;
    private File configDir;
}

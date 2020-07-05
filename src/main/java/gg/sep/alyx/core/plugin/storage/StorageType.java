package gg.sep.alyx.core.plugin.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Contains the valid types of storage supported by {@link AlyxStorageEngine}.
 */
@Getter
@AllArgsConstructor
public enum StorageType {
    JSON("json");

    private final String value;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value;
    }
}

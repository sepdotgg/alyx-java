package gg.sep.alyx.core.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link StorageType}.
 */
public class StorageTypeTest {

    @Test
    void toString_EqualsValue() {
        for (final StorageType storageType : StorageType.values()) {
            assertEquals(storageType.getValue(), storageType.toString());
        }
    }
}

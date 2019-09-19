package gg.sep.alyx.core.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gg.sep.alyx.core.storage.StorageType;
import gg.sep.alyx.model.AbstractJsonObject;
import gg.sep.alyx.model.config.AlyxConfig;
import gg.sep.alyx.model.config.BotEntry;

/**
 * Tests for {@link AbstractJsonObject}.
 */
public class AbstractJsonObjectTest {

    @Test
    void toJson_Valid() {
        final BotEntry botEntry = BotEntry.builder()
            .botName("Foo")
            .storageType(StorageType.JSON)
            .dataDir(Path.of("/path/to/data/"))
            .build();
        final AlyxConfig expectedAlyxConfig = AlyxConfig.builder()
            .bots(Map.of(botEntry.getBotName(), botEntry))
            .build();

        final String jsonString = expectedAlyxConfig.toJson();
        // load it back up and make sure they're equal
        final AlyxConfig newAlyxConfig = AbstractJsonObject.buildGson().fromJson(jsonString, AlyxConfig.class);
        assertEquals(expectedAlyxConfig, newAlyxConfig);
    }
}

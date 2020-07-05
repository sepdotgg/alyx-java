package gg.sep.alyx.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gg.sep.alyx.plugin.model.AlyxConfig;
import gg.sep.alyx.plugin.model.BotEntry;
import gg.sep.alyx.plugin.storage.AbstractJsonObject;
import gg.sep.alyx.plugin.storage.StorageType;

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

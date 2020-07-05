package gg.sep.alyx.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import gg.sep.alyx.plugin.model.AlyxConfig;
import gg.sep.alyx.plugin.model.BotEntry;
import gg.sep.alyx.plugin.storage.StorageType;

/**
 * Tests for {@link ConfigHandler}.
 */
public class ConfigHandlerTest {

    private ConfigHandler tempDirConfigHandler(final Path tempDir) {
        final Path configFile = Paths.get(tempDir.toAbsolutePath().toString(), "/config.json");
        return new ConfigHandler(configFile);
    }

    @Test
    void loadConfig_NoFile(@TempDir final Path tempDir) {
        final ConfigHandler configHandler = tempDirConfigHandler(tempDir);
        assertEquals(Optional.empty(), configHandler.loadAlyxConfig());
    }

    @Test
    void updateBotEntry_NewConfig(@TempDir final Path tempDir) throws Exception {
        final ConfigHandler configHandler = tempDirConfigHandler(tempDir);
        final BotEntry expectedBotEntry = BotEntry.builder()
            .botName("foo")
            .dataDir(tempDir)
            .storageType(StorageType.JSON)
            .build();

        configHandler.updateBotEntry(expectedBotEntry);

        // load the config back up
        final Optional<AlyxConfig> alyxConfig = configHandler.loadAlyxConfig();
        assertTrue(alyxConfig.isPresent());
        assertEquals(Map.of("foo", expectedBotEntry), alyxConfig.get().getBots());
    }

    @Test
    void updateBotEntry_ExistingConfig_NewBot(@TempDir final Path tempDir) throws Exception {
        final ConfigHandler configHandler = tempDirConfigHandler(tempDir);
        final BotEntry baseBotEntry = BotEntry.builder()
            .botName("base")
            .dataDir(tempDir)
            .storageType(StorageType.JSON)
            .build();
        final BotEntry newBotEntry = BotEntry.builder()
            .botName("foo")
            .dataDir(tempDir)
            .storageType(StorageType.JSON)
            .build();

        final Map<String, BotEntry> expectedMap = Map.of(
            baseBotEntry.getBotName(), baseBotEntry,
            newBotEntry.getBotName(), newBotEntry
        );

        configHandler.updateBotEntry(baseBotEntry);
        configHandler.updateBotEntry(newBotEntry);

        final Optional<AlyxConfig> alyxConfig = configHandler.loadAlyxConfig();
        assertTrue(alyxConfig.isPresent());
        assertEquals(expectedMap, alyxConfig.get().getBots());
    }

    @Test
    void updateBotEntry_ExistingConfig_OverwriteBot(@TempDir final Path tempDir) throws Exception {
        final ConfigHandler configHandler = tempDirConfigHandler(tempDir);
        final BotEntry baseBotEntry = BotEntry.builder()
            .botName("base")
            .dataDir(tempDir)
            .storageType(StorageType.JSON)
            .build();
        final BotEntry newBotEntry = BotEntry.builder()
            .botName("base")
            .dataDir(Paths.get(tempDir.toAbsolutePath().toString(), "overwrite"))
            .storageType(StorageType.JSON)
            .build();
        final Map<String, BotEntry> expectedMap = Map.of(newBotEntry.getBotName(), newBotEntry);

        configHandler.updateBotEntry(baseBotEntry);
        configHandler.updateBotEntry(newBotEntry);

        final Optional<AlyxConfig> alyxConfig = configHandler.loadAlyxConfig();
        assertTrue(alyxConfig.isPresent());
        assertEquals(expectedMap, alyxConfig.get().getBots());
    }

    @Test
    void writeConfig_NoParentDirectory() {
        final Path rootPath = Path.of("/");
        final ConfigHandler configHandler = new ConfigHandler(rootPath);
        assertThrows(IOException.class, () -> configHandler.writeConfig(null));
    }
}

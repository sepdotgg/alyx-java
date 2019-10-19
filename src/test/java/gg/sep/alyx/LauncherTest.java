package gg.sep.alyx;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;

import org.beryx.textio.StringInputReader;
import org.beryx.textio.TextIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import gg.sep.alyx.core.config.ConfigHandler;
import gg.sep.alyx.model.config.AlyxConfig;
import gg.sep.alyx.model.config.BotEntry;
import gg.sep.result.Result;

/**
 * Main test class for {@link Launcher}.
 */
public class LauncherTest {

    @Test
    void loadExisting_BotExists(@TempDir final Path tempDir) throws Exception {
        final Path configFile = Path.of(tempDir.toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        final BotEntry expectedBot = BotEntry.builder().botName("Foo").build();
        configHandler.updateBotEntry(expectedBot);

        final Result<BotEntry, String> loadedBotEntry = Launcher.loadExisting("Foo", configHandler, null);
        assertTrue(loadedBotEntry.isOk());
        assertEquals(expectedBot, loadedBotEntry.unwrap());
    }

    @Test
    void loadExisting_InvalidConfigFile() {
        final ConfigHandler configHandler = Mockito.mock(ConfigHandler.class);
        Mockito.when(configHandler.loadConfig()).thenReturn(Optional.empty());
        assertTrue(Launcher.loadExisting(null, configHandler, null).isErr());
    }

    @Test
    void loadExisting_BotNotExists(@TempDir final Path tempDir) throws Exception {
        final Path configFile = Path.of(tempDir.toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        configHandler.updateBotEntry(BotEntry.builder().botName("Foo").build());

        final Result<BotEntry, String> loadedBotEntry = Launcher.loadExisting("Bar", configHandler, null);
        assertTrue(loadedBotEntry.isErr());
    }

    @Test
    void loadExisting_NoBotsConfigured(@TempDir final Path tempDir) throws Exception {
        final Path configFile = Path.of(tempDir.toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        configHandler.writeConfig(AlyxConfig.empty());

        final Result<BotEntry, String> loadedBotEntry = Launcher.loadExisting(null, configHandler, null);
        assertTrue(loadedBotEntry.isErr());
    }

    @Test
    void loadExisting_NoBotSpecified_SelectExisting(@TempDir final Path tempDir) throws Exception {
        final Path configFile = Path.of(tempDir.toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        final BotEntry expectedBot = BotEntry.builder().botName("Foo").build();
        configHandler.updateBotEntry(expectedBot);

        final TextIO mockTextIO = Mockito.mock(TextIO.class);
        final StringInputReader reader = Mockito.mock(StringInputReader.class);
        Mockito.when(mockTextIO.newStringInputReader()).thenReturn(reader);
        Mockito.when(reader.withNumberedPossibleValues(Mockito.anyList())).thenReturn(reader);
        Mockito.when(reader.read(Mockito.anyString())).thenReturn("Foo");

        final Result<BotEntry, String> loadedBotEntry = Launcher.loadExisting(null, configHandler, mockTextIO);
        assertTrue(loadedBotEntry.isOk());
        assertEquals(expectedBot, loadedBotEntry.unwrap());
    }
}

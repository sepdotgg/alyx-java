package gg.sep.alyx.core.setup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;

import org.beryx.textio.BooleanInputReader;
import org.beryx.textio.EnumInputReader;
import org.beryx.textio.StringInputReader;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import gg.sep.alyx.core.config.ConfigHandler;
import gg.sep.alyx.core.storage.StorageType;
import gg.sep.alyx.model.config.BotEntry;
import gg.sep.alyx.util.result.Result;

/**
 * Tests for {@link AlyxSetup}.
 */
public class AlyxSetupTest {

    @SuppressWarnings("unchecked")
    private TextIO mockTextIOStuff(final String botName, final StorageType storageType, final boolean overwriteBot,
                                   final String configDirPath, final boolean confirmConfigDirPath,
                                   final boolean explicitOrder) {
        final TextIO mockTextIO = Mockito.mock(TextIO.class);
        final TextTerminal mockTerminal = Mockito.mock(TextTerminal.class);
        Mockito.when(mockTextIO.getTextTerminal()).thenReturn(mockTerminal);

        final StringInputReader mockStringReader = Mockito.mock(StringInputReader.class);
        final EnumInputReader<StorageType> mockEnumReader = Mockito.mock(EnumInputReader.class);
        final BooleanInputReader mockBooleanReader = Mockito.mock(BooleanInputReader.class);

        // we can kind of control which stream input reader we'll get because different methods are called
        final StringInputReader mockBotNameReader = Mockito.mock(StringInputReader.class);
        Mockito.when(mockStringReader.withValueChecker(Mockito.any())).thenReturn(mockBotNameReader);
        Mockito.when(mockBotNameReader.read(Mockito.anyList())).thenReturn(botName);

        final StringInputReader mockPathReader = Mockito.mock(StringInputReader.class);
        Mockito.when(mockStringReader.withDefaultValue(Mockito.anyString())).thenReturn(mockPathReader);
        Mockito.when(mockPathReader.withValueChecker(Mockito.any())).thenReturn(mockPathReader);
        Mockito.when(mockPathReader.read(Mockito.anyList())).thenReturn(configDirPath);

        // return our mock string reader
        Mockito.when(mockTextIO.newStringInputReader()).thenReturn(mockStringReader);
        Mockito.when(mockTextIO.newEnumInputReader(Mockito.eq(StorageType.class))).thenReturn(mockEnumReader);
        Mockito.when(mockTextIO.newBooleanInputReader()).thenReturn(mockBooleanReader);

        // handle storage type return
        Mockito.when(mockEnumReader.read(Mockito.anyList())).thenReturn(storageType);

        if (explicitOrder || overwriteBot) {
            Mockito.when(mockBooleanReader.read(Mockito.anyString()))
                .thenReturn(overwriteBot)
                .thenReturn(confirmConfigDirPath);
        } else {
            Mockito.when(mockBooleanReader.read(Mockito.anyString()))
                .thenReturn(confirmConfigDirPath);
        }

        return mockTextIO;
    }

    @Test
    void startSetup_ConfigFilePathIsDirectory(@TempDir final Path tempDir) {
        final ConfigHandler configHandler = new ConfigHandler(tempDir);
        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(tempDir)
            .build();

        final Result<?, String> result = alyxSetup.startSetup();
        assertTrue(result.isErr());
    }

    @Test
    void startSetup_UnwritableConfigFile(@TempDir final Path tempDir) throws Exception {
        final Path configFile = Path.of(tempDir.toAbsolutePath().toString(), "config.json");
        // create the file and set permissions
        Files.createFile(configFile);
        Files.setPosixFilePermissions(configFile, PosixFilePermissions.fromString("r--r-x---"));
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(tempDir)
            .build();

        final Result<?, String> result = alyxSetup.startSetup();
        assertTrue(result.isErr());
    }

    @Test
    void startSetup_ErrorLoadingConfigFile(@TempDir final Path tempDir) throws Exception {
        final Path configFile = Path.of(tempDir.toAbsolutePath().toString(), "config.json");
        // create the file and set permissions
        Files.createFile(configFile);
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(tempDir)
            .build();

        final Result<?, String> result = alyxSetup.startSetup();
        assertTrue(result.isErr());
    }

    @Test
    void startSetup_HappyPath(@TempDir final Path tempDir) {
        final BotEntry expectedBotEntry = BotEntry.builder()
            .botName("Foo")
            .storageType(StorageType.JSON)
            .dataDir(tempDir)
            .build();

        final TextIO mockTextIO = mockTextIOStuff(expectedBotEntry.getBotName(), expectedBotEntry.getStorageType(),
            false, expectedBotEntry.getDataDir().toAbsolutePath().toString(), true, false);
        final Path configFile = Path.of(tempDir.toAbsolutePath().toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(tempDir)
            .textIO(mockTextIO)
            .build();

        final Result<BotEntry, String> result = alyxSetup.startSetup();
        final BotEntry botEntry = result.unwrapOrElse((e) -> {
            throw new AssertionError(e);
        });

        assertEquals(expectedBotEntry, botEntry);
    }

    @Test
    void startSetup_OverwriteBotEntry_Accept(@TempDir final Path tempDir) throws Exception {
        final BotEntry expectedBotEntry = BotEntry.builder()
            .botName("Foo")
            .storageType(StorageType.JSON)
            .dataDir(tempDir)
            .build();

        final Path configFile = Path.of(tempDir.toAbsolutePath().toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        // place the bot into the config file
        configHandler.updateBotEntry(expectedBotEntry);

        final TextIO mockTextIO = mockTextIOStuff(expectedBotEntry.getBotName(), expectedBotEntry.getStorageType(),
            true, expectedBotEntry.getDataDir().toAbsolutePath().toString(), true, false);

        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(tempDir)
            .textIO(mockTextIO)
            .build();

        final Result<BotEntry, String> result = alyxSetup.startSetup();
        final BotEntry botEntry = result.unwrapOrElse((e) -> {
            throw new AssertionError(e);
        });

        assertEquals(expectedBotEntry, botEntry);
    }

    @Test
    void startSetup_OverwriteBotEntry_Deny(@TempDir final Path tempDir) throws Exception {
        final BotEntry expectedBotEntry = BotEntry.builder()
            .botName("Foo")
            .storageType(StorageType.JSON)
            .dataDir(tempDir)
            .build();

        final Path configFile = Path.of(tempDir.toAbsolutePath().toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);
        // place the bot into the config file
        configHandler.updateBotEntry(expectedBotEntry);

        final TextIO mockTextIO = mockTextIOStuff(expectedBotEntry.getBotName(), expectedBotEntry.getStorageType(),
            false, expectedBotEntry.getDataDir().toAbsolutePath().toString(), true, true);

        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(tempDir)
            .textIO(mockTextIO)
            .build();

        final Result<BotEntry, String> result = alyxSetup.startSetup();
        Mockito.verify(mockTextIO.getTextTerminal(), Mockito.times(1)).dispose();
        assertTrue(result.isErr());
    }

    @Test
    void startSetup_ConfirmDataDirPath_Deny(@TempDir final Path tempDir) {
        final BotEntry expectedBotEntry = BotEntry.builder()
            .botName("Foo")
            .storageType(StorageType.JSON)
            .dataDir(tempDir)
            .build();

        final Path configFile = Path.of(tempDir.toAbsolutePath().toString(), "config.json");
        final ConfigHandler configHandler = new ConfigHandler(configFile);

        final TextIO mockTextIO = mockTextIOStuff(expectedBotEntry.getBotName(), expectedBotEntry.getStorageType(),
            false, expectedBotEntry.getDataDir().toAbsolutePath().toString(), false, false);

        final AlyxSetup alyxSetup = AlyxSetup.builder()
            .configHandler(configHandler)
            .defaultDataDir(tempDir)
            .textIO(mockTextIO)
            .build();

        final Result<BotEntry, String> result = alyxSetup.startSetup();
        Mockito.verify(mockTextIO.getTextTerminal(), Mockito.times(1)).dispose();
        assertTrue(result.isErr());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo", "!", "1", "fooBar", " FooBar", "FooBar "})
    void botNameChecker_Valid(final String botNameEntered) {
        assertNull(AlyxSetup.botNameChecker(botNameEntered, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Foo Bar", "", " ", "     "})
    void botNameChecker_Invalid(final String botNameEntered) {
        assertNotNull(AlyxSetup.botNameChecker(botNameEntered, null));
    }

    @Test
    void configDataPathValueChecker_Valid(@TempDir final Path tempDir) {
        assertNull(AlyxSetup.configDataPathValueChecker(tempDir.toAbsolutePath().toString(), ""));
    }

    @Test
    void configDirPathValueChecker_RelativePath() {
        final Path relativePath = Path.of("../");
        assertNotNull(AlyxSetup.configDataPathValueChecker(relativePath.toString(), null));
    }

    @Test
    void configDirPathValueChecker_FileGiven(@TempDir final Path tempDir) throws Exception {
        final Path configFile = Path.of(tempDir.toAbsolutePath().toString(), "config.json");
        Files.createFile(configFile);
        assertNotNull(AlyxSetup.configDataPathValueChecker(configFile.toAbsolutePath().toString(), null));
    }

    @Test
    void configDirValueChecker_UnwritablePath(@TempDir final Path tempDir) throws Exception {
        Files.setPosixFilePermissions(tempDir, PosixFilePermissions.fromString("r--r-x---"));
        assertNotNull(AlyxSetup.configDataPathValueChecker(tempDir.toAbsolutePath().toString(), null));
    }

    @Test
    void configDirValueChecker_CreateNewDirectory(@TempDir final Path tempDir) throws Exception {
        // create a new directory under the tempdir and remove write permissions so the create will fail
        final Path configDir = Path.of(tempDir.toAbsolutePath().toString(), "/configDir/");
        assertNull(AlyxSetup.configDataPathValueChecker(configDir.toAbsolutePath().toString(), null));
    }

    @Test
    void configDirValueChecker_ErrorCreatingDirectory(@TempDir final Path tempDir) throws Exception {
        // create a new directory under the tempdir and remove write permissions so the create will fail
        final Path newDir = Path.of(tempDir.toAbsolutePath().toString(), "/newDir/");
        final Path configDir = Path.of(newDir.toAbsolutePath().toString(), "/configDir/");
        Files.createDirectory(newDir);
        Files.setPosixFilePermissions(newDir, PosixFilePermissions.fromString("r--r-x---"));
        assertNotNull(AlyxSetup.configDataPathValueChecker(configDir.toAbsolutePath().toString(), null));
    }
}

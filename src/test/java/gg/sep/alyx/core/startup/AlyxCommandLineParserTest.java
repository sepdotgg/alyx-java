package gg.sep.alyx.core.startup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AlyxCommandLineParser}.
 */
public class AlyxCommandLineParserTest {

    @Test
    void parseArgs_SetupOnly() throws Exception {
        final String[] shortArgs = new String[] {"-s"};
        final String[] longArgs = new String[] {"--setup"};

        final CommandLine shortCL = AlyxCommandLineParser.parseArgs(shortArgs);
        final CommandLine longCL = AlyxCommandLineParser.parseArgs(longArgs);
        assertTrue(shortCL.hasOption("s"));
        assertTrue(longCL.hasOption("s"));
        assertFalse(shortCL.hasOption("b"));
        assertFalse(longCL.hasOption("b"));
    }

    @Test
    void buildArguments_SetupOnly() throws Exception {
        final String[] args = new String[] {"-s"};
        final CommandLine cl = AlyxCommandLineParser.parseArgs(args);
        final AlyxStartupArguments arguments = AlyxCommandLineParser.buildArguments(cl);

        assertTrue(arguments.isSetup());
        assertEquals(Optional.empty(), arguments.getBotName());
        assertEquals(Optional.empty(), arguments.getConfigPath());
    }

    @Test
    void parseArgs_SetupWithConfigFile() throws Exception {
        final String[] shortArgs = new String[] {"-s", "-config", "/path/to/config.json"};
        final String[] longArgs = new String[] {"--setup", "--config", "/path/to/config.json"};
        final String[] mixedArgs = new String[] {"--setup", "-config", "/path/to/config.json"};

        final CommandLine shortCL = AlyxCommandLineParser.parseArgs(shortArgs);
        final CommandLine longCL = AlyxCommandLineParser.parseArgs(longArgs);
        final CommandLine mixedCL = AlyxCommandLineParser.parseArgs(mixedArgs);

        assertTrue(shortCL.hasOption("s"));
        assertTrue(longCL.hasOption("s"));
        assertTrue(mixedCL.hasOption("s"));
        assertTrue(shortCL.hasOption("config"));
        assertTrue(longCL.hasOption("config"));
        assertTrue(mixedCL.hasOption("config"));
        assertEquals("/path/to/config.json", shortCL.getOptionValue("config"));
        assertEquals("/path/to/config.json", longCL.getOptionValue("config"));
        assertEquals("/path/to/config.json", mixedCL.getOptionValue("config"));
    }

    @Test
    void buildArguments_SetupWithConfigFile() throws Exception {
        final String[] args = new String[] {"-s", "-config", "/path/to/config.json"};
        final CommandLine cl = AlyxCommandLineParser.parseArgs(args);
        final AlyxStartupArguments arguments = AlyxCommandLineParser.buildArguments(cl);

        assertTrue(arguments.isSetup());
        assertEquals(Optional.of(Path.of("/path/to/config.json")), arguments.getConfigPath());
        assertEquals(Optional.empty(), arguments.getBotName());
    }

    @Test
    void parseArgs_BotOnly() throws Exception {
        final String[] shortArgs = new String[] {"-b", "Foo"};
        final String[] longArgs = new String[] {"--bot", "Foo"};

        final CommandLine shortCL = AlyxCommandLineParser.parseArgs(shortArgs);
        final CommandLine longCL = AlyxCommandLineParser.parseArgs(longArgs);
        assertTrue(shortCL.hasOption("b"));
        assertTrue(longCL.hasOption("b"));
        assertFalse(shortCL.hasOption("s"));
        assertFalse(longCL.hasOption("s"));
    }

    @Test
    void buildArguments_BotOnly() throws Exception {
        final String[] args = new String[] {"-b", "Foo"};
        final CommandLine cl = AlyxCommandLineParser.parseArgs(args);
        final AlyxStartupArguments arguments = AlyxCommandLineParser.buildArguments(cl);

        assertFalse(arguments.isSetup());
        assertEquals(Optional.of("Foo"), arguments.getBotName());
        assertEquals(Optional.empty(), arguments.getConfigPath());
    }

    @Test
    void parseArgs_ConfigMissingParameter() {
        final String[] shortArgs = new String[] {"-config"};
        final String[] longArgs = new String[] {"--config"};

        assertThrows(ParseException.class, () -> AlyxCommandLineParser.parseArgs(shortArgs));
        assertThrows(ParseException.class, () -> AlyxCommandLineParser.parseArgs(longArgs));
    }

    @Test
    void parseArgs_BotMissingParameter() {
        final String[] shortArgs = new String[] {"-b"};
        final String[] longArgs = new String[] {"--bot"};

        assertThrows(ParseException.class, () -> AlyxCommandLineParser.parseArgs(shortArgs));
        assertThrows(ParseException.class, () -> AlyxCommandLineParser.parseArgs(longArgs));
    }
}

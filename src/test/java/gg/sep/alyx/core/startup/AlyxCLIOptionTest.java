package gg.sep.alyx.core.startup;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AlyxCLIOption}.
 */
public class AlyxCLIOptionTest {

    @Test
    void addAllToOptions_AllOptionsAdded() {
        final Options testOptions = new Options();
        AlyxCLIOption.addAllToOptions(testOptions);

        for (final AlyxCLIOption alyxOption : AlyxCLIOption.values()) {
            final Option cliOption = testOptions.getOption(alyxOption.getOpt());
            assertNotNull(cliOption);
            assertEquals(alyxOption.getLongOpt(), cliOption.getLongOpt());
            assertEquals(alyxOption.getDescription(), cliOption.getDescription());
            assertEquals(alyxOption.isHasArg(), cliOption.hasArg());
        }
    }
}

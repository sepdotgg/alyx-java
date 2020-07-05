package gg.sep.alyx.plugins;

import static gg.sep.alyx.plugins.PluginConstants.ALYX_PLUGIN_SERIAL;

import java.time.Duration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.AlyxBot;
import gg.sep.alyx.core.commands.Command;
import gg.sep.alyx.core.commands.StatelessAlyxPlugin;
import gg.sep.alyx.core.commands.permissions.PermissionLevel;
import gg.sep.alyx.replies.InteractiveActions;

/**
 * Command to administer the Alyx bot instance.
 */
public class AdminCommandsPlugin extends StatelessAlyxPlugin {
    private static final String NAME = "AlyxAdminCommands";

    /**
     * Initialize the plugin.
     * @param alyx Instance of Alyx.
     */
    public AdminCommandsPlugin(final AlyxBot alyx) {
        super(NAME, ALYX_PLUGIN_SERIAL, true, alyx);
    }

    /**
     * Instructs Alyx to begin the teardown process and shutdown.
     *
     * Asks the user for confirmation before starting the shutdown process.
     *
     * @param event Shutdown command message event.
     */
    @Command(name = "shutdown", level = PermissionLevel.BOT_ADMIN)
    public void shutdown(final MessageReceivedEvent event) {

        InteractiveActions.yesOrNo(getAlyx(), event, "Confirm shutdown", Duration.ofSeconds(15))
            .thenAccept(response -> {
                if (Boolean.TRUE.equals(response)) {
                    event.getChannel().sendMessage("Shutting down... :wave:").queue(message -> {
                        getAlyx().shutdown();
                    });
                }
            });
    }

    /**
     * Instructs Alyx to begin the teardown process and shutdown.
     *
     * This command will not ask for confirmation before shutting down.
     *
     * @param event Shutdown command message event.
     */
    @Command(name = "shutdown", level = PermissionLevel.BOT_ADMIN)
    @Command(name = "now", level = PermissionLevel.BOT_ADMIN)
    public void shutdownNow(final MessageReceivedEvent event) {
        event.getChannel().sendMessage("Shutting down... :wave:").queue(message -> {
            getAlyx().shutdown();
        });
    }
}

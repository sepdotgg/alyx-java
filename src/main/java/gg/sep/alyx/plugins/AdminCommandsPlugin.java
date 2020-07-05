package gg.sep.alyx.plugins;

import static gg.sep.alyx.plugins.PluginConstants.ALYX_PLUGIN_SERIAL;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.Alyx;
import gg.sep.alyx.core.commands.Command;
import gg.sep.alyx.core.commands.StatelessAlyxPlugin;
import gg.sep.alyx.core.commands.permissions.PermissionLevel;

/**
 * Command to administer the Alyx bot instance.
 */
public class AdminCommandsPlugin extends StatelessAlyxPlugin {
    private static final String NAME = "AlyxAdminCommands";

    /**
     * Initialize the plugin.
     * @param alyx Instance of Alyx.
     */
    public AdminCommandsPlugin(final Alyx alyx) {
        super(NAME, ALYX_PLUGIN_SERIAL, true, alyx);
    }

    /**
     * Immediately shuts down the Alyx bot instance.
     * @param event Shutdown command message event.
     */
    @Command(name = "shutdown", level = PermissionLevel.BOT_ADMIN)
    public void shutdown(final MessageReceivedEvent event) {
        event.getChannel().sendMessage("Shutting down... :wave:").queue(message -> {
            getAlyx().shutdown();
        });
    }
}

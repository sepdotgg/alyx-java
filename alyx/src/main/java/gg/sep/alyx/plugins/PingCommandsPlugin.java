package gg.sep.alyx.plugins;

import static gg.sep.alyx.plugins.PluginConstants.ALYX_PLUGIN_SERIAL;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.pf4j.Extension;

import gg.sep.alyx.plugin.Alyx;
import gg.sep.alyx.plugin.StatelessAlyxPlugin;
import gg.sep.alyx.plugin.commands.Command;

/**
 * Command to test Alyx commands.
 */
@Extension
public class PingCommandsPlugin extends StatelessAlyxPlugin {
    private static final String NAME = "PingCommandsPlugin";

    /**
     * Initialize the plugin.
     * @param alyx The instance of Alyx for which to initialize this plugin.
     */
    public PingCommandsPlugin(final Alyx alyx) {
        super(NAME, ALYX_PLUGIN_SERIAL, false, alyx);
    }

    /**
     * Ping!
     *
     * @param event The event which triggered the ping.
     */
    @Command(name = "ping")
    public void ping(final MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!").queue();
    }
}

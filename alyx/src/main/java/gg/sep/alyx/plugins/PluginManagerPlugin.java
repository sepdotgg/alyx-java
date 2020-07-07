package gg.sep.alyx.plugins;

import static gg.sep.alyx.plugins.PluginConstants.ALYX_PLUGIN_SERIAL;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.pf4j.Extension;

import gg.sep.alyx.plugin.Alyx;
import gg.sep.alyx.plugin.AlyxException;
import gg.sep.alyx.plugin.AlyxPlugin;
import gg.sep.alyx.plugin.StatelessAlyxPlugin;
import gg.sep.alyx.plugin.commands.Command;

/**
 * Default plugin for managing other plugins in Alyx.
 * This plugin is guarded, and cannot be unloaded without an explicit override.
 *
 * See {@link AlyxPlugin#isGuarded()} for info.
 */
@Extension
public class PluginManagerPlugin extends StatelessAlyxPlugin {
    private static final String NAME = "AlyxPluginManager";

    public static final String PLUGIN_IDENTIFIER = String.format("%s.%s", ALYX_PLUGIN_SERIAL, NAME);
    /**
     * Initializes a new instance of the Plugin Manager Plugin.
     * @param alyx The instance of Alyx for which to initialize this plugin.
     */
    public PluginManagerPlugin(final Alyx alyx) {
        super(NAME, ALYX_PLUGIN_SERIAL, true, alyx);
    }

    /**
     * Lists the currently registered plugins in this bot's instance.
     *
     * @param event The message event which triggered the command.
     */
    @Command(name = "plugins")
    @Command(name = "list")
    public void listPlugins(final MessageReceivedEvent event) {
        final List<String> lines = new ArrayList<>();

        getAlyx().getRegisteredPlugins()
            .forEach(plugin -> {
                final String pluginPrefix = plugin.isLoaded() ? "✅" : "❌";
                lines.add(pluginPrefix + " " + plugin.getName());
            });

        final String response = String.join("\n", lines);
        event.getChannel().sendMessage(response).queue();
    }

    /**
     * Loads the specified registered plugin into this Alyx instance.
     *
     * @param event The message event which triggered the command.
     * @param pluginName The name of the plugin.
     * @throws AlyxException Exception thrown if loading a plugin fails.
     */
    @Command(name = "plugins")
    @Command(name = "load")
    public void loadPlugin(final MessageReceivedEvent event, final String pluginName) throws AlyxException {
        for (final AlyxPlugin<?> plugin : getAlyx().getRegisteredPlugins()) {
            if (pluginName.trim().equals(plugin.getName())) {
                getAlyx().loadPlugin(plugin);
                event.getMessage().addReaction("✅").queue();
                return;
            }
        }
        event.getMessage().addReaction("❌").queue();
        event.getMessage().getChannel().sendMessage("No plugin with that name was found.").queue();
    }

    /**
     * Unloads the specified registered plugin from this Alyx instance.
     *
     * @param event The message event which triggered the command.
     * @param pluginName The name of the plugin.
     * @throws AlyxException Thrown if unloading the plugin failed (eg, it is guarded or not registered).
     */
    @Command(name = "plugins")
    @Command(name = "unload")
    public void unloadPlugin(final MessageReceivedEvent event, final String pluginName) throws AlyxException {

        for (final AlyxPlugin<?> plugin : getAlyx().getRegisteredPlugins()) {
            if (pluginName.trim().equals(plugin.getName())) {
                getAlyx().unloadPlugin(plugin);
                event.getMessage().addReaction("✅").queue();
                return;
            }
        }
        event.getMessage().addReaction("❌").queue();
        event.getMessage().getChannel().sendMessage("No plugin with that name was found.").queue();
    }
}

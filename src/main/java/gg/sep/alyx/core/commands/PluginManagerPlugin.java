package gg.sep.alyx.core.commands;

import static gg.sep.alyx.core.commands.CommandConstants.ALYX_PLUGIN_SERIAL;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.Alyx;
import gg.sep.alyx.AlyxException;

/**
 * Default plugin for managing other plugins in Alyx.
 * This plugin is guarded, and cannot be unloaded without an explicit override.
 *
 * See {@link #AlyxPlugin#isGuarded()} for info.
 */
public class PluginManagerPlugin extends AlyxPlugin {
    private static final String NAME = "AlyxPluginManager";

    /**
     * Initializes a new instance of the Plugin Manager Plugin.
     * @param alyx Instance of Alyx which will use this plugin.
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

        alyx.getRegisteredPlugins()
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
        for (final AlyxPlugin plugin : alyx.getRegisteredPlugins()) {
            if (pluginName.trim().equals(plugin.getName())) {
                alyx.loadPlugin(plugin);
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

        for (final AlyxPlugin plugin : alyx.getRegisteredPlugins()) {
            if (pluginName.trim().equals(plugin.getName())) {
                alyx.unloadPlugin(plugin);
                event.getMessage().addReaction("✅").queue();
                return;
            }
        }
        event.getMessage().addReaction("❌").queue();
        event.getMessage().getChannel().sendMessage("No plugin with that name was found.").queue();
    }
}

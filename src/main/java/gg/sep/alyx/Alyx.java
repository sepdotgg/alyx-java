package gg.sep.alyx;

import java.util.Collection;
import java.util.Map;

import net.dv8tion.jda.api.entities.User;

import gg.sep.alyx.core.commands.AlyxCommand;
import gg.sep.alyx.core.commands.AlyxPlugin;
import gg.sep.alyx.core.commands.parsers.ParameterParser;
import gg.sep.alyx.core.events.EventWaiter;
import gg.sep.alyx.core.storage.json.JsonStorageEngine;
import gg.sep.alyx.model.config.BotEntry;

/**
 * Models an instance of an Alyx Discord bot.
 */
public interface Alyx {

    /**
     * Returns the {@link BotEntry} configuration associated with
     * this instance of {@link Alyx}.
     *
     * @return The {@link BotEntry} configuration associated with this instance of {@link Alyx}.
     */
    BotEntry getBotEntry();

    /**
     * Returns the owner of this {@link Alyx} bot per the Discord API.
     *
     * @return The bot owner's Discord {@link User}.
     */
    User getBotOwner();

    /**
     * The prefix used to trigger commands for this bot.
     *
     * @return The prefix used to trigger commands for this bot.
     */
    String getCommandPrefix();

    /**
     * The common event waiter and its thread pool which is used
     * to wait for specified Discord {@link net.dv8tion.jda.api.events.Event}s to be fired
     * in order to complete.
     *
     * See {@link EventWaiter}.
     * @return The {@link EventWaiter} used by this instance of Alyx.
     */
    EventWaiter getEventWaiter();

    /**
     * Returns the plugins that have been fully registered into this instance of {@link Alyx}.
     *
     * @return The plugins that have been fully registered into this bot instance.
     */
    Collection<AlyxPlugin<?>> getRegisteredPlugins();

    /**
     * Returns the collection of plugins which have been loaded and are available for use
     * in this instance of {@link Alyx}.
     *
     * All loaded plugins are also registered plugins. See {@link #getRegisteredPlugins()}.
     *
     * @return The plugins which have been loaded into this instance of {@link Alyx}.
     */
    Collection<AlyxPlugin<?>> getLoadedPlugins();

    /**
     * Returns the command {@link ParameterParser}s which have been registered to this
     * instance of {@link Alyx}.
     *
     * @return The command parameter parsers which have been registered in this instance.
     */
    Map<Class<?>, ParameterParser<?>> getParameterParsers();

    /**
     * Returns the commands which have been loaded into this instance of {@link Alyx}.
     *
     * @return The commands which have been loaded into this instance of {@link Alyx}.
     */
    Collection<AlyxCommand> getLoadedCommands();

    /**
     * Returns the storage engine used by this instance of Alyx.
     *
     * The storage engine is used by plugins to persist their data between
     * bot restarts, and provides an interface for retrieving that data.
     *
     * @return {@link gg.sep.alyx.core.storage.AlyxStorageEngine} used by this
     * instance of {@link Alyx}.
     */
    JsonStorageEngine getStorageEngine(); // TODO: Move methods up to the abstract

    /**
     * Registers a new {@link AlyxPlugin} and makes it available for usage in the bot.
     *
     * TODO: Validate command conflicts
     * TODO: Allow loading/unloading of plugins. Registering just makes it available, but does not load.
     * @param plugin The plugin to register.
     */
    void registerPlugin(AlyxPlugin<?> plugin);

    /**
     * Loads the specified plugin into this instance of Alyx.
     *
     * @param plugin The plugin to load.
     * @throws AlyxException Exception thrown if the plugin is not registered.
     */
    void loadPlugin(AlyxPlugin<?> plugin) throws AlyxException;

    /**
     * Unloads the specified plugin from this instance of Alyx.
     *
     * If the plugin is guarded, an {@link AlyxException} will be thrown.
     *
     * @param plugin The plugin to unload.
     * @throws AlyxException Exception thrown if unloading the plugin fails (it is not registered or is guarded).
     */
    void unloadPlugin(AlyxPlugin<?> plugin) throws AlyxException;

    /**
     * Registers a {@link ParameterParser} for use in Alyx when parsing command strings.
     * TODO: This should happen as part of the plugin registration process.
     *       Make them specific to plugins so that each plugin can handle same types differently?
     *
     * @param parser The parser to register. This will (currently) override any other existing parsers for
     *               the parser's type.
     */
    void registerParameterParser(ParameterParser<?> parser);

    /**
     * Gracefully shuts down this instance of Alyx.
     */
    void shutdown();
}

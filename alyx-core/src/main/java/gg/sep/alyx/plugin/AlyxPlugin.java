package gg.sep.alyx.plugin;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.pf4j.ExtensionPoint;

import gg.sep.alyx.plugin.commands.AlyxCommand;
import gg.sep.alyx.plugin.commands.Command;
import gg.sep.alyx.plugin.storage.JsonSerializable;

/**
 * A plugin for {@link Alyx}, containing commands and event listeners which can be loaded into a bot instance.
 *
 * @param <D> The type of the data for this plugin.
 *
 * TODO: Consider making plugins semi-stateless, ie, not need to have an instance of Alyx included.
 *       This would instead require Alyx be passed in as a parameter to each command method, but
 *       allows better scalability since you can share instances of plugins across Alyx instances.
 */
public interface AlyxPlugin<D extends JsonSerializable> extends ExtensionPoint {

    /**
     * Returns a string which uniquely identifies this plugin.
     *
     * Normally this is some combination of the {@link #getName()}} value
     * and a predefined serial number (Snowflake ID).
     * @return A string which uniquely identifies this plugin.
     */
    String getIdentifier();

    /**
     * Returns the friendly name of the plugin.
     *
     * This name is often surfaced to users.
     * @return The friendly name of the plugin.
     */
    String getName();

    /**
     * Returns the instance of {@link Alyx} where this plugin is
     * being used.
     * @return The instance of {@link Alyx} where this plugin is installed.
     */
    Alyx getAlyx();

    /**
     * Returns whether the plugin is guarded.
     *
     * Guarded plugins cannot be de-registered or unloaded without explicitly
     * overriding the safeguards. This permission is reserved, by default, for only
     * the bot owner.
     *
     * For example, the core plugins which are responsible for loading other plugins
     * would be guarded since, once disabled, there would be no way to ever enable it again.
     *
     * Your plugin implementation should use this only in extreme cases, and
     * even then you should think really hard about whether its right for the job.
     * @return Returns {@code true} if the plugin is guarded.
     */
    boolean isGuarded();

    /**
     * Checks whether the plugin is registered into Alyx.
     *
     * @return Returns {@code true} if the plugin is registered into Alyx.
     */
    boolean isRegistered();

    /**
     * Checks whether the plugin is loaded and active in Alyx.
     *
     * @return Returns {@code true} if the plugin is loaded and active in Alyx.
     */
    boolean isLoaded();

    /**
     * Hook called when the plugin is registered.
     * TODO: Make this a future
     */
    void register();

    /**
     * Hook called when the plugin is loaded.
     * TODO: Make this a future
     */
    void load();

    /**
     * Hook called when the plugin is unloaded.
     * TODO: Make this a future
     */
    void unload();

    /**
     * Hook called when the bot is shut down.
     *
     * This should <em>not</em> block the main thread, but instead return a future which can be waited
     * on by the shutdown process across all plugins.
     *
     * @return Returns a completable future which can be monitored for the shutdown process.
     */
    CompletableFuture<Void> botShutdown();

    /**
     * Returns the {@link Command} commands present in this plugin.
     *
     * @return Collection of the commands present in this plugin.
     * @throws AlyxException Exception thrown if loading a plugin's commands fails.
     *                       Usually this happens during the {@link Alyx#loadPlugin(AlyxPlugin)}
     *                       execution, and the error should be surfaced to the user.
     */
    Collection<AlyxCommand> loadCommands() throws AlyxException;

    /**
     * Returns the class of this plugin's data object.
     * @return The class of this plugin's data object.
     */
    Class<D> storageDataType();

    /**
     * Returns the plugin's data that is currently loaded.
     * @return The currently loaded plugin data.
     */
    D getPluginData();

    /**
     * Writes the plugin's data to the storage engine.
     */
    void writePluginData();

    /**
     * Loads the plugin's data from the storage engine.
     * @return Optional of the plugin's data if successfully loaded, otherwise empty.
     */
    Optional<D> loadPluginData();
}

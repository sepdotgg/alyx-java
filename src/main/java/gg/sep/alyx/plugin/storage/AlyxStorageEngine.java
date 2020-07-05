package gg.sep.alyx.plugin.storage;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Interface for interacting with the bot's data storage.
 */
public interface AlyxStorageEngine {

    /**
     * Load's data for a plugin from a JSON file.
     *
     * @param pluginId Unique identifier of the plugin.
     * @param botDataPath Path to the Bot's data directory.
     * @param dataClass Class model of the plugin's data.
     * @param <T> Type of the data class.
     * @return The plugin's data file loaded from the JSON file if found, otherwise an empty optional.
     */
    <T extends JsonSerializable> Optional<T> loadPluginData(String pluginId, Path botDataPath, Class<T> dataClass);

    /**
     * Writes a plugin's data to its database JSON file.
     *
     * @param pluginId Unique identifier of the plugin.
     * @param botDataPath Path to the Bot's data directory.
     * @param pluginData The plugin's data.
     * @param <T> Type of the plugin's data.
     */
    <T extends JsonSerializable> void writePluginData(String pluginId, Path botDataPath, T pluginData);


}

package gg.sep.alyx.core.storage.json;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import gg.sep.alyx.core.commands.NoOpPluginData;
import gg.sep.alyx.core.storage.AlyxStorageEngine;
import gg.sep.alyx.model.JsonSerializable;
import gg.sep.alyx.util.ModelParser;

/**
 * Class to interact with the bot's JSON data storage.
 */
public class JsonStorageEngine implements AlyxStorageEngine {

    /**
     * Load's data for a plugin from a JSON file.
     *
     * @param pluginId Unique identifier of the plugin.
     * @param botDataPath Path to the Bot's data directory.
     * @param dataClass Class model of the plugin's data.
     * @param <T> Type of the data class.
     * @return The plugin's data file loaded from the JSON file if found, otherwise an empty optional.
     */
    public <T extends JsonSerializable> Optional<T> loadPluginData(
        final String pluginId,
        final Path botDataPath,
        final Class<T> dataClass) {

        if (dataClass.equals(NoOpPluginData.class)) {
            return Optional.empty();
        }

        final Path pluginDataFile = getDbFilePath(botDataPath, pluginId);

        try {
            ensureConfigFile(pluginDataFile);
            final String configContents = Files.readString(pluginDataFile);
            return ModelParser.parseJson(configContents, dataClass);
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Writes a plugin's data to its database JSON file.
     *
     * @param pluginId Unique identifier of the plugin.
     * @param botDataPath Path to the Bot's data directory.
     * @param pluginData The plugin's data.
     * @param <T> Type of the plugin's data.
     */
    public <T extends JsonSerializable> void writePluginData(
        final String pluginId,
        final Path botDataPath,
        final T pluginData) {

        if (pluginData.getClass().equals(NoOpPluginData.class)) {
            return;
        }

        final Path pluginDataFile = getDbFilePath(botDataPath, pluginId);

        try {
            final String output = pluginData.toPrettyJson();
            ensureConfigFile(pluginDataFile);
            Files.writeString(pluginDataFile, output);
        } catch (final IOException e) {
            throw new RuntimeException("Error writing config.");
        }
    }

    private static Path getDbFilePath(final Path pluginDataDir, final String pluginId) {
        final Path pluginDirectory = pluginDataDir.resolve("plugins").resolve(pluginId);
        return pluginDirectory.resolve("data.json");
    }

    private void ensureConfigFile(final Path filePath) throws IOException {
        final Path parentPath = filePath.getParent();
        if (parentPath == null) {
            throw new IOException("Config file path cannot be a root directory.");
        }
        if (!Files.exists(filePath)) {
            Files.createDirectories(parentPath);
            Files.createFile(filePath);
        }
    }
}

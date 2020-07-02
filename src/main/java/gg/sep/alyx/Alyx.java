package gg.sep.alyx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.security.auth.login.LoginException;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import gg.sep.alyx.core.commands.AlyxCommand;
import gg.sep.alyx.core.commands.AlyxPlugin;
import gg.sep.alyx.core.commands.TestCommandsPlugin;
import gg.sep.alyx.core.config.ConfigHandler;
import gg.sep.alyx.events.AlyxCommandListener;
import gg.sep.alyx.model.config.BotConfig;
import gg.sep.alyx.model.config.BotEntry;

/**
 * The Alyx bot instance.
 */
public final class Alyx {
    private final BotEntry botEntry;
    private final BotConfig botConfig;
    private final JDA jda;

    @Getter private final String commandPrefix;
    @Getter private final Set<AlyxPlugin> registeredPlugins = Collections.synchronizedSet(new HashSet<>());
    @Getter private final Set<AlyxPlugin> loadedPlugins = Collections.synchronizedSet(new HashSet<>());
    @Getter private final List<AlyxCommand> commandsList = new ArrayList<>();

    private Alyx(final BotEntry botEntry) throws LoginException, IOException {
        this.botEntry = botEntry;
        this.botConfig = loadBotConfig(botEntry);
        this.commandPrefix = botConfig.getCommandPrefix().toString();

        this.jda = JDABuilder.createLight(botConfig.getDiscordToken())
            .addEventListeners(new AlyxCommandListener(this))
            .setActivity(Activity.playing("Type !ping"))
            .build();
    }

    /**
     * Launches a new instance of Alyx for the provided {@link BotEntry}.
     *
     * @param botEntry Bot Entry with metadata about the bot and where to find the bot's configuration.
     * @return New instance of Alyx.
     */
    public static Alyx launchBot(final BotEntry botEntry) {
        try {
            final Alyx alyx = new Alyx(botEntry);
            alyx.registerPlugin(new TestCommandsPlugin(alyx));
            return alyx;
        } catch (final LoginException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a new {@link AlyxPlugin} and makes it available for usage in the bot.
     *
     * TODO: Validate command conflicts
     * TODO: Allow loading/unloading of plugins. Registering just makes it available, but does not load.
     * @param plugin The plugin to register.
     */
    public void registerPlugin(final AlyxPlugin plugin) {

        // TODO: Loading/unloading won't be here so remove the try/catch
        try {
            this.registeredPlugins.add(plugin);
            this.loadPlugin(plugin);
        } catch (final AlyxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down this instance of Alyx.
     */
    public void shutdown() {
        this.jda.shutdownNow();
    }

    private void loadPlugin(final AlyxPlugin plugin) throws AlyxException {
        if (!registeredPlugins.contains(plugin)) {
            throw new AlyxException(
                String.format("Plugin '%s' is not registered.", plugin.getName())
            );
        }
        this.loadedPlugins.add(plugin);
        this.commandsList.addAll(plugin.loadCommands());
    }

    private void unloadPlugin(final AlyxPlugin plugin) throws AlyxException {
        if (!registeredPlugins.contains(plugin) || !loadedPlugins.contains(plugin)) {
            throw new AlyxException(
                String.format("Plugin '%s' is not registered or loaded", plugin.getName())
            );
        }
        this.loadedPlugins.remove(plugin);
        this.commandsList.removeAll(plugin.loadCommands());
    }

    /**
     * Attempts to load the a bot's configuration file.
     *
     * @param botEntry BotEntry of the bot.
     * @return The bot's configuration, if found.
     * @throws IOException Exception thrown if loading the bot's configuration file fails.
     */
    private static BotConfig loadBotConfig(final BotEntry botEntry) throws IOException {
        final ConfigHandler handler = new ConfigHandler(botEntry.getDataDir());
        final Optional<BotConfig> botConfig = handler.loadBotConfig(botEntry);
        return botConfig.orElseThrow(() ->
            new IOException("Error loading the Bot's config from: " + botEntry.getDataDir()));
    }
}

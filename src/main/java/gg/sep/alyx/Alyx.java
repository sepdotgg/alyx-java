package gg.sep.alyx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.security.auth.login.LoginException;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;

import gg.sep.alyx.core.commands.AlyxCommand;
import gg.sep.alyx.core.commands.AlyxPlugin;
import gg.sep.alyx.core.commands.parsers.DoubleParameterParser;
import gg.sep.alyx.core.commands.parsers.InstantParameterParser;
import gg.sep.alyx.core.commands.parsers.IntegerParameterParser;
import gg.sep.alyx.core.commands.parsers.LongParameterParser;
import gg.sep.alyx.core.commands.parsers.ParameterParser;
import gg.sep.alyx.core.commands.parsers.StringParameterParser;
import gg.sep.alyx.core.commands.parsers.discord.ChannelParameterParser;
import gg.sep.alyx.core.commands.parsers.discord.EmoteParameterParser;
import gg.sep.alyx.core.commands.parsers.discord.RoleParameterParser;
import gg.sep.alyx.core.commands.parsers.discord.UserParameterParser;
import gg.sep.alyx.core.commands.plugins.AdminCommandsPlugin;
import gg.sep.alyx.core.commands.plugins.PluginManagerPlugin;
import gg.sep.alyx.core.config.ConfigHandler;
import gg.sep.alyx.events.AlyxCommandListener;
import gg.sep.alyx.events.EventWaiter;
import gg.sep.alyx.model.config.BotConfig;
import gg.sep.alyx.model.config.BotEntry;

/**
 * The Alyx bot instance.
 */
public final class Alyx {
    private final BotEntry botEntry;
    private final BotConfig botConfig;
    @Getter private final JDA jda;

    @Getter
    private final EventWaiter eventWaiter;
    @Getter
    private final String commandPrefix;
    @Getter
    private final User botOwner;
    @Getter
    private final Set<AlyxPlugin> registeredPlugins = Collections.synchronizedSet(new HashSet<>());
    @Getter
    private final Set<AlyxPlugin> loadedPlugins = Collections.synchronizedSet(new HashSet<>());
    @Getter
    private final Map<Class<?>, ParameterParser<?>> parameterParsers = new ConcurrentHashMap<>();
    @Getter
    private final List<AlyxCommand> commandsList = new ArrayList<>();

    private Alyx(final BotEntry botEntry) throws LoginException, IOException {
        this.botEntry = botEntry;
        this.botConfig = loadBotConfig(botEntry);
        this.commandPrefix = botConfig.getCommandPrefix().toString();
        this.eventWaiter = new EventWaiter();

        this.jda = JDABuilder.createDefault(botConfig.getDiscordToken())
            .addEventListeners(new AlyxCommandListener(this))
            .addEventListeners(eventWaiter)
            .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
            .setAutoReconnect(true)
            .setActivity(Activity.playing("λ Half Life: Alyx"))
            .build();

        this.botOwner = retrieveBotOwner(this.jda);
    }

    /**
     * Registers a {@link ParameterParser} for use in Alyx when parsing command strings.
     * TODO: This should happen as part of the plugin registration process.
     *       Make them specific to plugins so that each plugin can handle same types differently?
     *
     * @param parser The parser to register. This will (currently) override any other existing parsers for
     *               the parser's type.
     */
    public void registerParameterParser(final ParameterParser<?> parser) {
        this.parameterParsers.put(parser.getType(), parser);
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
            final PluginManagerPlugin pluginManager = new PluginManagerPlugin(alyx);
            final AdminCommandsPlugin adminPlugin = new AdminCommandsPlugin(alyx);
            alyx.registerDefaultParsers();

            alyx.registerPlugin(pluginManager);
            alyx.registerPlugin(adminPlugin);

            alyx.loadPlugin(pluginManager);
            alyx.loadPlugin(adminPlugin);
            return alyx;
        } catch (final AlyxException | LoginException | IOException e) {
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
            if (!this.registeredPlugins.add(plugin)) {
                throw new AlyxException("A matching plugin already exists.");
            }
            plugin.register();
        } catch (final AlyxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shuts down this instance of Alyx.
     */
    public void shutdown() {
        // TODO: Plugin shutdown procedures
        this.jda.shutdownNow();
    }

    /**
     * Loads the specified plugin into this instance of Alyx.
     *
     * @param plugin The plugin to load.
     * @throws AlyxException Exception thrown if the plugin is not registered.
     */
    public void loadPlugin(final AlyxPlugin plugin) throws AlyxException {
        if (!registeredPlugins.contains(plugin)) {
            throw new AlyxException(
                String.format("Plugin '%s' is not registered.", plugin.getName())
            );
        }

        this.loadedPlugins.add(plugin);
        this.commandsList.addAll(plugin.loadCommands());
        plugin.load();
    }

    /**
     * Unloads the specified plugin from this instance of Alyx.
     *
     * If the plugin is guarded, an {@link AlyxException} will be thrown.
     *
     * @param plugin The plugin to unload.
     * @throws AlyxException Exception thrown if unloading the plugin fails (it is not registered or is guarded).
     */
    public void unloadPlugin(final AlyxPlugin plugin) throws AlyxException {
        this.unloadPlugin(plugin, false);
    }

    /**
     * TODO: Make this method public, but add permissions to override the guard.
     * @param plugin The plugin to unload.
     * @param guardOverride If set to {@code true}, guarded plugins can be unloaded.
     *                      This is extremely dangerous since it's possible they might
     *                      not ever be able to be loaded again without modifying config files (eg, PluginManager).
     * @throws AlyxException Exception thrown if unloading the plugin fails (it is not registered or is guarded).
     */
    private void unloadPlugin(final AlyxPlugin plugin, final boolean guardOverride) throws AlyxException {
        if (plugin.isGuarded() && !guardOverride) {
            throw new AlyxException(plugin.getName() + " is a guarded plugin.");
        }

        if (!registeredPlugins.contains(plugin) || !loadedPlugins.contains(plugin)) {
            throw new AlyxException(
                String.format("Plugin '%s' is not registered or loaded", plugin.getName())
            );
        }
        plugin.unload();
        this.loadedPlugins.remove(plugin);
        this.commandsList.removeAll(plugin.loadCommands());
    }

    /**
     * Returns the Bot Owner {@link User} for the given instance of JDA.
     * @param jda JDA instance.
     * @return Bot owner user.
     */
    private static User retrieveBotOwner(final JDA jda) {
        return jda.retrieveApplicationInfo().complete()
            .getOwner();
    }

    /**
     * Registers the default parsers that come as a part of Alyx.
     */
    private void registerDefaultParsers() {
        List.of(
            new StringParameterParser(),
            new IntegerParameterParser(),
            new DoubleParameterParser(),
            new LongParameterParser(),
            new InstantParameterParser(),

            // Discord types
            new UserParameterParser(),
            new RoleParameterParser(),
            new ChannelParameterParser(),
            new EmoteParameterParser()
        ).forEach(this::registerParameterParser);
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

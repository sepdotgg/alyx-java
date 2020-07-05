package gg.sep.alyx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import javax.security.auth.login.LoginException;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
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
import gg.sep.alyx.core.config.ConfigHandler;
import gg.sep.alyx.core.events.AlyxCommandListener;
import gg.sep.alyx.core.events.EventWaiter;
import gg.sep.alyx.core.storage.json.JsonStorageEngine;
import gg.sep.alyx.model.config.BotConfig;
import gg.sep.alyx.model.config.BotEntry;
import gg.sep.alyx.plugins.AdminCommandsPlugin;
import gg.sep.alyx.plugins.PluginManagerPlugin;

/**
 * The Alyx bot instance.
 */
@Log4j2
public final class AlyxBot implements Alyx {
    private final BotConfig botConfig;
    private final AlyxCommandListener commandListener;
    private volatile boolean isShutdown = false;
    private final JDA jda;

    @Getter
    private final BotEntry botEntry;
    @Getter
    private final EventWaiter eventWaiter;
    @Getter
    private final String commandPrefix;
    @Getter
    private final User botOwner;
    @Getter
    private final Collection<AlyxPlugin<?>> registeredPlugins = Collections.synchronizedSet(new HashSet<>());
    @Getter
    private final Collection<AlyxPlugin<?>> loadedPlugins = Collections.synchronizedSet(new HashSet<>());
    @Getter
    private final Map<Class<?>, ParameterParser<?>> parameterParsers = new ConcurrentHashMap<>();
    @Getter
    private final Collection<AlyxCommand> loadedCommands = new ArrayList<>();
    @Getter
    private final JsonStorageEngine storageEngine = new JsonStorageEngine();

    private AlyxBot(final BotEntry botEntry) throws LoginException, IOException {
        this.botEntry = botEntry;
        this.botConfig = loadBotConfig(botEntry);
        this.commandPrefix = botConfig.getCommandPrefix().toString();
        this.eventWaiter = new EventWaiter(botEntry.getBotName());
        this.commandListener = new AlyxCommandListener(this);

        this.jda = JDABuilder.createDefault(botConfig.getDiscordToken())
            .addEventListeners(this.commandListener)
            .addEventListeners(eventWaiter)
            .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)
            .setAutoReconnect(true)
            .setActivity(Activity.playing("λ Half Life: Alyx"))
            .build();

        this.botOwner = retrieveBotOwner(this.jda);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerParameterParser(final ParameterParser<?> parser) {
        this.parameterParsers.put(parser.getType(), parser);
    }

    /**
     * Launches a new instance of Alyx for the provided {@link BotEntry}.
     *
     * @param botEntry Bot Entry with metadata about the bot and where to find the bot's configuration.
     * @return New instance of Alyx.
     */
    public static AlyxBot launchBot(final BotEntry botEntry) {
        try {
            final AlyxBot alyx = new AlyxBot(botEntry);
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
     * {@inheritDoc}
     */
    @Override
    public void registerPlugin(final AlyxPlugin<?> plugin) {

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
    @Override
    public synchronized void shutdown() {
        // don't run the shutdown process twice
        if (isShutdown) {
            log.error("Attempted to shut down an already shut down bot.");
            return;
        }
        this.isShutdown = true;

        // stop listening for new commands
        this.commandListener.setListening(false);

        // tell plugins that we're shutting down
        // they have 30 seconds to complete and should respond asynchronously
        final List<AlyxPlugin<?>> currentPlugins = new ArrayList<>(this.getRegisteredPlugins());
        final Map<AlyxPlugin<?>, CompletableFuture<?>> futureMap = new HashMap<>();

        for (final AlyxPlugin<?> plugin : currentPlugins) {
            futureMap.put(plugin, plugin.botShutdown().orTimeout(30, TimeUnit.SECONDS));
        }

        final CompletableFuture<Void> allShutdown = CompletableFuture.allOf(futureMap.values()
            .toArray(new CompletableFuture[0]));

        // confirm plugins are shut down
        allShutdown.exceptionally(throwable -> {
            // find the plugins that failed
            for (final Map.Entry<AlyxPlugin<?>, CompletableFuture<?>> entry : futureMap.entrySet()) {
                if (entry.getValue().isCancelled() || entry.getValue().isCompletedExceptionally()) {
                    log.error("Error shutting down plugin. plugin={}", entry.getKey().getIdentifier());
                }
            }
            log.error("Error shutting down plugins.", throwable);
            return null;
        }).join();

        // shutdown anything in EventWaiter
        this.eventWaiter.shutdown(60, TimeUnit.SECONDS).handle((result, throwable) -> {
            if (!result || throwable != null) {
                log.error("Failed to shut down all EventWaiter tasks. This may result in additional errors.", throwable);
            }
            return result;
        }).join(); // block

        // don't shutdown JDA until we know it's safe or the timeout has been reached
        this.jda.shutdown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadPlugin(final AlyxPlugin<?> plugin) throws AlyxException {
        if (!registeredPlugins.contains(plugin)) {
            throw new AlyxException(
                String.format("Plugin '%s' is not registered.", plugin.getName())
            );
        }

        this.loadedPlugins.add(plugin);
        this.loadedCommands.addAll(plugin.loadCommands());
        plugin.load();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unloadPlugin(final AlyxPlugin<?> plugin) throws AlyxException {
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
    private void unloadPlugin(final AlyxPlugin<?> plugin, final boolean guardOverride) throws AlyxException {
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
        this.loadedCommands.removeAll(plugin.loadCommands());
    }

    /**
     * Returns the Bot Owner {@link User} for the given instance of JDA.
     *
     * This is a blocking call.
     *
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

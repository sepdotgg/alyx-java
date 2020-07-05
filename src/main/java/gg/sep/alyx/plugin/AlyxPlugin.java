package gg.sep.alyx.plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.Alyx;
import gg.sep.alyx.core.storage.json.JsonStorageEngine;
import gg.sep.alyx.model.JsonSerializable;
import gg.sep.alyx.plugin.commands.AlyxCommand;
import gg.sep.alyx.plugin.commands.Command;
import gg.sep.alyx.plugin.commands.ParameterParser;

/**
 * A plugin for {@link Alyx}, containing commands and event listeners which can be loaded into a bot instance.
 *
 * @param <C> The type of the data for this plugin.
 *
 * TODO: Consider making plugins semi-stateless, ie, not need to have an instance of Alyx included.
 *       This would instead require Alyx be passed in as a parameter to each command method, but
 *       allows better scalability since you can share instances of plugins across Alyx instances.
 */
public abstract class AlyxPlugin<C extends JsonSerializable> {
    private C pluginData = null;
    @Getter
    private final Alyx alyx;
    @Getter
    private boolean guarded;
    @Getter
    private final String identifier;
    @Getter
    private final String name;

    protected AlyxPlugin(final String name, final long serial, final Alyx alyx) {
        this.name = name;
        this.alyx = alyx;
        this.identifier = String.format("%s.%s", serial, name);
        this.guarded = false;
    }

    protected AlyxPlugin(final String name, final long serial, final boolean isGuarded, final Alyx alyx) {
        this(name, serial, alyx);
        this.guarded = isGuarded;
    }

    /**
     * Returns the class of this plugin's data object.
     * @return The class of this plugin's data object.
     */
    public abstract Class<C> storageDataType();

    /**
     * Generates a fresh plugin data object in the event one does not already exist.
     * @return Fresh plugin data object.
     */
    protected abstract C freshPluginData();

    /**
     * Returns the plugin's data that is currently loaded.
     * @return The currently loaded plugin data.
     */
    public C getPluginData() {
        return this.pluginData;
    }

    /**
     * Writes the plugin's data to the storage engine.
     */
    public synchronized void writePluginData() {
        final JsonStorageEngine storageEngine = alyx.getStorageEngine();
        storageEngine.writePluginData(identifier, this.alyx.getBotEntry().getDataDir(), getPluginData());
    }

    /**
     * Loads the plugin's data from the storage engine.
     * @return Optional of the plugin's data if successfully loaded, otherwise empty.
     */
    public synchronized Optional<C> loadPluginData() {
        final JsonStorageEngine storageEngine = alyx.getStorageEngine();
        return storageEngine.loadPluginData(identifier, this.alyx.getBotEntry().getDataDir(), this.storageDataType());
    }

    /**
     * Checks whether the plugin is registered into Alyx.
     *
     * @return Returns {@code true} if the plugin is registered into Alyx.
     */
    public boolean isRegistered() {
        return this.alyx.getRegisteredPlugins().contains(this);
    }

    /**
     * Checks whether the plugin is loaded and active in Alyx.
     *
     * @return Returns {@code true} if the plugin is loaded and active in Alyx.
     */
    public boolean isLoaded() {
        return this.alyx.getLoadedPlugins().contains(this);
    }


    /**
     * Hook called when the plugin is registered.
     * TODO: Make this a future
     */
    public void register() { }

    /**
     * Hook called when the plugin is loaded.
     * TODO: Make this a future
     */
    public void load() {
        this.pluginData = loadPluginData().orElseGet(this::freshPluginData);
    }

    /**
     * Hook called when the plugin is unloaded.
     * TODO: Make this a future
     */
    public void unload() {
        this.writePluginData();
    }

    /**
     * Hook called when the bot is shut down.
     *
     * This should <em>not</em> block the main thread, but instead return a future which can be waited
     * on by the shutdown process across all plugins.
     *
     * @return Returns a completable future which can be monitored for the shutdown process.
     */
    public CompletableFuture<Void> botShutdown() {
        return CompletableFuture.runAsync(this::writePluginData);
    }

    /**
     * Returns the {@link Command} commands present in this plugin.
     *
     * @return Collection of the commands present in this plugin.
     * @throws AlyxException Exception thrown if loading a plugin's commands fails.
     *                       Usually this happens during the {@link Alyx#loadPlugin(AlyxPlugin)}
     *                       execution, and the error should be surfaced to the user.
     */
    public Collection<AlyxCommand> loadCommands() throws AlyxException {
        final Method[] methods = this.getClass().getDeclaredMethods();
        final Set<AlyxCommand> commands = new HashSet<>();

        for (final Method method : methods) {
            final Command[] cmdAnnotations = method.getAnnotationsByType(Command.class);
            if (cmdAnnotations != null && cmdAnnotations.length > 0) {
                commands.add(parseCommand(cmdAnnotations, method));
            }
        }
        return commands;
    }

    /**
     * Parses a method and its {@link Command} annotations into an {@link AlyxCommand}.
     *
     * @param commands Array of the {@link Command} annotations present on the {@code method}.
     * @param method The method which will be used to execute the command.
     * @return Constructed {@link AlyxCommand} from the plugin's method.
     * @throws AlyxException Exception thrown if extracting the command's paramter parsers fails.
     */
    private AlyxCommand parseCommand(final Command[] commands, final Method method) throws AlyxException {
        final List<List<String>> commandChain = getCommandChain(commands);
        final List<ParameterParser<?>> commandParsers = extractParsers(method);

        // use the permissions associated with the last command for now
        // TODO
        final Command lastCommand = commands[commands.length - 1];
        final String[] requiredRoles = lastCommand.roles();
        final Permission[] requiredPermissions = lastCommand.permissions();

        return new AlyxCommand(
            this,
            Arrays.asList(requiredPermissions),
            Arrays.asList(requiredRoles),
            lastCommand.level(),
            lastCommand.guildOnly(),
            getCommandName(commands),
            commandChain,
            commandParsers,
            method
        );
    }

    /**
     * Returns the name of the command.
     *
     * If the command is a nested command, will return the each's command name joined by a space.
     *
     * @param commands Array of commands on the method.
     * @return The name of the command.
     */
    private String getCommandName(final Command[] commands) {
        final List<String> names = Stream.of(commands)
            .map(Command::name)
            .collect(Collectors.toList());
        return String.join(" ", names);
    }

    /**
     * Generates a list of the command's name plus its aliases for each of the commands
     * in the method's command chain.
     *
     * For example, given the following method:
     *
     *   {@code
     *     @Command(name = "foo", aliases = {"one", "two"})
     *     @Command(name = "bar", aliases = {"baz"})
     *     public void command(MessageReceivedEvent event) {
     *
     *     }
     *   }
     *
     * The result of method would be: {@code [ ["foo", "one" two"], ["bar, "baz"] ]}.
     *
     * The method could be triggered by any one of:
     *   - {@code !foo bar}
     *   - {@code !one baz}
     *   - {@code !two bar}
     *
     * @param commands Array of command annotations on the command method.
     * @return List of the command's name plus its aliases which makes up the command's name chain.
     */
    private List<List<String>> getCommandChain(final Command[] commands) {
        return Stream.of(commands)
            .map(command -> {
                final List<String> withAliases = new ArrayList<>();
                withAliases.add(command.name());
                withAliases.addAll(Arrays.asList(command.aliases()));
                return withAliases;
            })
            .collect(Collectors.toList());
    }

    /**
     * Extracts a list of parameter parsers that are needed to execute this command.
     *
     * This list is ordered, and should match the exact size of parameters passed to
     * the command.
     * @param method The command's method.
     * @return List of parameter parsers that are needed to execute this command.
     * @throws AlyxException Exception thrown if a parser has not been registered for a given
     *                       parameter type on the method.
     */
    private List<ParameterParser<?>> extractParsers(final Method method) throws AlyxException {
        final List<ParameterParser<?>> parsers = new ArrayList<>();
        for (final Parameter parameter : method.getParameters()) {
            if (parameter.getType().equals(MessageReceivedEvent.class)) {
                continue;
            }

            final ParameterParser<?> parser = alyx.getParameterParsers().get(parameter.getType());
            if (parser == null) {
                throw new AlyxException(String.format("A parameter parser for '%s' does not exist", parameter.getType()));
            }
            parsers.add(parser);
        }
        return parsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.identifier);
    }

    /**
     * Checks whether this plugin is the same as another plugin.
     *
     *
     * @param other The other plugin.
     * @return Returns {@code true} if it is the same plugin.
     */
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof AlyxPlugin)) {
            return false;
        }
        final AlyxPlugin<?> plugin = (AlyxPlugin<?>) other;
        return this.identifier.equals(plugin.identifier);
    }
}

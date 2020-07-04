package gg.sep.alyx.core.commands;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.AlyxException;
import gg.sep.alyx.core.commands.parsers.ParameterParser;
import gg.sep.alyx.core.permissions.PermissionLevel;
import gg.sep.alyx.util.Strings;

/**
 * Represents a single command which can be executed/invoked by Alyx.
 */
@AllArgsConstructor
public final class AlyxCommand {
    private final AlyxPlugin plugin;

    @Setter private Collection<Permission> requiredPermissions;
    @Setter private Collection<String> requiredRoles;
    @Setter private PermissionLevel permissionLevel;
    @Setter private boolean guildOnly;

    private final String name;
    private final List<List<String>> commandChain;
    private final List<ParameterParser<?>> parsers;

    private final Method method;

    /**
     * Creates a new AlyxCommand.
     *
     * @param plugin The plugin where the command is defined.
     * @param name The name of the command.
     * @param commandChain The chain of commands needed to execute the command.
     * @param parsers Parameter parsers used by the command.
     * @param method The Java method which executes the command.
     */
    public AlyxCommand(final AlyxPlugin plugin, final String name, final List<List<String>> commandChain,
                       final List<ParameterParser<?>> parsers, final Method method) {

        this(plugin, Collections.emptyList(), Collections.emptyList(),
            PermissionLevel.EVERYONE, false, name, commandChain, parsers, method);
    }

    /**
     * Checks whether this command responds to the supplied command string.
     *
     * @param messageText The event's message text minus the bot's prefix.
     * @return Returns {@code true} if this command handles the event text command string.
     */
    public boolean matches(final String messageText) {
        final String[] splitCheck = Strings.splitWithQuotes(messageText);

        if (splitCheck.length < commandChain.size()) {
            return false;
        }

        for (int i = 0; i < commandChain.size(); i++) {
            final String cmdPortion = splitCheck[i];
            if (!commandChain.get(i).contains(cmdPortion.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Extracts the parameter passed to a bot command, removing the command chain from
     * the start of the string.
     *
     * @param messageText The raw event message text, minus the command prefix.
     * @return Array of string parameters to pass to the command invoker.
     */
    private String[] extractParameters(final String messageText) {
        // we've already validated the command string at this point
        final String[] splitMessageText = Strings.splitWithQuotes(messageText);
        final List<String> parameters = new ArrayList<>();
        final int parametersLen = splitMessageText.length - commandChain.size();

        // skip past the command's in the message and grab just the parameters (if any)
        for (int i = 0; i < parametersLen; i++) {
            final int argsPosition = i + commandChain.size();
            parameters.add(splitMessageText[argsPosition]);
        }
        // clear out any empty parameters
        // TODO: This may be a valid use case in the future for empty double quotes
        if (parameters.size() == 0 || parameters.stream().allMatch(Strings::isBlank)) {
            return new String[]{};
        }
        return parameters.toArray(new String[0]);
    }

    /**
     * Checks whether a command method receives the raw message event as the first parameter.
     *
     * @return Returns {@code true} if the command's method receives the Discord event as the first parameter.
     */
    private boolean receivesEvent() {
        final Parameter[] parameters = method.getParameters();
        return parameters.length > 0 && parameters[0].getType().equals(MessageReceivedEvent.class);
    }

    /**
     * Invokes the command, passing any parameters to the command's method.
     *
     * If the parameters do not match the format of the method's parameters,
     * the command's help text will instead by sent back to the user.
     *
     * @param event The Discord message event which triggered the command.
     * @param messageText The raw event message text, minus the command prefix.
     * @throws AlyxException Exception thrown if invoking the command failed.
     */
    public void invoke(final MessageReceivedEvent event, final String messageText) throws AlyxException {

        if (!canUseCommand(event)) {
            return;
        }

        final String[] parameterArgs = extractParameters(messageText);

        // We matched the command, but it's missing parameters or has too many parameters
        if (parameterArgs.length != parsers.size()) {
            sendCommandHelp(event);
            return;
        }

        // place the event at the start of the array if the method receives the event as a parameter
        final Object[] invokeArgs;
        if (receivesEvent()) {
            invokeArgs = new Object[parameterArgs.length + 1];
            invokeArgs[0] = event;
        } else {
            invokeArgs = new Object[parameterArgs.length];
        }

        // parse the parameter strings into their typed counterparts
        for (int i = 0; i < parameterArgs.length; i++) {
            final Object parsed = parsers.get(i).parse(parameterArgs[i], event);
            invokeArgs[i + 1] = parsed;
        }

        try {
            method.invoke(plugin, invokeArgs);
        } catch (final ReflectiveOperationException e) {
            if (e.getCause() instanceof AlyxException) {
                throw (AlyxException) e.getCause();
            }
            // TODO (release): Wrap this in an AlyxException
            // TODO: Leaving this as a Runtime exception during development
            throw new RuntimeException(e);
        }
    }

    private boolean canUseCommand(final MessageReceivedEvent event) {

        // check if this is a guild only command being called in a private channel
        if (!event.isFromGuild() && guildOnly) {
            return false;
        }

        // if there's no required roles or permissions, return true
        if (requiredRoles.size() == 0
            && requiredPermissions.size() == 0
            && permissionLevel == PermissionLevel.EVERYONE) {
            return true;
        }

        final User eventUser = event.getAuthor();
        final User botOwner = plugin.alyx.getBotOwner();

        // bot owner
        if (eventUser.equals(botOwner)) {
            return true;
        }

        // guild permission checks
        if (event.isFromGuild() && event.getMember() != null) {
            // role check
            final List<String> userRoles = event.getMember().getRoles().stream().map(Role::getName)
                .collect(Collectors.toList());

            final boolean hasRole = requiredRoles.stream().anyMatch(userRoles::contains);
            if (hasRole) {
                return true;
            }

            // permissions check
            for (final Permission permission : requiredPermissions) {
                final boolean hasChannelPermission = permission.isChannel() &&
                    event.getMember().hasPermission(event.getTextChannel(), permission);
                if (hasChannelPermission) {
                    return true;
                }

                final boolean hasGlobalPermission = event.getMember().hasPermission(permission);
                if (hasGlobalPermission) {
                    return true;
                }
            }
        }
        final PermissionLevel userPermissionLevel = PermissionLevel.getLevel(eventUser, plugin.alyx);
        if (userPermissionLevel.isOk(permissionLevel)) {
            return true;
        }
        // TODO: Private Channel permissions
        return false;
    }

    /**
     * Sends the command's usage documentation back to the user.
     *
     * @param event The event which triggered the command.
     */
    private void sendCommandHelp(final MessageReceivedEvent event) {
        // TODO
        event.getChannel().sendMessage(this.name + " Help").queue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(plugin.getIdentifier(), this.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        // TODO Serial
        return (obj instanceof AlyxCommand) && this.hashCode() == obj.hashCode();
    }
}

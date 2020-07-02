package gg.sep.alyx.core.commands;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Represents a single command which can be executed/invoked by Alyx.
 */
@RequiredArgsConstructor
public final class AlyxCommand {
    private final AlyxPlugin plugin;
    private final String name;
    private final Collection<String> aliases;
    private final Method method;

    /**
     * Checks whether this command responds to the supplied command string.
     * @param command The command string minus the bot's prefix.
     * @return Returns {@code true} if this command handles the command string.
     */
    public boolean matches(final String command) {
        return command.equals(name) || aliases.contains(command);
    }

    /**
     * Invokes the command.
     *
     * @param event The Discord message event which triggered the command.
     */
    public void invoke(final MessageReceivedEvent event) {
        try {
            method.invoke(plugin, event);
        } catch (final ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        // TODO: Serial
        return Objects.hash(AlyxCommand.class, this.name);
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

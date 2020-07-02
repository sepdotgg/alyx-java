package gg.sep.alyx.core.commands;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.Getter;

import gg.sep.alyx.Alyx;

/**
 * Plugin for Alyx.
 */
public abstract class AlyxPlugin {
    private final long serial;
    protected final Alyx alyx;
    @Getter private final String identifier;
    @Getter private final String name;

    protected AlyxPlugin(final String name, final long serial, final Alyx alyx) {
        this.name = name;
        this.alyx = alyx;
        this.serial = serial;
        this.identifier = String.format("%s.%s", serial, name);
    }

    /**
     * Returns the {@link Command} commands present in this plugin.
     * @return Collection of the commands present in this plugin.
     */
    public Collection<AlyxCommand> loadCommands() {
        final Method[] methods = this.getClass().getDeclaredMethods();
        final Set<AlyxCommand> commands = new HashSet<>();

        for (final Method method : methods) {
            final Command cmdAnnotation = method.getAnnotation(Command.class);
            if (cmdAnnotation != null) {
                final AlyxCommand command = new AlyxCommand(
                    this,
                    cmdAnnotation.name(),
                    Arrays.asList(cmdAnnotation.aliases()),
                    method
                );
                commands.add(command);
            }
        }
        return commands;
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
        final AlyxPlugin plugin = (AlyxPlugin) other;
        return this.identifier.equals(plugin.identifier);
    }
}

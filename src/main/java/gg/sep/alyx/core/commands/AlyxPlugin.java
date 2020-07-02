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
 * TODO: Serial
 */
public abstract class AlyxPlugin {
    protected final Alyx alyx;
    @Getter protected final String name;

    protected AlyxPlugin(final String name, final Alyx alyx) {
        this.name = name;
        this.alyx = alyx;
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
        return Objects.hash(AlyxPlugin.class, this.name);
    }

    /**
     * Checks whether this plugin is the same as another plugin.
     *
     * // TODO: Add serials to handle plugin name conflicts.
     *
     * @param plugin The other plugin.
     * @return Returns {@code true} if it is the same plugin.
     */
    @Override
    public boolean equals(final Object plugin) {
        if (!(plugin instanceof AlyxPlugin)) {
            return false;
        }
        return this.name.equals(((AlyxPlugin) plugin).getName());
    }
}

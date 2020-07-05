package gg.sep.alyx.core.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.dv8tion.jda.api.Permission;

import gg.sep.alyx.core.commands.permissions.PermissionLevel;

/**
 * Annotation used on {@link AlyxPlugin} methods to designate bot command handlers.
 */
@Repeatable(NestedCommand.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {
    /**
     * Name of the command (eg, a command of "!ping" has a name of "ping").
     * @return Name of the command.
     */
    String name();

    /**
     * Aliases that the command should also respond to.
     *
     * For example, if you want the bot to also respond to "!pong" and "!tennis",
     * you would supply {@code {"pong", "tennis"}}
     *
     * @return The aliases the command should also respond to.
     */
    String[] aliases() default {};

    /**
     * Any of the specified {@link Permission} are allowed to execute this command.
     * @return Array of the permissions associated with this command. If a user has any
     *         of these permissions, they will be allowed to execute the command.
     */
    Permission[] permissions() default {};

    /**
     * Role names which are allowed to execute this command (for Guild channels).
     *
     * @return Array of role names which are allowed to execute this command.
     */
    String[] roles() default {};

    /**
     * The minimum permission level required to run this command.
     *
     * Defaults to {@link PermissionLevel#EVERYONE}.
     *
     * @return The minimum permission level required to run this command.
     */
    PermissionLevel level() default PermissionLevel.EVERYONE;

    /**
     * Whether the command can only be executed within the context of a Guild/Server.
     *
     * Defaults to {@code false}.
     *
     * @return Returns {@code true} if the command
     */
    boolean guildOnly() default false;
}

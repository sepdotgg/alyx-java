package gg.sep.alyx.core.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used on {@link AlyxPlugin} methods to designate bot command handlers.
 */
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
     * The permission level of the user required to execute the command.
     * TODO: Implement this.
     * @return The permission level.
     */
    int level() default 0;
}

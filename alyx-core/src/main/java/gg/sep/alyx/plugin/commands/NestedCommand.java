package gg.sep.alyx.plugin.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Repeatable annotation container for {@link Command}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NestedCommand {
    /**
     * Array of the {@link Command} annotations present on the method.
     * @return The {@link Command} annotations present on the method.
     */
    Command[] value();
}

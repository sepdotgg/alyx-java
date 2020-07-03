package gg.sep.alyx.core.permissions;

import net.dv8tion.jda.api.entities.User;

import gg.sep.alyx.Alyx;

/**
 * Permission levels which can be used to determine who is allowed to execute a command.
 *
 * TODO: Right now this only works for users who get assigned BOT_OWNER. We'll need config
 * and plugin config checks to get the other levels working.
 */
public enum PermissionLevel {
    BOT_OWNER(Double.POSITIVE_INFINITY),
    BOT_ADMIN(Double.MAX_VALUE),
    ADMIN(100_000_000D),
    MOD(50_000_000D),

    EVERYONE(Double.NEGATIVE_INFINITY);

    private final Double level;

    PermissionLevel(final double level) {
        this.level = level;
    }

    /**
     * Checks whether this permission is at or above the {@code other} permission.
     *
     * In this instance {@code other} should typically be the permission assigned to a
     * {@link gg.sep.alyx.core.commands.Command#level()}.
     *
     * @param other A command's required permission to check against.
     * @return Returns {@code true} if this permission level is at or above the {@code other} permission.
     */
    public boolean isOk(final PermissionLevel other) {
        return this.level >= other.level;
    }

    /**
     * Returns the PermissionLevel for the given user.
     * @param user User for which to get a Permission Level.
     * @param alyx Alyx bot instance.
     * @return Permission level if found, otherwise returns {@link #EVERYONE};
     */
    public static PermissionLevel getLevel(final User user, final Alyx alyx) {
        if (user.equals(alyx.getBotOwner())) {
            return BOT_OWNER;
        }
        // TODO Check user permissions
        return EVERYONE;
    }
}

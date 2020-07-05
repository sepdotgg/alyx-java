package gg.sep.alyx.plugin;

/**
 * Recoverable exception which can be thrown in Alyx.
 *
 * IMPORTANT: The exception can (and likely will) be surfaced to the
 *            user who called it in Discord or administrators of the bot.
 *            The message should not contain any sensitive information.
 */
public class AlyxException extends Exception {
    /**
     * Create a new {@link AlyxException} with the given exception message.
     *
     * IMPORTANT: The exception can (and likely will) be surfaced to the
     *            user who called it in Discord or administrators of the bot.
     *            The message should not contain any sensitive information.
     * @param message Exception message.
     */
    public AlyxException(final String message) {
        super(message);
    }
}

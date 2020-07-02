package gg.sep.alyx;

/**
 * Recoverable exception which can be thrown in Alyx.
 */
public class AlyxException extends Exception {
    /**
     * Create a new {@link AlyxException} with the given exception message.
     * @param message Exception message.
     */
    public AlyxException(final String message) {
        super(message);
    }
}

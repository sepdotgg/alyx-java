package gg.sep.alyx.util.result;

/**
 * Exception thrown by {@link Result}'s when attempting to unwrap an invalid value.<br>
 * <br>
 * Thrown when attempting to {@link Result#unwrap()} an {@link Err} value, or
 * {@link Result#unwrapErr()} an {@link Ok} value. <br>
 *
 * By extension, this also gets thrown by {@link Result#expect(String)}} and
 * {@link Result#expectErr(String)} as these methods also unwrap.
 *
 * This is the Java version of a "panic" by Rust's Result implementation.
 */
public class ResultException extends RuntimeException {

    /**
     * Constructs a new Result exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message The detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ResultException(final String message) {
        super(message);
    }
}

package gg.sep.alyx.replies;

/**
 * An {@link EmbedReply} which can be used to represent error messages.
 *
 * Contains lots of red and X's.
 */
public class ErrorReply extends EmbedReply {
    /**
     * Constructs a ErrorReply embed reply.
     * @param msg The message to set in the embed.
     */
    public ErrorReply(final String msg) {
        super(msg, EmbedColors.ERROR, "❌");
    }
}

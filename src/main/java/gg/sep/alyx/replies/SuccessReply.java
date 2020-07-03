package gg.sep.alyx.replies;

/**
 * An {@link EmbedReply} which can be used to represent success messages.
 *
 * Contains lots of green and checkmarks.
 */
public class SuccessReply extends EmbedReply {

    /**
     * Constructs a SuccessReply embed reply.
     * @param msg The message to set in the embed.
     */
    public SuccessReply(final String msg) {
        super(msg, EmbedColors.SUCCESS, "✅");
    }
}

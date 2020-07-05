// Generated by delombok at Sun Jul 05 15:37:37 PDT 2020
package gg.sep.alyx.plugin.replies;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * A collection of utilities for responding to message events.
 */
public final class ResponseUtils {
    private static final String ERROR = "❌";
    private static final String SUCCESS = "✅";

    /**
     * If able, add a "cross" X emoji reaction to the event message, signaling failure.
     * @param event The message event.
     */
    public static void cross(final MessageReceivedEvent event) {
        addReactionIfAble(event, ERROR);
    }

    /**
     * If able, add a "checkmark" emoji reaction to the event message, signaling success.
     * @param event The message event.
     */
    public static void check(final MessageReceivedEvent event) {
        addReactionIfAble(event, SUCCESS);
    }

    private static void addReactionIfAble(final MessageReceivedEvent event, final String reaction) {
        if (!event.isFromGuild()) {
            event.getMessage().addReaction(reaction).queue();
        }
        final Member botMember = event.getGuild().getMember(event.getJDA().getSelfUser());
        if (botMember != null && botMember.hasPermission(Permission.MESSAGE_ADD_REACTION)) {
            event.getMessage().addReaction(reaction).queue();
        }
    }

    @java.lang.SuppressWarnings("all")
    @lombok.Generated
    private ResponseUtils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

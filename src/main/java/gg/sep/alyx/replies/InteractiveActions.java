package gg.sep.alyx.replies;

import java.awt.Color;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import gg.sep.alyx.Alyx;
import gg.sep.alyx.events.EventWaiter;

/**
 * Utility class which contains helpers for building common "interactive" wizard-like
 * interactions with Discord, such as requiring a user to choose a true/false/confirm/deny
 * answer, and return that response back to the calling command.
 */
public final class InteractiveActions {

    private static final String YES_EMOJI = "✅";
    private static final String NO_EMOJI = "❌";

    private InteractiveActions() { }

    private static Predicate<MessageReactionAddEvent> yesNoReactionPredicate(final String userId,
                                                                             final String messageId) {

        return event -> {
            if (messageId.equals(event.getMessageId()) && userId.equals(event.getUserId())) {
                return event.getReactionEmote().getEmoji().equals(YES_EMOJI) ||
                    event.getReactionEmote().getEmoji().equals(NO_EMOJI);
            }
            return false;
        };
    }

    private static Predicate<MessageReceivedEvent> yesNoMessagePredicate(final String userId,
                                                                         final MessageChannel channel) {
        return event -> {
            if (channel.equals(event.getChannel()) && userId.equals(event.getAuthor().getId())) {
                return List.of("y", "n").contains(
                    event.getMessage().getContentRaw().trim().toLowerCase()
                );
            }
            return false;
        };
    }

    private static void addReactions(final Message message, final String... reactions) {
        final List<CompletableFuture<Void>> reactionFutures = new ArrayList<>();
        for (final String reaction : reactions) {
            reactionFutures.add(message.addReaction(reaction).submit());
        }

        try {
            CompletableFuture.allOf(reactionFutures.toArray(new CompletableFuture[0])).get();
        } catch (final InterruptedException | ExecutionException e) {
            // do nothing
        }
    }

    /**
     * Starts a Yes/No interactive session.
     *
     * This action will send the {@code message} into the channel which triggered (contained on the {@code event}).
     * It will then ask the user to choose a true/false (yes/no, confirm/deny, etc) response and will
     * wait up until the given {@code timeout} for a response.<br><br>
     *
     * By default, this method will attempt to use checkmark/X reaction emojis to record the response. If permissions
     * are not available to do so, it will instead wait for a text {@code y} or {@code n} response from
     * the triggering user in the same channel as the message.<br><br>
     *
     * A future eventually containing a {@link Boolean} representing the user's response is immediately sent back to
     * caller. The caller can decide what to do with this future (block until complete, follow up with a callback).
     * <br><br>
     *
     * If the timeout is reached and no response has been recorded, the future's data will {@code null}.
     *
     * @param alyx The instance of Alyx which triggered this action.
     * @param event The message event which triggered the action.
     * @param message The message (question, etc) to present to the user along with the question.
     * @param timeout The maximum duration to wait for a valid response event.
     * @return A future containing the result of a yes/no response (true/false), or {@code null} if
     *         the timeout was reached before a response was received.
     */
    public static CompletableFuture<Boolean> yesOrNo(final Alyx alyx, final MessageReceivedEvent event,
                                                     final String message, final Duration timeout) {

        final boolean canReact = !event.getChannelType().isGuild() || event.getGuild().getSelfMember()
            .hasPermission(event.getTextChannel(), Permission.MESSAGE_ADD_REACTION);

        final String embedMessage = canReact ? message : message + "\n\n(y/n)";
        final EmbedReply reply = new EmbedReply(embedMessage, Color.BLUE, "❓");

        final Message noticeMessage = event.getChannel().sendMessage(reply.build()).complete();
        final EventWaiter waiter = alyx.getEventWaiter();

        if (canReact) {
            final Predicate<MessageReactionAddEvent> predicate = yesNoReactionPredicate(event.getAuthor().getId(),
                noticeMessage.getId());
            final Function<MessageReactionAddEvent, Boolean> getAnswer = e ->
                e.getReactionEmote().getEmoji().equals(YES_EMOJI);
            addReactions(noticeMessage, YES_EMOJI, NO_EMOJI);
            return waiter.waitForEvent(MessageReactionAddEvent.class, predicate, getAnswer, timeout);
        }

        final Predicate<MessageReceivedEvent> predicate = yesNoMessagePredicate(event.getAuthor().getId(),
            event.getChannel());
        final Function<MessageReceivedEvent, Boolean> checkAnswer = e ->
            e.getMessage().getContentRaw().trim().toLowerCase().equals("y");
        return waiter.waitForEvent(MessageReceivedEvent.class, predicate, checkAnswer, timeout);
    }
}

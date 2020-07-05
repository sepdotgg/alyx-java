package gg.sep.alyx.core.events;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import gg.sep.alyx.AlyxBot;
import gg.sep.alyx.AlyxException;
import gg.sep.alyx.core.commands.AlyxCommand;

/**
 * A abstract listener for Alyx which can be extended in order to listen
 * to any type of Discord Event received by the bot.
 *
 * All events can be listened to by your implemented listener, with the
 * exception of {@link MessageReceivedEvent}, {@link PrivateMessageReceivedEvent}, and
 * {@link GuildMessageReceivedEvent} events. These events will be handled by the bot's
 * command processor.
 *
 * If you wish to listen for these events in your bot, you'll need to explicitly add
 * a new listener adapter to Alyx's JDA instance.
 * TODO: Make provide a simple way to do this (eg, spam/moderation bots would need this).
 *
 * See {@link ListenerAdapter} for details of what can be listened to.
 */
@RequiredArgsConstructor
public abstract class AbstractAlyxCommandListener extends AbstractAlyxListenerAdapter {
    protected final AlyxBot alyx;

    /**
     * Catches {@link PrivateMessageReceivedEvent} events and does nothing with them. They will
     * be handled by {@link #onMessageReceived(MessageReceivedEvent)}.
     * @param event Private message received event.
     */
    @Override
    public final void onPrivateMessageReceived(@Nonnull final PrivateMessageReceivedEvent event) {
    }

    /**
     * Catches {@link GuildMessageReceivedEvent} events and does nothing with them. They will
     * be handled by {@link #onMessageReceived(MessageReceivedEvent)}.
     * @param event Guild message received event.
     */
    @Override
    public final void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
    }

    /**
     * Handles a {@link MessageReceivedEvent} from either a guild or private Discord channel
     * and submits it to the bot's command processor.
     *
     * @param event The {@link MessageReceivedEvent} event which triggered was received by the bot.
     */
    @Override
    public final void onMessageReceived(final MessageReceivedEvent event) {
        if (!isListening()) {
            return;
        }

        final String rawMessage = event.getMessage().getContentRaw();
        if (rawMessage.startsWith(alyx.getCommandPrefix())) {
            final String cmd = removeBotCommandPrefix(rawMessage);

            final List<AlyxCommand> matchingCommands = this.alyx.getLoadedCommands().stream()
                .filter(command -> command.matches(cmd))
                .collect(Collectors.toList());

            // if there's more than one command, choose the one with the longest command chain
            AlyxCommand executeCommand = null;
            if (matchingCommands.size() > 1) {
                int longest = 0;
                AlyxCommand longestCommand = null;

                for (final AlyxCommand command : matchingCommands) {
                    if (command.getCommandChain().size() > longest) {
                        longest = command.getCommandChain().size();
                        longestCommand = command;
                    }
                }
                executeCommand = longestCommand;
            } else if (!matchingCommands.isEmpty()) {
                executeCommand = matchingCommands.get(0);
            }

            if (executeCommand != null) {
                try {
                    executeCommand.invoke(event, cmd);
                } catch (final AlyxException e) {
                    event.getMessage().addReaction("❌").queue();
                    // TODO: This may contain private information.
                    event.getChannel().sendMessage(e.getMessage()).queue();
                }
            }
        }
    }

    /**
     * Removes the bot's command prefix from a message's text.
     *
     * @param rawMessageText The raw message text received from a {@link net.dv8tion.jda.api.entities.Message}.
     * @return The raw message text with the bot's command prefix removed.
     */
    private String removeBotCommandPrefix(final String rawMessageText) {
        return rawMessageText.substring(alyx.getCommandPrefix().length());
    }
}

package gg.sep.alyx.events;

import javax.annotation.Nonnull;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import gg.sep.alyx.Alyx;

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
public abstract class AbstractAlyxCommandListener extends ListenerAdapter {
    protected final Alyx alyx;

    @Override
    public final void onPrivateMessageReceived(@Nonnull final PrivateMessageReceivedEvent event) {
    }

    @Override
    public final void onGuildMessageReceived(@Nonnull final GuildMessageReceivedEvent event) {
    }

    /**
     * Handles a {@link MessageReceivedEvent} from either a guild or private Discord channel
     * and submits it to the bot's command processor.
     * @param event The {@link MessageReceivedEvent} event which triggered.
     */
    @Override
    public final void onMessageReceived(final MessageReceivedEvent event) {
        if (event.getMessage().getContentRaw().startsWith(alyx.getCommandPrefix())) {
            this.alyx.getCommandsList().forEach(command -> {
                if (command.matches(extractCommand(event.getMessage()))) {
                    command.invoke(event);
                }
            });
        }
    }

    private String extractCommand(final Message message) {
        return message.getContentRaw().substring(alyx.getCommandPrefix().length());
    }
}

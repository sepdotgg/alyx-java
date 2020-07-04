package gg.sep.alyx.core.commands.parsers.discord;

import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.core.commands.parsers.CommandParseException;
import gg.sep.alyx.util.Strings;

/**
 * Handles parsing of String parameters into Discord Guild Channels.
 *
 * The bot must be in the Discord channel.
 *
 * Order of evaluation:
 *   - ID
 *   - Mention
 *   - Name
 */
public class ChannelParameterParser extends MentionParser<GuildChannel> {

    /**
     * Creates a new instance of the ChannelParameterParser.
     */
    public ChannelParameterParser() {
        super(Message.MentionType.CHANNEL.getPattern(), 1);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<GuildChannel> getType() {
        return GuildChannel.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected GuildChannel getMentionedItem(final String value, final Event event) {
        if (matches(value)) {
            return event.getJDA().getGuildChannelById(getMentionId(value));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GuildChannel parse(final String value, final Event event) throws CommandParseException {
        GuildChannel channel = null;

        // match by ID
        if (Strings.isNumeric(value)) {
            channel = event.getJDA().getGuildChannelById(value);
            if (channel != null) {
                return channel;
            }
        }

        // match by mention
        channel = getMentionedItem(value, event);
        if (channel != null) {
            return channel;
        }

        return channel;
    }
}

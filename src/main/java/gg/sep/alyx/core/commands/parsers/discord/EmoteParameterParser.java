package gg.sep.alyx.core.commands.parsers.discord;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.core.plugin.CommandParseException;
import gg.sep.alyx.core.plugin.util.Strings;

/**
 * Handles parsing of String parameters into Discord Emotes.
 *
 * The bot must have access to the emotes.
 *
 * Order of evaluation:
 *   - ID
 *   - Mention
 */
public class EmoteParameterParser extends MentionParser<Emote> {

    /**
     * Creates a new instance of the EmoteParameterParser.
     */
    public EmoteParameterParser() {
        super(Message.MentionType.EMOTE.getPattern(), 2);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Emote> getType() {
        return Emote.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Emote getMentionedItem(final String value, final Event event) {
        if (matches(value)) {
            return event.getJDA().getEmoteById(getMentionId(value));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Emote parse(final String value, final Event event) throws CommandParseException {
        Emote emote = null;

        // match by ID
        if (Strings.isNumeric(value)) {
            emote = event.getJDA().getEmoteById(value);
            if (emote != null) {
                return emote;
            }
        }

        // match by mention
        emote = getMentionedItem(value, event);
        if (emote != null) {
            return emote;
        }

        return emote;
    }
}

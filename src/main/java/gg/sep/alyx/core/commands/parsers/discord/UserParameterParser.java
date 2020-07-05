package gg.sep.alyx.core.commands.parsers.discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.core.plugin.CommandParseException;
import gg.sep.alyx.core.plugin.util.Strings;

/**
 * Handles parsing of String parameters into Discord Users.
 *
 * The user must be in a mutual server with the bot.
 *
 * Order of evaluation:
 *   - ID
 *   - Mention
 */
public class UserParameterParser extends MentionParser<User> {

    /**
     * Creates a new instance of the UserParameterParser.
     */
    public UserParameterParser() {
        super(Message.MentionType.USER.getPattern(), 1);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Class<User> getType() {
        return User.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected User getMentionedItem(final String value, final Event event) {
        if (matches(value)) {
            return event.getJDA().getUserById(getMentionId(value));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User parse(final String value, final Event event) throws CommandParseException {
        User user = null;

        // match by ID
        if (Strings.isNumeric(value)) {
            user = event.getJDA().getUserById(value);
            if (user != null) {
                return user;
            }
        }

        // match by mention
        user = getMentionedItem(value, event);
        if (user != null) {
            return user;
        }

        return user;
    }
}

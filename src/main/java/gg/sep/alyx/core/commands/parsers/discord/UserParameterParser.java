package gg.sep.alyx.core.commands.parsers.discord;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.core.commands.parsers.CommandParseException;
import gg.sep.alyx.core.commands.parsers.ParameterParser;

/**
 * Handles parsing of String parameters into Discord Users.
 *
 * The ID of the user is used to identify the user. The user must be in a mutual server with the bot.
 */
@RequiredArgsConstructor
public class UserParameterParser implements ParameterParser<User> {
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
    public User parse(final String value, final Event event) throws CommandParseException {
        return event.getJDA().getUserById(value);
    }
}

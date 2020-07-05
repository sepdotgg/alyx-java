package gg.sep.alyx.core.commands.parsers.discord;

import java.util.List;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.Event;

import gg.sep.alyx.plugin.CommandParseException;
import gg.sep.alyx.util.Strings;

/**
 * Handles parsing of String parameters into Discord Roles.
 *
 * Order of evaluation:
 *   - ID
 *   - Mention
 *   - (if in a guild) Role Name, case insensitive
 */
public class RoleParameterParser extends MentionParser<Role> {

    /**
     * Creates a new instance of the RoleParameterParser.
     */
    public RoleParameterParser() {
        super(Message.MentionType.ROLE.getPattern(), 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Role> getType() {
        return Role.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Role getMentionedItem(final String value, final Event event) {
        if (matches(value)) {
            return event.getJDA().getRoleById(getMentionId(value));
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role parse(final String value, final Event event) throws CommandParseException {
        Role role = null;

        // match by ID
        if (Strings.isNumeric(value)) {
            // try getting it by number
            role = event.getJDA().getRoleById(value);
            if (role != null) {
                return role;
            }
        }

        // match by mention
        role = getMentionedItem(value, event);
        if (role != null) {
            return role;
        }

        // match by name
        final Guild guild = getGuild(event);
        if (guild != null) {
            final List<Role> roles = guild.getRoles().stream()
                .filter(r -> r.getName().equalsIgnoreCase(value))
                .collect(Collectors.toList());
            role = roles.size() > 0 ? roles.get(0) : null;
        }
        return role;
    }
}

package gg.sep.alyx.core.commands.parsers.discord;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.core.commands.parsers.CommandParseException;
import gg.sep.alyx.core.commands.parsers.ParameterParser;
import gg.sep.alyx.util.Strings;

/**
 * Handles parsing of String parameters into Discord Roles.
 *
 * It will handle both the ID of the role and, if the event originated from a Guild channel,
 * the name of the role as well.
 */
@RequiredArgsConstructor
public class RoleParameterParser implements ParameterParser<Role> {

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
    public Role parse(final String value, final Event event) throws CommandParseException {
        Role role = null;
        if (Strings.isNumeric(value)) {
            // try getting it by number
            role = event.getJDA().getRoleById(value);
        } else {
            final Guild guild = getGuild(event);
            if (guild != null) {
                final List<Role> roles = guild.getRoles().stream()
                    .filter(r -> r.getName().equalsIgnoreCase(value))
                    .collect(Collectors.toList());
                role = roles.size() > 0 ? roles.get(0) : null;
            }
        }
        return role;
    }

    private Guild getGuild(final Event event) {
        if (event instanceof GenericGuildEvent) {
            return ((GenericGuildEvent) event).getGuild();
        } else if (event instanceof MessageReceivedEvent && ((MessageReceivedEvent) event).isFromGuild()) {
            return ((MessageReceivedEvent) event).getGuild();
        }
        return null;
    }
}

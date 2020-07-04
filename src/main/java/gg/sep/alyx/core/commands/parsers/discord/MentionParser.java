package gg.sep.alyx.core.commands.parsers.discord;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.core.commands.parsers.ParameterParser;

/**
 * Abstract class which is able to parse Discord mention patterns
 * into the ID of the item that it refers to.
 *
 * @param <T> Type of Discord item which can be mentioned and parsed.
 */
public abstract class MentionParser<T> implements ParameterParser<T> {
    private final Pattern mentionPattern;
    private final int idGroupNum;

    protected MentionParser(final Pattern mentionPattern, final int idGroupNum) {
        this.mentionPattern = mentionPattern;
        this.idGroupNum = idGroupNum;
    }

    protected String getMentionId(final String mentionValue) {
        final Matcher matcher = mentionPattern.matcher(mentionValue);
        if (!matcher.matches() || matcher.groupCount() < idGroupNum) {
            throw new IllegalArgumentException("Mention value does not match expected pattern");
        }
        return matcher.group(idGroupNum);
    }

    protected abstract T getMentionedItem(String value, Event event);

    protected boolean matches(final String mentionValue) {
        final Matcher matcher = mentionPattern.matcher(mentionValue);
        return matcher.matches() && matcher.groupCount() >= idGroupNum;
    }

    protected static Guild getGuild(final Event event) {
        if (event instanceof GenericGuildEvent) {
            return ((GenericGuildEvent) event).getGuild();
        } else if (event instanceof MessageReceivedEvent && ((MessageReceivedEvent) event).isFromGuild()) {
            return ((MessageReceivedEvent) event).getGuild();
        }
        return null;
    }
}

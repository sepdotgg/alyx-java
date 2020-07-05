package gg.sep.alyx.plugin.replies;

import java.awt.Color;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 * EmbedReply is a very basic helper class for modeling custom Discord Embed responses.
 * This can be subclassed to customize its attributes.
 */
@RequiredArgsConstructor
public class EmbedReply {
    private final String msg;
    private final Color color;
    private final String emoji;

    /**
     * Formats the output message. By default, this will prefix the message with the emoji.
     *
     * @return The message which will, by default, be placed in the Discord embed Description.
     */
    public String buildMessage() {
        final String prefix = (emoji == null) ? "" : emoji + " ";
        return prefix  + msg;
    }

    /**
     * Compiles all of the classes values into a Discord {@link MessageEmbed} object.
     * @return A {@link MessageEmbed} embed object, which can be sent to a channel.
     */
    public MessageEmbed build() {
        final EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(color);
        builder.setDescription(buildMessage());
        return builder.build();
    }

    /**
     * Builds the embed and sends it as a message to the given {@link User}.
     *
     * @param user The Discord user who should be sent the built {@link MessageEmbed}.
     */
    public void send(final User user) {
        user.openPrivateChannel()
            .queue(this::send);
    }

    /**
     * Builds the embed and sends it as a message to the given {@link MessageChannel}.
     *
     * @param channel The Discord channel which should be sent the built {@link MessageEmbed}.
     */
    public void send(final MessageChannel channel) {
        channel.sendMessage(build()).queue();
    }
}

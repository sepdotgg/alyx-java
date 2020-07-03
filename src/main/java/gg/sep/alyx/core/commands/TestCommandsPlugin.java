package gg.sep.alyx.core.commands;

import static gg.sep.alyx.core.commands.CommandConstants.ALYX_PLUGIN_SERIAL;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import gg.sep.alyx.Alyx;

/**
 * Temporary class which holds test commands for Alyx.
 * TODO: Remove this.
 */
public class TestCommandsPlugin extends AlyxPlugin {
    private static final String NAME = "TestCommands";

    /**
     * Initialize the plugin.
     * @param alyx Instance of Alyx.
     */
    public TestCommandsPlugin(final Alyx alyx) {
        super(NAME, ALYX_PLUGIN_SERIAL, alyx);
    }

    /**
     * Ping!
     * @param event Ping message event.
     */
    @Command(name = "ping")
    public void ping(final MessageReceivedEvent event) {
        event.getChannel().sendMessage("Pong!")
            .queue();
    }

    /**
     * Shutdown the bot.
     * @param event Shutdown message event.
     */
    @Command(name = "shutdown", aliases = {"quit", "exit"})
    public void shutdown(final MessageReceivedEvent event) {
        event.getChannel().sendMessage("Bye! :wave:").queue(message -> {
            alyx.shutdown();
        });
    }
}

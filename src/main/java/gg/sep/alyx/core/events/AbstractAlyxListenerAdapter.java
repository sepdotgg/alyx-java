package gg.sep.alyx.core.events;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * An abstract listener adapter for Alyx.
 *
 * Implementing classes should first check {@link #isListening()} before
 * performing any actions on any events which are fired.
 */
public abstract class AbstractAlyxListenerAdapter extends ListenerAdapter {

    @Getter @Setter
    private boolean listening = true;
}

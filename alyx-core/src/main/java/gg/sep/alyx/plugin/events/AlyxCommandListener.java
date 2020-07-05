package gg.sep.alyx.plugin.events;

import gg.sep.alyx.plugin.Alyx;

/**
 * The primary bot command listener for Alyx and JDA.
 */
public class AlyxCommandListener extends AbstractAlyxCommandListener {

    /**
     * Creates new listener for the provided {@link Alyx} instance.
     * @param alyx The instance of Alyx which will use this listener.
     */
    public AlyxCommandListener(final Alyx alyx) {
        super(alyx);
    }
}

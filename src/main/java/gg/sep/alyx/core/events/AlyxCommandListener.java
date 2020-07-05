package gg.sep.alyx.core.events;

import gg.sep.alyx.AlyxBot;

/**
 * The primary bot command listener for Alyx and JDA.
 */
public class AlyxCommandListener extends AbstractAlyxCommandListener {

    /**
     * Creates new listener for the provided {@link AlyxBot} instance.
     * @param alyx The instance of Alyx which will use this listener.
     */
    public AlyxCommandListener(final AlyxBot alyx) {
        super(alyx);
    }
}

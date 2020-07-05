package gg.sep.alyx.core.commands;

import gg.sep.alyx.AlyxBot;

/**
 * An abstract class for an {@link AlyxPlugin} which does not need to store
 * any state or configuration to the bot's database.
 */
public abstract class StatelessAlyxPlugin extends AlyxPlugin<NoOpPluginData> {

    private static final NoOpPluginData NO_OP_DATA = new NoOpPluginData();

    protected StatelessAlyxPlugin(final String name, final long serial, final AlyxBot alyx) {
        this(name, serial, false, alyx);
    }

    protected StatelessAlyxPlugin(final String name, final long serial, final boolean isGuarded, final AlyxBot alyx) {
        super(name, serial, isGuarded, alyx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<NoOpPluginData> storageDataType() {
        return NoOpPluginData.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NoOpPluginData getPluginData() {
        return NO_OP_DATA;
    }

    @Override
    protected NoOpPluginData freshPluginData() {
        return NO_OP_DATA;
    }
}

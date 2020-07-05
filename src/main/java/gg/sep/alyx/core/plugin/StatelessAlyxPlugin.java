package gg.sep.alyx.core.plugin;

import gg.sep.alyx.core.Alyx;

/**
 * An abstract class for an {@link AlyxPlugin} which does not need to store
 * any state or configuration to the bot's database.
 */
public abstract class StatelessAlyxPlugin extends AlyxPlugin<NoOpPluginData> {

    private static final NoOpPluginData NO_OP_DATA = new NoOpPluginData();

    protected StatelessAlyxPlugin(final String name, final long serial, final Alyx alyx) {
        this(name, serial, false, alyx);
    }

    protected StatelessAlyxPlugin(final String name, final long serial, final boolean isGuarded, final Alyx alyx) {
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

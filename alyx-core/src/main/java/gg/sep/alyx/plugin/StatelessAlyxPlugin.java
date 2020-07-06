package gg.sep.alyx.plugin;

/**
 * An abstract class for an {@link AlyxPlugin} which does not need to store
 * any state or configuration to the bot's database.
 */
public abstract class StatelessAlyxPlugin extends AbstractAlyxPlugin<NoOpPluginData> {

    private static final NoOpPluginData NO_OP_DATA = new NoOpPluginData();

    protected StatelessAlyxPlugin(final String name, final long serial) {
        this(name, serial, false);
    }

    protected StatelessAlyxPlugin(final String name, final long serial, final boolean isGuarded) {
        super(name, serial, isGuarded);
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

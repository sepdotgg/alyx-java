package gg.sep.alyx;

import java.lang.reflect.Constructor;
import java.nio.file.Path;

import lombok.extern.log4j.Log4j2;
import org.pf4j.DefaultExtensionFactory;
import org.pf4j.ExtensionFactory;
import org.pf4j.JarPluginManager;

import gg.sep.alyx.plugin.Alyx;

/**
 * A pf4j {@link JarPluginManager} which handles injecting an instance of
 * {@link Alyx} to the plugin.
 */
@Log4j2
public class AlyxPluginManager extends JarPluginManager {
    private final Alyx alyx;

    /**
     * Create a new instance of the plugin manager.
     * @param alyx The instance of Alyx which will be injected into plugins.
     */
    public AlyxPluginManager(final Alyx alyx) {
        super();
        this.alyx = alyx;
    }

    /**
     * Create a new instance of the plugin manager.
     * @param pluginRoot The path to the directory which holds the plugins.
     * @param alyx The instance of Alyx which will be injected into plugins.
     */
    public AlyxPluginManager(final Alyx alyx, final Path pluginRoot) {
        super(pluginRoot);
        this.alyx = alyx;
    }

    /**
     * Creates a new {@link ExtensionFactory} which will inject {@link Alyx} into
     * the plugin's constructor when the plugin is loaded.
     * @return Extension Factory for the plugin manager.
     */
    @Override
    protected ExtensionFactory createExtensionFactory() {
        return new ExtensionFactory() {
            @Override
            public <T> T create(final Class<T> extensionClass) {
                try {
                    final Constructor<T> constructor = extensionClass.getDeclaredConstructor(Alyx.class);
                    return constructor.newInstance(alyx);
                } catch (final ReflectiveOperationException e) {
                    log.error("Could not find a valid constructor in " + extensionClass);
                    return new DefaultExtensionFactory().create(extensionClass);
                }
            }
        };
    }
}

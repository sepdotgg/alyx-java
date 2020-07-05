package gg.sep.alyx.core.plugin;

import gg.sep.alyx.core.plugin.storage.AbstractJsonObject;
import gg.sep.alyx.core.plugin.storage.JsonSerializable;
import gg.sep.alyx.core.plugin.storage.AlyxStorageEngine;

/**
 * A special {@link JsonSerializable} for use in {@link StatelessAlyxPlugin}s as their config type.
 * This is handled by {@link AlyxStorageEngine} to not read or write.
 */
public class NoOpPluginData extends AbstractJsonObject implements JsonSerializable { }

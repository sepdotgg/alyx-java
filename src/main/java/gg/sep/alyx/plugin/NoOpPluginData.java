package gg.sep.alyx.plugin;

import gg.sep.alyx.model.AbstractJsonObject;
import gg.sep.alyx.model.JsonSerializable;
import gg.sep.alyx.plugin.storage.AlyxStorageEngine;

/**
 * A special {@link JsonSerializable} for use in {@link StatelessAlyxPlugin}s as their config type.
 * This is handled by {@link AlyxStorageEngine} to not read or write.
 */
public class NoOpPluginData extends AbstractJsonObject implements JsonSerializable { }

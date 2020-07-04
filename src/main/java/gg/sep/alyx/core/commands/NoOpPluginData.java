package gg.sep.alyx.core.commands;

import gg.sep.alyx.model.AbstractJsonObject;
import gg.sep.alyx.model.JsonSerializable;

/**
 * A special {@link JsonSerializable} for use in {@link StatelessAlyxPlugin}s as their config type.
 * This is handled by {@link gg.sep.alyx.core.storage.AlyxStorageEngine} to not read or write.
 */
public class NoOpPluginData extends AbstractJsonObject implements JsonSerializable { }

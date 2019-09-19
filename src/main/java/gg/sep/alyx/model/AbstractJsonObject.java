package gg.sep.alyx.model;

import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gg.sep.alyx.model.serializer.PathDeserializerAdapter;
import gg.sep.alyx.model.serializer.PathSerializerAdapter;


/**
 * A base class for representing Json-serializable models used throughout Alyx.
 *
 * Implements all of the necessary type adapters for subclasses, and implements
 * the {@link JsonSerializable#toJson()} and {@link JsonSerializable#toPrettyJson()}
 * methods at a basic level.
 */
public abstract class AbstractJsonObject implements JsonSerializable {

    private static GsonBuilder baseGsonBuilder() {
        return new GsonBuilder()
            .registerTypeAdapter(Path.class, new PathSerializerAdapter())
            .registerTypeAdapter(Path.class, new PathDeserializerAdapter());
    }

    /**
     * Builds a Gson object which, when used to parse an object o JSON, will be pretty-formatted.
     * @return A Gson object which, when used to parse an object o JSON, will be pretty-formatted.
     */
    public static Gson buildPrettyGson() {
        return baseGsonBuilder()
            .setPrettyPrinting()
            .create();
    }

    /**
     * Builds a basic Gson object, suitable for use in subclasses of {@link AbstractJsonObject}.
     * @return A basic Gson object, suitable for use in subclasses of {@link AbstractJsonObject}.
     */
    public static Gson buildGson() {
        return baseGsonBuilder()
            .create();
    }

    /**
     * {@inheritDoc}
     */
    public String toJson() {
        return buildGson()
            .toJson(this);
    }

    /**
     * {@inheritDoc}
     */
    public String toPrettyJson() {
        return buildPrettyGson()
            .toJson(this);
    }
}

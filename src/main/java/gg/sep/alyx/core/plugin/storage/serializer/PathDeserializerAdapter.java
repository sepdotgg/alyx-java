package gg.sep.alyx.core.plugin.storage.serializer;

import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

/**
 * {@link com.google.gson.Gson} deserializer for {@link Path} types.
 */
public class PathDeserializerAdapter implements JsonDeserializer<Path> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Path deserialize(final JsonElement element, final Type type, final JsonDeserializationContext ctx) {
        return Path.of(element.getAsJsonPrimitive().getAsString());
    }
}

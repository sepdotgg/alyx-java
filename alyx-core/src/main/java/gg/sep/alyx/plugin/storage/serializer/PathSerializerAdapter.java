package gg.sep.alyx.plugin.storage.serializer;

import java.lang.reflect.Type;
import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * {@link com.google.gson.Gson} serializer for {@link Path} types.
 */
public class PathSerializerAdapter implements JsonSerializer<Path> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement serialize(final Path path, final Type type, final JsonSerializationContext ctx) {
        return new JsonPrimitive(path.toAbsolutePath().toString());
    }
}

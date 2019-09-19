package gg.sep.alyx.util;

import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import gg.sep.alyx.model.AbstractJsonObject;

/**
 * Utility for parsing JSON strings into one of our model classes, and handles JSON exceptions.
 */
@Log4j2
@UtilityClass
public class ModelParser {

    private static final Gson GSON = AbstractJsonObject.buildGson();

    /**
     * Attempts to parse the specified JSON string into the specified model class.
     *
     * If parsing was successful, will return an Optional of the class, otherwise empty.
     *
     * @param json String contents of the JSON/json file.
     * @param clazz Class of the model which the JSON is parsed into.
     * @param <T> Type of the model which the JSON is parsed into.
     * @return If parsing was successful, returns an Optional of the model's class, otherwise empty.
     */
    public static <T> Optional<T> parseJson(final String json, final Class<T> clazz) {
        try {
            return Optional.ofNullable(GSON.fromJson(json, clazz));
        } catch (final JsonSyntaxException e) {
            log.error("Error parsing JSON into models. class={}, error={}", clazz, e);
            return Optional.empty();
        }
    }
}

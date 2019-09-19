package gg.sep.alyx.model;

/**
 * Describes a class which can be serialized to (and from) a JSON string.
 *
 * By default, many of the types present on the object may not be serializable on their own,
 * and will require a custom type adapter.
 *
 * The implementing class should ensure that whatever {@link com.google.gson.Gson} object is
 * used to serialize/deserialize has the necessary type adapters.
 */
public interface JsonSerializable {

    /**
     * Serializes the object into a JSON string.
     * @return A JSON string which is a serialized representation of the object's state.
     */
    String toJson();

    /**
     * Serialized the object into a pretty JSON string with formatting, idea for output to a file.
     * @return A formatted JSON string which is a serialized representation of the object's state.
     */
    String toPrettyJson();
}

package org.geyser.extension.packetlogger.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.MetadataType;
import org.geysermc.mcprotocollib.protocol.data.game.entity.metadata.MetadataTypes;

import java.lang.reflect.Type;

/**
 * A Gson adapter for converting {@link MetadataType} objects.
 */
public class MetadataTypeSerializer<T> implements JsonSerializer<MetadataType<T>> {

    @Override
    public JsonElement serialize(MetadataType<T> src, Type typeOfSrc, JsonSerializationContext context) {
        // Find property in MetadataTypes and serialize as its name, otherwise fallback to toString()
         for (var field : MetadataTypes.class.getDeclaredFields()) {
             if (MetadataType.class.isAssignableFrom(field.getType()) && field.canAccess(null)) {
                 try {
                     if (field.get(null) == src) {
                         return new JsonPrimitive(field.getName());
                     }
                 } catch (IllegalAccessException ignored) {}
             }
         }

        return new JsonPrimitive(src.toString());
    }
}

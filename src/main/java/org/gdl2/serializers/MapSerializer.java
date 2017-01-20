package org.gdl2.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class MapSerializer implements JsonSerializer<Map<?, ?>> {
    @Override
    public JsonElement serialize(Map<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty()) {
            return null;
        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<?, ?> entry : src.entrySet()) {
            jsonObject.add(entry.getKey().toString(), context.serialize(entry.getValue()));
        }
        return jsonObject;
    }
}

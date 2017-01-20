package org.gdl2.serializers;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListSerializer implements JsonSerializer<List<?>> {
    @Override
    public JsonElement serialize(List<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty()) {
            return null;
        }
        JsonArray array = new JsonArray();
        for (Object child : src) {
            JsonElement element = context.serialize(child);
            array.add(element);
        }
        return array;
    }
}
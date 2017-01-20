package org.gdl2.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.gdl2.model.Rule;

import java.lang.reflect.Type;
import java.util.stream.Collectors;

public class RuleSerializer implements JsonSerializer<Rule> {
    public JsonElement serialize(Rule src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.addProperty("id", src.getId());
        object.addProperty("priority", src.getPriority());
        if (!src.getWhen().isEmpty()) {
            object.add("when", context.serialize(src.getWhen().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())));
        }
        if (!src.getThen().isEmpty()) {
            object.add("then", context.serialize(src.getThen().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())));
        }
        return object;
    }
}

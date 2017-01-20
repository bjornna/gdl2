package org.gdl2.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.gdl2.model.DataBinding;

import java.lang.reflect.Type;
import java.util.stream.Collectors;

public class DataBindingSerializer implements JsonSerializer<DataBinding> {
    public JsonElement serialize(DataBinding src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("id", context.serialize(src.getId()));
        object.add("model_id", context.serialize(src.getModelId()));
        object.add("elements", context.serialize(src.getElements()));
        if (src.getPredicates() != null) {
            object.add("predicates", context.serialize(src.getPredicates().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList())));
        }
        return object;
    }
}
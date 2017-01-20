package org.gdl2.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.gdl2.expression.ExpressionItem;
import org.gdl2.model.GuideDefinition;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

public class GuideDefinitionSerializer implements JsonSerializer<GuideDefinition> {
    public JsonElement serialize(GuideDefinition src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        if (!src.getPreConditions().isEmpty()) {
            object.add("pre_conditions", context.serialize(toStringList(src.getPreConditions())));
        }
        if (!src.getDefaultActions().isEmpty()) {
            object.add("default_actions", context.serialize(toStringList(src.getDefaultActions())));
        }
        if (!src.getDataBindings().isEmpty()) {
            object.add("data_bindings", context.serialize(src.getDataBindings()));
        }
        if (!src.getRules().isEmpty()) {
            object.add("rules", context.serialize(src.getRules()));
        }
        return object;
    }

    private List<String> toStringList(List<ExpressionItem> expressionItems) {
        return expressionItems.stream()
                .map(ExpressionItem::toString)
                .collect(Collectors.toList());
    }
}
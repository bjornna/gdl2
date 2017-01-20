package org.gdl2.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.gdl2.datatypes.CodePhrase;

import java.lang.reflect.Type;

public class CodePhraseSerializer implements JsonSerializer<CodePhrase> {
    public JsonElement serialize(CodePhrase src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }
}
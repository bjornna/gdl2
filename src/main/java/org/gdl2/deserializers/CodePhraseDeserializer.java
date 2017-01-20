package org.gdl2.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.gdl2.datatypes.CodePhrase;

import java.lang.reflect.Type;

public class CodePhraseDeserializer implements JsonDeserializer<CodePhrase> {
    public CodePhrase deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return CodePhrase.valueOf(json.getAsJsonPrimitive().getAsString());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new JsonParseException(exception);
        }
    }
}

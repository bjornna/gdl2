package org.gdl2.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.gdl2.expression.ExpressionItem;
import org.gdl2.expression.parser.ExpressionParser;

import java.io.StringReader;
import java.lang.reflect.Type;

public class ExpressionItemDeserializer implements JsonDeserializer<ExpressionItem> {
    public ExpressionItem deserialize(JsonElement json, Type type,
                                      JsonDeserializationContext context)
            throws JsonParseException {
        try {
            return parse(json.getAsJsonPrimitive().getAsString());
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new JsonParseException(exception);
        }
    }

    ExpressionItem parse(String value) throws Exception {
        ExpressionParser parser = new ExpressionParser(new StringReader(value));
        return parser.parse();
    }
}
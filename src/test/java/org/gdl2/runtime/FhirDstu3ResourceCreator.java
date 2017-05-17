package org.gdl2.runtime;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Map;

/**
 * Sample objectCreatorPlugin implementation that creates fhir dstu3 resources
 */
public class FhirDstu3ResourceCreator implements ObjectCreatorPlugin {

    @Override
    public Object create(String modelId, Map<String, Object> valueMap) throws ClassNotFoundException {
        Gson gson = new GsonBuilder().
                registerTypeAdapter(Double.class, (JsonSerializer<Double>) (src, typeOfSrc, context) -> {
                    if (src == src.longValue())
                        return new JsonPrimitive(src.longValue());
                    return new JsonPrimitive(src);
                }).create();
        String json = gson.toJson(valueMap);
        Class modelClass = Class.forName(modelId);
        IParser fhirContextParser = FhirContext.forDstu3().newJsonParser();
        return fhirContextParser.parseResource(modelClass, json);
    }
}

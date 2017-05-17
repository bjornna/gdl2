package org.gdl2.runtime;

import com.google.gson.Gson;

import java.util.Map;

/**
 * The default object creator that creates simple objects based
 * on classes known to gdl2 library, e.g. built-in data types
 */
public class DefaultObjectCreator implements ObjectCreatorPlugin {
    private Gson gson = new Gson();

    @Override
    public Object create(String modelId, Map<String, Object> values) throws ClassNotFoundException {
        String json = gson.toJson(values);
        Class modelClass = Class.forName(modelId);
        return gson.fromJson(json, modelClass);
    }
}

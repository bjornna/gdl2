package org.gdl2.runtime;

import java.util.Map;

public interface ObjectCreatorPlugin {

    /**
     * A plugin to create object based on given model id and values.
     *
     * @param modelId fully qualified class name of the model to create
     * @param values nested value map used to create the object
     * @return the object created
     */
    Object create(String modelId, Map<String, Object> values) throws ClassNotFoundException;
}

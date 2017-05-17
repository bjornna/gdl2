package org.gdl2.model;

import lombok.Value;

import java.util.Map;

@Value
public class Template {
    private String id;
    private String modelId;

    private Map<String, Object> object;
}

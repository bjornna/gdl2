package org.gdl2.terminology;

import lombok.Value;

import java.util.Map;

@Value
public final class TermBinding {
    private String id;
    private Map<String, Binding> bindings;
}
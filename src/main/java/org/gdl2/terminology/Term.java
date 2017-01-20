package org.gdl2.terminology;

import lombok.Value;

@Value
public final class Term {
    private String id;
    private String text;
    private String description;
}
package org.gdl2.terminology;

import lombok.Value;
import org.gdl2.datatypes.CodePhrase;

import java.util.Collections;
import java.util.List;

@Value
public final class Binding {
    private String id;
    private List<CodePhrase> codes;
    private String uri;
}
package org.gdl2.resources;

import lombok.Value;
import org.gdl2.datatypes.CodePhrase;

import java.util.Map;

@Value
public final class TranslationDetails {
    private String id;
    private CodePhrase language;
    private Map<String, String> author;
}
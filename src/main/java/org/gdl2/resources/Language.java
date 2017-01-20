package org.gdl2.resources;

import lombok.Value;
import org.gdl2.datatypes.CodePhrase;

import java.util.Map;

@Value
public final class Language {
    private CodePhrase originalLanguage;
    private Map<String, TranslationDetails> translations;
}
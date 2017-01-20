package org.gdl2.model;

import lombok.Value;
import org.gdl2.terminology.TermBinding;
import org.gdl2.terminology.TermDefinition;

import java.util.Map;

@Value
public final class GuideOntology {
    private Map<String, TermDefinition> termDefinitions;
    private Map<String, TermBinding> termBindings;
}
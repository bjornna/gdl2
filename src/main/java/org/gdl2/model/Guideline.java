package org.gdl2.model;

import lombok.Value;
import org.gdl2.resources.Language;
import org.gdl2.resources.ResourceDescription;

/**
 * Top level object representing a CDS guideline.
 */
@Value
public final class Guideline {
    private String id;
    private String gdlVersion;
    private String concept;
    private Language language;
    private ResourceDescription description;
    private GuideDefinition definition;
    private GuideOntology ontology;
}

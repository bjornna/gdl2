package org.gdl2.resources;

import lombok.Value;
import org.gdl2.datatypes.CodePhrase;

import java.util.List;

@Value
public final class ResourceDescriptionItem {
    private String id;
    private CodePhrase language;
    private String purpose;
    private List<String> keywords;
    private String use;
    private String misuse;
    private String copyright;
}
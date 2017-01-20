package org.gdl2.resources;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public final class ResourceDescription {
    private Map<String, String> originalAuthor;
    private List<String> otherContributors;
    private String lifecycleState;
    private String resourcePackageUri;
    private Map<String, ResourceDescriptionItem> details;
    private Map<String, String> otherDetails;
}
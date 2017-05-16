package org.gdl2.model;

import lombok.Value;
import org.gdl2.expression.ExpressionItem;

import java.util.List;
import java.util.Map;

@Value
public final class GuideDefinition {
    private Map<String, DataBinding> dataBindings;
    private Map<String, Template> templates;
    private List<ExpressionItem> preConditions;
    private List<ExpressionItem> defaultActions;
    private Map<String, Rule> rules;
}
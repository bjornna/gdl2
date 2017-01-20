package org.gdl2.model;

import lombok.Value;
import org.gdl2.expression.ExpressionItem;

import java.util.List;

@Value
public final class Rule {
    private String id;
    private List<ExpressionItem> when;
    private List<ExpressionItem> then;
    private int priority;
}
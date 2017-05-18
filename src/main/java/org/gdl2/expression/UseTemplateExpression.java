package org.gdl2.expression;

import lombok.Value;

import java.util.List;

@Value
public class UseTemplateExpression extends ExpressionItem {
    private Variable variable;
    private List<AssignmentExpression> assignmentExpressions;
}

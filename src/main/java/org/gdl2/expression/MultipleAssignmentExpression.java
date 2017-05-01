package org.gdl2.expression;

import lombok.Value;

import java.util.List;

@Value
public class MultipleAssignmentExpression extends ExpressionItem {
    private List<AssignmentExpression> assignmentExpressions;
}
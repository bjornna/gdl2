package org.gdl2.expression;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public final class CreateInstanceExpression extends AssignmentExpression {
    public static String FUNCTION_CREATE_NAME = "create";

    public CreateInstanceExpression(Variable variable, List<AssignmentExpression> assignmentExpressions) {
        super(variable, new MultipleAssignmentExpression(assignmentExpressions));
    }

    public List<AssignmentExpression> getAssignmentExpressions() {
        return ((MultipleAssignmentExpression) getAssignment()).getAssignmentExpressions();
    }
}
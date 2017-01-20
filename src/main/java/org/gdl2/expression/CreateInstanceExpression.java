package org.gdl2.expression;

import java.util.List;

public final class CreateInstanceExpression extends AssignmentExpression {
    public static String FUNCTION_CREATE_NAME = "create";

    public CreateInstanceExpression(Variable variable, List<AssignmentExpression> assignmentExpressions) {
        super(variable, new MultipleAssignmentExpression(assignmentExpressions));
    }

    public String toString() {
        return String.valueOf(getVariable()) + getAssigment();
    }

    public MultipleAssignmentExpression getAssigment() {
        return (MultipleAssignmentExpression) this.getAssignment();
    }


}
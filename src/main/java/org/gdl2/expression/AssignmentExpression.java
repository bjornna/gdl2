package org.gdl2.expression;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
public class AssignmentExpression extends ExpressionItem {
    private Variable variable;
    private ExpressionItem assignment;

    public AssignmentExpression(Variable variable, ExpressionItem assignment) {
        this.variable = variable;
        this.assignment = assignment;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(variable);
        buf.append("=");
        boolean isBinaryExpression = assignment instanceof BinaryExpression;
        if (isBinaryExpression) {
            buf.append("(");
        }
        buf.append(assignment);
        if (isBinaryExpression) {
            buf.append(")");
        }
        return buf.toString();
    }

    public Variable getVariable() {
        return variable;
    }

    public ExpressionItem getAssignment() {
        return assignment;
    }
}
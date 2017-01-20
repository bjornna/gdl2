package org.gdl2.expression;

import java.util.Objects;

public class AssignmentExpression extends ExpressionItem {
    private Variable variable;
    private ExpressionItem assignment;

    public AssignmentExpression(Variable variable, ExpressionItem assignment) {
        super();
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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AssignmentExpression that = (AssignmentExpression) other;
        return Objects.equals(variable, that.variable)
                && Objects.equals(assignment, that.assignment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, assignment);
    }
}
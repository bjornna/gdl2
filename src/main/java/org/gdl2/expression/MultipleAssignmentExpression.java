package org.gdl2.expression;

import java.util.List;
import java.util.Objects;

public class MultipleAssignmentExpression extends ExpressionItem {
    private List<AssignmentExpression> assignmentExpressions;

    MultipleAssignmentExpression(List<AssignmentExpression> assignmentExpressions) {
        this.assignmentExpressions = assignmentExpressions;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        MultipleAssignmentExpression that = (MultipleAssignmentExpression) other;
        return Objects.equals(assignmentExpressions, that.assignmentExpressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignmentExpressions);
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        if (assignmentExpressions != null) {
            String prefix = "";
            for (AssignmentExpression assignmentExpression : assignmentExpressions) {
                buf.append(prefix);
                buf.append(assignmentExpression);
                prefix = ";";
            }
        }
        buf.append(")");
        return buf.toString();
    }

    public List<AssignmentExpression> getAssignmentExpressions() {
        return assignmentExpressions;
    }
}
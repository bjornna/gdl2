package org.gdl2.expression;

import java.util.Objects;

public class UnaryExpression extends ExpressionItem {
    private ExpressionItem operand;
    private OperatorKind operator;

    public static UnaryExpression create(ExpressionItem operand,
                                         OperatorKind operator) {
        return new UnaryExpression(operand, operator);
    }

    public UnaryExpression(ExpressionItem operand, OperatorKind operator) {
        super();
        this.operand = operand;
        this.operator = operator;
    }

    public ExpressionItem getOperand() {
        return operand;
    }

    public OperatorKind getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        return operator.getSymbol() + "(" + operand + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        UnaryExpression that = (UnaryExpression) other;
        return Objects.equals(operand, that.operand)
                && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand, operator);
    }
}
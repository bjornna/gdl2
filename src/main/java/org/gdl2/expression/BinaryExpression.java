package org.gdl2.expression;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public final class BinaryExpression extends ExpressionItem {
    private ExpressionItem left;
    private ExpressionItem right;
    private OperatorKind operator;

    public static BinaryExpression create(ExpressionItem left, ExpressionItem right,
                                          OperatorKind operator) {
        return new BinaryExpression(left, right, operator);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (left instanceof BinaryExpression || left instanceof UnaryExpression) {
            buf.append("(");
            buf.append(left.toString());
            buf.append(")");
        } else {
            if (left != null) {
                buf.append(left.toString());
            } else {
                throw new IllegalStateException("Left item == null");
            }
        }

        if (OperatorKind.IS_A == operator) {
            buf.append(" ");
        }
        if (operator != null) {
            buf.append(operator.getSymbol());
        } else {
            throw new IllegalStateException("Operator == null");
        }
        if (OperatorKind.IS_A == operator) {
            buf.append(" ");
        }

        if (right instanceof BinaryExpression || right instanceof UnaryExpression) {
            buf.append("(");
            buf.append(right.toString());
            buf.append(")");
        } else {
            if (right != null) {
                buf.append(right.toString());
            } else {
                throw new IllegalStateException("Right item == null");
            }
        }
        return buf.toString();
    }
}
package org.gdl2.expression;

import lombok.Value;

@Value
public class MaxExpression extends ExpressionItem {
    private ExpressionItem operand;
    private int position;

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("max(");
        buf.append(operand);
        buf.append(";");
        buf.append(position);
        buf.append(")");
        return buf.toString();
    }
}

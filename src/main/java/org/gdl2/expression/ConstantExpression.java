package org.gdl2.expression;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper = false)
@Getter
public class ConstantExpression extends ExpressionItem {
    private String value;

    public ConstantExpression(String value) {
        this.value = value;
    }

    public static ConstantExpression create(String value) {
        return new ConstantExpression(value);
    }

    public String toString() {
        return value;
    }
}
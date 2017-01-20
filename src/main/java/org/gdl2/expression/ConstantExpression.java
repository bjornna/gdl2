package org.gdl2.expression;

import java.util.Objects;

public class ConstantExpression extends ExpressionItem {
    private String value;

    public static ConstantExpression create(String value) {
        return new ConstantExpression(value);
    }

    public ConstantExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ConstantExpression that = (ConstantExpression) other;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
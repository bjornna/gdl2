package org.gdl2.expression;

public class DateTimeConstant extends ConstantExpression {
    public DateTimeConstant(String date) {
        super(date);
    }

    public String toString() {
        return "(" + getValue() + ")";
    }
}
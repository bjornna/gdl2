package org.gdl2.expression;

import java.util.Objects;

public class StringConstant extends ConstantExpression {
    private String string;

    public StringConstant(String string) {
        super(string);
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public String toString() {
        return "'" + string + "'";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!super.equals(other)) {
            return false;
        }
        StringConstant that = (StringConstant) other;
        return Objects.equals(string, that.string);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), string);
    }
}
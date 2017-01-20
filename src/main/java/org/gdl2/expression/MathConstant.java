package org.gdl2.expression;

import java.util.Objects;

public class MathConstant extends ConstantExpression {
    private Constant constant;

    MathConstant(Constant constant) {
        super(constant.toString());
        this.constant = constant;
    }

    public static MathConstant create(Constant constant) {
        if (constant == null) {
            throw new IllegalArgumentException("null constant");
        }
        return new MathConstant(constant);
    }

    public Constant getConstant() {
        return constant;
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
        MathConstant that = (MathConstant) other;
        return constant == that.constant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), constant);
    }
}

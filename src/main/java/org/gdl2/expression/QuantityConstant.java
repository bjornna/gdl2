package org.gdl2.expression;

import org.gdl2.datatypes.DvQuantity;

import java.util.Objects;

public class QuantityConstant extends ConstantExpression {
    private DvQuantity quantity;

    public QuantityConstant(DvQuantity quantity) {
        super(quantity.toString());
        this.quantity = quantity;
    }

    public DvQuantity getQuantity() {
        return this.quantity;
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
        QuantityConstant that = (QuantityConstant) other;
        return Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), quantity);
    }
}
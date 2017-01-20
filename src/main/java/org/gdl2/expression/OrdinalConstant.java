package org.gdl2.expression;

import org.gdl2.datatypes.CodePhrase;
import org.gdl2.datatypes.DvCodedText;
import org.gdl2.datatypes.DvOrdinal;

import java.util.Objects;

public class OrdinalConstant extends ConstantExpression {
    private DvOrdinal ordinal;

    public OrdinalConstant(int order, String value, CodePhrase code) {
        super(order + "|" + code.toString() + "|" + value + "|");
        this.ordinal = new DvOrdinal(order, new DvCodedText(value, code));
    }

    public OrdinalConstant(DvOrdinal ordinal) {
        super(ordinal.toString());
        this.ordinal = ordinal;
    }

    public DvOrdinal getOrdinal() {
        return this.ordinal;
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
        OrdinalConstant that = (OrdinalConstant) other;
        return Objects.equals(ordinal, that.ordinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ordinal);
    }
}
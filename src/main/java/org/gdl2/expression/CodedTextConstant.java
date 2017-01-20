package org.gdl2.expression;

import org.gdl2.datatypes.CodePhrase;
import org.gdl2.datatypes.DvCodedText;

import java.util.Objects;

public final class CodedTextConstant extends ConstantExpression {
    private DvCodedText codedText;

    public CodedTextConstant(String value, CodePhrase code) {
        super(code.toString() + "|" + value + "|");
        this.codedText = new DvCodedText(value, code);
    }

    public DvCodedText getCodedText() {
        return this.codedText;
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
        CodedTextConstant that = (CodedTextConstant) other;
        return Objects.equals(codedText, that.codedText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), codedText);
    }
}
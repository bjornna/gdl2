package org.gdl2.expression;

import org.gdl2.datatypes.CodePhrase;

import java.util.Objects;

public final class CodePhraseConstant extends ConstantExpression {
    private CodePhrase codePhrase;

    public CodePhraseConstant(CodePhrase code) {
        super(code.toString());
        this.codePhrase = code;
    }

    public CodePhrase getCodePhrase() {
        return this.codePhrase;
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
        CodePhraseConstant that = (CodePhraseConstant) other;
        return Objects.equals(codePhrase, that.codePhrase);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), codePhrase);
    }
}
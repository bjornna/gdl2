package org.gdl2.datatypes;

import lombok.Value;

@Value
public final class DvCodedText extends DataValue {
    private String value;
    private CodePhrase code;

    public DvCodedText(String value, CodePhrase code) {
        this.value = value;
        this.code = code;
    }

    public DvCodedText(String value, String terminology, String code) {
        this(value, new CodePhrase(terminology, code));
    }

    public static DvCodedText valueOf(String value) {
        String[] tokens = value.split("::");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("failed to parse DvCodedText '" + value + "', wrong number of tokens.");
        }
        String[] tokens2 = tokens[1].split("\\|");
        if (tokens2.length != 2) {
            throw new IllegalArgumentException("failed to parse DvCodedText '" + value + "', wrong number of tokens.");
        }
        return new DvCodedText(tokens2[1], new CodePhrase(tokens[0], tokens2[0]));
    }

    public CodePhrase getDefiningCode() {
        return code;
    }

    @Override
    public String toString() {
        return code.toString() + "|" + value + "|";
    }
}
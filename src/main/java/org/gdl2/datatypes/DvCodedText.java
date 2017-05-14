package org.gdl2.datatypes;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@NoArgsConstructor(force = true)
public final class DvCodedText {
    private String value;
    private CodePhrase definingCode;

    public DvCodedText(String value, CodePhrase definingCode) {
        this.value = value;
        this.definingCode = definingCode;
    }

    public DvCodedText(String value, String terminology, String definingCode) {
        this(value, new CodePhrase(terminology, definingCode));
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

    @Override
    public String toString() {
        return definingCode.toString() + "|" + value + "|";
    }
}
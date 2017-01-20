package org.gdl2.datatypes;

import lombok.Value;

@Value
public final class CodePhrase extends DataValue {
    private String terminology;
    private String code;

    public CodePhrase(String terminology, String code) {
        this.terminology = terminology;
        this.code = code;
    }

    public static CodePhrase valueOf(String value) {
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        int index = value.indexOf("::");
        if (index <= 0 || index >= value.length() - 1) {
            throw new IllegalArgumentException("wrong format of code phrase");
        }

        String terminology = value.substring(0, index);
        String code = value.substring(index + 2);
        return new CodePhrase(terminology, code);
    }

    @Override
    public String toString() {
        return terminology + "::" + code;
    }
}
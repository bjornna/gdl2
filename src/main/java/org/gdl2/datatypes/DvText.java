package org.gdl2.datatypes;

import lombok.Value;

@Value
public final class DvText extends DataValue {
    private String value;

    public DvText(String value) {
        this.value = value;
    }

    public static DvText valueOf(String value) {
        return new DvText(value);
    }
}
package org.gdl2.datatypes;

import lombok.Value;

@Value
public final class DvBoolean extends DataValue {
    private static final DvBoolean TRUE = new DvBoolean(true);
    private static final DvBoolean FALSE = new DvBoolean(false);
    private boolean value;

    private DvBoolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    public static DvBoolean valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static DvBoolean valueOf(String value) {
        return Boolean.TRUE.toString().equalsIgnoreCase(value) ? TRUE : FALSE;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
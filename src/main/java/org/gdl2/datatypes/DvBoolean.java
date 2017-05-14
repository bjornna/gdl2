package org.gdl2.datatypes;

import lombok.Value;

@Value
public final class DvBoolean {
    public static final DvBoolean TRUE = new DvBoolean(true);
    public static final DvBoolean FALSE = new DvBoolean(false);
    private boolean value;

    private DvBoolean(boolean value) {
        this.value = value;
    }

    public static DvBoolean valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    public static DvBoolean valueOf(String value) {
        return Boolean.TRUE.toString().equalsIgnoreCase(value) ? TRUE : FALSE;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
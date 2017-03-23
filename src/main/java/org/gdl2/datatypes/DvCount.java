package org.gdl2.datatypes;

import lombok.Value;

@Value
public final class DvCount extends DataValue {
    private int magnitude;

    public DvCount(int magnitude) {
        this.magnitude = magnitude;
    }

    public static DvCount valueOf(String value) throws IllegalArgumentException {
        try {
            int intValue = Integer.parseInt(value);
            return new DvCount(intValue);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Wrong number format", nfe);
        }
    }

    public static DvCount valueOf(int value) {
        return new DvCount(value);
    }

    @Override
    public String toString() {
        return Integer.toString(magnitude);
    }
}

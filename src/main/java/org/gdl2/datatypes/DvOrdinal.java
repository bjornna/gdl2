package org.gdl2.datatypes;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class DvOrdinal {
    private int value;
    private DvCodedText symbol;

    public DvOrdinal(int value, DvCodedText symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    public DvOrdinal(int value, String label, String terminology, String code) {
        this(value, new DvCodedText(label, terminology, code));
    }

    public static DvOrdinal valueOf(String value) {
        int index = value.indexOf("|");
        if (index < 0) {
            throw new IllegalArgumentException("failed to parse DvOrdinal '" + value + "', wrong number of tokens.");
        }
        int ordinalValue;
        try {
            ordinalValue = Integer.parseInt(value.substring(0, index));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("failed to parse DvOrdinal '" + value + "', invalid integer value.");
        }
        String str = value.substring(index + 1);
        return new DvOrdinal(ordinalValue, DvCodedText.valueOf(str));
    }

    public String getTerminologyId() {
        return this.symbol.getDefiningCode().getTerminology();
    }

    @Override
    public String toString() {
        return value + "|" + symbol.toString();
    }
}
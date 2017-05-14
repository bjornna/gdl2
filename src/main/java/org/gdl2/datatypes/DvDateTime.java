package org.gdl2.datatypes;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public final class DvDateTime {
    private LocalDateTime dateTime;

    private DvDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public DvDateTime() {
        this(LocalDateTime.now());
    }

    public static DvDateTime valueOf(String value) {
        LocalDateTime dateTime = LocalDateTime.parse(value);
        return new DvDateTime(dateTime);
    }

    @Override
    public String toString() {
        return dateTime.toString();
    }
}
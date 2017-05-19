package org.gdl2.datatypes;

import lombok.Value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Value
public final class DvDateTime {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime);
    }
}
package org.gdl2.datatypes;

import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseDvDateTimeValueTest {

    @Test
    public void can_parse_iso_datetime_value_without_timezone() {
        LocalDateTime dateTime = DvDateTime.valueOf("2012-01-10T05:07:15").getDateTime();
        assertThat(dateTime.getYear(), is(2012));
        assertThat(dateTime.getMonthValue(), is(1));
        assertThat(dateTime.getDayOfMonth(), is(10));
        assertThat(dateTime.getHour(), is(5));
        assertThat(dateTime.getMinute(), is(7));
        assertThat(dateTime.getSecond(), is(15));
    }

    @Test(expectedExceptions = DateTimeParseException.class)
    public void can_parse_iso_datetime_value_timezone_expect_parsing_failure() {
        DvDateTime.valueOf("2012-01-10T05:07:15+01:00");
    }

    @Test
    public void can_print_iso_formatted_string() {
        DvDateTime dateTime = DvDateTime.valueOf("2012-01-10T05:07:15");
        assertThat(dateTime.toString(), is("2012-01-10T05:07:15"));
    }
}

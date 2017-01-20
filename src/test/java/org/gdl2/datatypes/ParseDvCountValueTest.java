package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseDvCountValueTest {

    @Test
    public void can_parse_dv_count_value() {
        DvCount count = DvCount.valueOf("5");
        assertThat(count.getMagnitude(), is(5));
    }
}

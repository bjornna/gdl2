package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseDvBooleanValueTest {

    @Test
    public void can_parse_true_value() {
        assertThat(DvBoolean.valueOf("true").getValue(), is(true));
    }

    @Test
    public void can_parse_false_value() {
        assertThat(DvBoolean.valueOf("false").getValue(), is(false));
    }
}

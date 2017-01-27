package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DvOrdinalEqualityTest {

    @Test
    public void two_dv_ordinals_with_same_values_should_be_equal() {
        DvOrdinal dvOrdinal = DvOrdinal.valueOf("2|local::at0018]|M1|");
        DvOrdinal dvOrdinal2 = DvOrdinal.valueOf("2|local::at0018]|M1|");
        assertThat(dvOrdinal.equals(dvOrdinal2), is(true));
        assertThat(dvOrdinal2.equals(dvOrdinal), is(true));
    }
}

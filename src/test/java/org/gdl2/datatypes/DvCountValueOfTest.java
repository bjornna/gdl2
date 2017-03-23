package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DvCountValueOfTest {

    @Test
    public void can_create_dv_count_by_value_of_int() {
        DvCount dvCount = DvCount.valueOf(3);
        assertThat(dvCount.getMagnitude(), is(3));
    }
}

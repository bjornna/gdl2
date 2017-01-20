package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseDvQuantityValueTest {

    @Test
    public void can_parse_dv_quantity() {
        DvQuantity dvQuantity = DvQuantity.valueOf("100.8,kg");
        assertThat(dvQuantity.getMagnitude(), is(100.8));
        assertThat(dvQuantity.getUnits(), is("kg"));
    }
}

package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseDvOrdinalValueTest {

    @Test
    public void can_parse_dv_ordinal_value() {
        DvOrdinal dvOrdinal = DvOrdinal.valueOf("1|terminology::code|label|");
        assertThat(dvOrdinal.getValue(), is(1));
        assertThat(dvOrdinal.getSymbol().getValue(), is("label"));
        assertThat(dvOrdinal.getSymbol().getDefiningCode().getTerminology(), is("terminology"));
        assertThat(dvOrdinal.getSymbol().getDefiningCode().getCode(), is("code"));
    }
}

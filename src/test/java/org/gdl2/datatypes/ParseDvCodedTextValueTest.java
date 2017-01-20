package org.gdl2.datatypes;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseDvCodedTextValueTest {

    @Test
    public void can_parse_dv_coded_text_value() {
        DvCodedText codedText = DvCodedText.valueOf("terminology::code|label|");
        assertThat(codedText.getValue(), is("label"));
        assertThat(codedText.getDefiningCode().getTerminology(), is("terminology"));
        assertThat(codedText.getDefiningCode().getCode(), is("code"));
    }
}

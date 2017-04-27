package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class BuildersTest {

    @Test
    public void can_create_dv_coded_text_using_builder() {
        DvCodedText codedText = DvCodedText.builder()
                .value("Heart failure")
                .definingCode(CodePhrase.builder()
                        .terminology("ICD10")
                        .code("I50").build())
                .build();

        assertThat(codedText.getValue(), is("Heart failure"));
        assertThat(codedText.getDefiningCode().getTerminology(), is("ICD10"));
        assertThat(codedText.getDefiningCode().getCode(), is("I50"));
    }

    @Test
    public void can_create_dv_quantity_using_builder() {
        DvQuantity dvQuantity = DvQuantity.builder()
                .magnitude(120.0)
                .precision(1)
                .units("mm[Hg]")
                .build();
        assertThat(dvQuantity.getMagnitude(), is(120.0));
        assertThat(dvQuantity.getPrecision(), is(1));
        assertThat(dvQuantity.getUnits(), is("mm[Hg]"));
    }

    @Test
    public void can_create_dv_ordinal_using_builder() {
        DvOrdinal dvOrdinal = DvOrdinal.builder()
                .value(1)
                .symbol(DvCodedText.builder()
                        .value("label")
                        .definingCode(CodePhrase.builder()
                                .terminology("local")
                                .code("at0001")
                                .build())
                        .build())
                .build();
        assertThat(dvOrdinal.getValue(), is(1));
        assertThat(dvOrdinal.getSymbol().getValue(), is("label"));
        assertThat(dvOrdinal.getTerminologyId(), is("local"));
        assertThat(dvOrdinal.getSymbol().getDefiningCode().getCode(), is("at0001"));
    }
}

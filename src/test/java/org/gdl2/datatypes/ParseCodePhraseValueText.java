package org.gdl2.datatypes;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ParseCodePhraseValueText {

    @Test
    public void can_parse_code_phrase_value() {
        CodePhrase codePhrase = CodePhrase.valueOf("ICD10::I50");
        assertThat(codePhrase.getTerminology(), is("ICD10"));
        assertThat(codePhrase.getCode(), is("I50"));
    }
}

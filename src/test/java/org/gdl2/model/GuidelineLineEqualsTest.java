package org.gdl2.model;

import org.gdl2.runtime.TestCommon;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GuidelineLineEqualsTest extends TestCommon {

    @Test
    public void equals_should_return_true_on_parsed_guideline() throws Exception {
        Guideline guideline = loadGuideline("BSA_Calculation.v1.gdl2");
        Guideline guideline2 = loadGuideline("BSA_Calculation.v1.gdl2");
        assertThat(guideline.equals(guideline2), is(true));
    }
}

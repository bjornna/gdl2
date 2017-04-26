package org.gdl2.test;

import org.gdl2.model.Guideline;
import org.gdl2.runtime.TestCommon;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GuidelineTestTest extends TestCommon {

    @Test
    public void can_load_guideline_test_cases() throws Exception {
        Guideline guideline = loadGuideline("DAS28-ESR_Calculation.v1.gdl2");
        assertThat(guideline.getTestCases().size(), is(3));
    }
}

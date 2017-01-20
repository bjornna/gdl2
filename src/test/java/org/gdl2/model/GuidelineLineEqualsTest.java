package org.gdl2.model;

import org.apache.commons.io.IOUtils;
import org.gdl2.Gdl2;
import org.gdl2.runtime.TestCommon;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GuidelineLineEqualsTest extends TestCommon {

    @Test
    public void equals_should_return_true_on_parsed_guideline() throws Exception {
        Guideline guideline = Gdl2.fromGdl2(loadJson("BSA_Calculation.v1.gdl2"));
        Guideline guideline2 = Gdl2.fromGdl2(loadJson("BSA_Calculation.v1.gdl2"));
        assertThat(guideline.equals(guideline2), is(true));
    }

    private String loadJson(String name) throws Exception {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream((name + ".json")), "UTF-8");
    }
}

package org.gdl2.runtime;

import org.gdl2.datatypes.DataValue;
import org.gdl2.datatypes.DvQuantity;
import org.gdl2.model.Guideline;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ContinuousAssignmentsTest extends TestCommon {
    private Interpreter interpreter;

    @BeforeMethod
    public void setUp() {
        interpreter = new Interpreter();
    }

    @Test
    public void can_run_continuous_assignments_within_the_same_rule() throws Exception {
        Guideline guideline = loadGuideline("continuous_assignments_test.v1.gdl2");
        Map<String, DataValue> result = interpreter.execute(guideline, new ArrayList<>());
        DataValue dataValue = result.get("gt0013");
        assertThat(dataValue, Matchers.instanceOf(DvQuantity.class));
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), is(2.0));
        assertThat(dvQuantity.getPrecision(), is(1));
    }
}

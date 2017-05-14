package org.gdl2.runtime;

import org.gdl2.datatypes.DvBoolean;
import org.gdl2.model.Guideline;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class FiredRulesTest extends TestCommon {
    private static final String BSA_CALCULATION_FIRED_RULE = "BSA_Calculation_fired_rule.v1.gdl2";
    private Interpreter interpreter;
    private Guideline guideline;

    @BeforeMethod
    public void setUp() {
        interpreter = new Interpreter();
    }

    @Test
    public void can_evaluate_fired_rule_expected_true() throws Exception {
        guideline = loadGuideline(BSA_CALCULATION_FIRED_RULE);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(toWeight("72.0,kg"));
        dataInstances.add(toHeight("180.0,cm"));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0014");
        assertThat(dataValue, Matchers.instanceOf(DvBoolean.class));
        DvBoolean dvBoolean = (DvBoolean) dataValue;
        assertThat(dvBoolean.getValue(), is(true));
    }

    @Test
    public void can_evaluate_fired_rule_expected_false() throws Exception {
        guideline = loadGuideline(BSA_CALCULATION_FIRED_RULE);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(toWeight("158.7,lbs"));
        dataInstances.add(toHeight("5.95,ft"));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0014");
        assertThat(dataValue, Matchers.instanceOf(DvBoolean.class));
        DvBoolean dvBoolean = (DvBoolean) dataValue;
        assertThat(dvBoolean.getValue(), is(false));
    }
}

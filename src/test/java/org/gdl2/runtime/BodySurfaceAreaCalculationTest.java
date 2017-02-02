package org.gdl2.runtime;

import org.gdl2.datatypes.DvQuantity;
import org.gdl2.model.Guideline;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class BodySurfaceAreaCalculationTest extends TestCommon {
    private Interpreter interpreter;

    @BeforeMethod
    public void setUp() throws Exception {
        interpreter = new Interpreter();
    }

    @Test
    public void can_run_body_surface_calculation_rule_as_single_guideline() throws Exception {
        Guideline guideline = loadGuideline(BSA_CALCULATION);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(toWeight("72.0,kg", "2012-01-01T00:00:00"));
        dataInstances.add(toHeight("180.0,cm", "2012-01-01T00:00:00"));

        List<DataInstance> result = interpreter.executeSingleGuideline(guideline, dataInstances);
        DataInstance dataInstance = result.get(0);
        DvQuantity dvQuantity = dataInstance.getDvQuantity("/data[at0001]/events[at0002]/data[at0003]/items[at0004]");
        assertThat(dvQuantity.getMagnitude(), closeTo(1.90, 0.1));
        assertThat(dvQuantity.getPrecision(), is(2));
        assertThat(dvQuantity.getUnits(), is("m2"));
    }

    @Test
    public void can_run_body_surface_calculation_rule_as_guidelines() throws Exception {
        Guideline guideline = loadGuideline(BSA_CALCULATION);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(toWeight("72.0,kg", "2012-01-01T00:00:00"));
        dataInstances.add(toHeight("180.0,cm", "2012-01-01T00:00:00"));

        List<DataInstance> result = interpreter.executeGuidelines(Collections.singletonList(guideline), dataInstances);
        DataInstance dataInstance = result.get(2);
        DvQuantity dvQuantity = dataInstance.getDvQuantity("/data[at0001]/events[at0002]/data[at0003]/items[at0004]");
        assertThat(dvQuantity.getMagnitude(), closeTo(1.90, 0.1));
        assertThat(dvQuantity.getPrecision(), is(2));
        assertThat(dvQuantity.getUnits(), is("m2"));
    }

    @Test
    public void can_run_body_surface_calculation_rule_without_when_statements() throws Exception {
        Guideline guideline = loadGuideline(BSA_CALCULATION_WITHOUT_WHEN);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(toWeight("72.0,kg", "2012-01-01T00:00:00"));
        dataInstances.add(toHeight("180.0,cm", "2012-01-01T00:00:00"));

        List<DataInstance> result = interpreter.executeSingleGuideline(guideline, dataInstances);
        DataInstance dataInstance = result.get(0);
        DvQuantity dvQuantity = dataInstance.getDvQuantity("/data[at0001]/events[at0002]/data[at0003]/items[at0004]");
        assertThat(dvQuantity.getMagnitude(), closeTo(1.90, 0.1));
        assertThat(dvQuantity.getPrecision(), is(2));
        assertThat(dvQuantity.getUnits(), is("m2"));
    }
}

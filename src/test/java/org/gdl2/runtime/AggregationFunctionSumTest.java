package org.gdl2.runtime;

import org.gdl2.datatypes.*;
import org.gdl2.model.Guideline;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AggregationFunctionSumTest extends TestCommon {
    private static final String MEDICATION = "openEHR-EHR-INSTRUCTION.medication.v1";
    private static final String INPUT_PATH = "/activities[at0001]/description[openEHR-EHR-ITEM_TREE.medication.v1]/items[at0033]/items[at0035]/items[at0037]";
    private Interpreter interpreter;

    @BeforeMethod
    public void setUp() {
        interpreter = new Interpreter();
    }

    @Test
    public void can_evaluation_sum_in_condition_with_dv_quantity() throws Exception {
        Guideline guideline = loadGuideline("Sum_in_condition_test.v1.gdl2");
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(withQuantity(80.0));
        dataInstances.add(withQuantity(50.0));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0007");
        assertThat(dataValue, Matchers.instanceOf(DvText.class));
        DvText dvText = (DvText) dataValue;
        assertThat(dvText.getValue(), is("Overdose"));
    }

    @Test
    public void can_evaluation_sum_in_condition_with_dv_count() throws Exception {
        Guideline guideline = loadGuideline("Sum_in_condition_test.v1.gdl2");
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(withCount(50));
        dataInstances.add(withCount(60));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0007");
        assertThat(dataValue, Matchers.instanceOf(DvText.class));
        DvText dvText = (DvText) dataValue;
        assertThat(dvText.getValue(), is("Overdose"));
    }

    @Test
    public void can_evaluation_sum_in_condition_with_dv_ordinal() throws Exception {
        Guideline guideline = loadGuideline("Sum_in_condition_test.v1.gdl2");
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(withOrdinal(50));
        dataInstances.add(withOrdinal(60));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0007");
        assertThat(dataValue, Matchers.instanceOf(DvText.class));
        DvText dvText = (DvText) dataValue;
        assertThat(dvText.getValue(), is("Overdose"));
    }

    @Test
    public void can_evaluation_sum_in_assignment_with_two_dv_quantity() throws Exception {
        Guideline guideline = loadGuideline("Sum_in_assignment_test.v1.gdl2");
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(withQuantity(80.0));
        dataInstances.add(withQuantity(50.0));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0007");
        assertThat(dataValue, Matchers.instanceOf(DvQuantity.class));
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), is(130.0));
    }

    @Test
    public void can_evaluation_sum_in_assignment_with_three_dv_count() throws Exception {
        Guideline guideline = loadGuideline("Sum_in_assignment_count_test.v1.gdl2");
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(withCount(10));
        dataInstances.add(withCount(40));
        dataInstances.add(withCount(5));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0007");
        assertThat(dataValue, Matchers.instanceOf(DvCount.class));
        DvCount dvCount = (DvCount) dataValue;
        assertThat(dvCount.getMagnitude(), is(55));
    }

    @Test
    public void can_evaluation_sum_in_assignment_with_four_dv_ordinal() throws Exception {
        Guideline guideline = loadGuideline("Sum_in_assignment_count_test.v1.gdl2");
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(withOrdinal(1));
        dataInstances.add(withOrdinal(2));
        dataInstances.add(withOrdinal(3));
        dataInstances.add(withOrdinal(4));

        Map<String, Object> result = interpreter.execute(guideline, dataInstances);
        Object dataValue = result.get("gt0007");
        assertThat(dataValue, Matchers.instanceOf(DvCount.class));
        DvCount dvCount = (DvCount) dataValue;
        assertThat(dvCount.getMagnitude(), is(10));
    }

    private DataInstance withQuantity(double dose) {
        return new DataInstance.Builder()
                .modelId(MEDICATION)
                .addValue(INPUT_PATH, DvQuantity.builder().magnitude(dose).precision(1).units("mg").build())
                .build();
    }

    private DataInstance withCount(int dose) {
        return new DataInstance.Builder()
                .modelId(MEDICATION)
                .addValue(INPUT_PATH, new DvCount(dose))
                .build();
    }

    private DataInstance withOrdinal(int value) {
        return new DataInstance.Builder()
                .modelId(MEDICATION)
                .addValue(INPUT_PATH, new DvOrdinal(value, new DvCodedText("v", "t", "c")))
                .build();
    }
}
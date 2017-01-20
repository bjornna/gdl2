package org.gdl2.runtime;

import org.gdl2.datatypes.DataValue;
import org.gdl2.datatypes.DvQuantity;
import org.gdl2.model.Guideline;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class MathFunctionsTest extends TestCommon {
    private final static String MATH_FUNCTIONS_TEST_GUIDE = "math_functions_test.gdl2";

    private Map<String, DataValue> result;

    @BeforeMethod
    public void setUp() throws Exception {
        Interpreter interpreter = new Interpreter();
        Guideline guideline = loadGuideline(MATH_FUNCTIONS_TEST_GUIDE);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        result = interpreter.execute(guideline, dataInstances);
    }

    @Test
    public void should_return_correct_abs_function_result() {
        DataValue dataValue = result.get("gt0003");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(5.3, 0));
    }

    @Test
    public void should_return_correct_ceil_function_result() {
        DataValue dataValue = result.get("gt0004");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(2.0, 0));
    }

    @Test
    public void should_return_correct_exp_function_result() {
        DataValue dataValue = result.get("gt0005");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(20.08, 0.1));
    }

    @Test
    public void should_return_correct_floor_function_result() {
        DataValue dataValue = result.get("gt0006");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(1.0, 0));
    }

    @Test
    public void should_return_correct_log_function_result() {
        DataValue dataValue = result.get("gt0007");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(4.60, 0.01));
    }

    @Test
    public void should_return_correct_log10_function_result() {
        DataValue dataValue = result.get("gt0008");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(3.0, 0));
    }

    @Test
    public void should_return_correct_log1p_function_result() {
        DataValue dataValue = result.get("gt0009");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(2.39, 0.01));
    }

    @Test
    public void should_return_correct_round_function_result() {
        DataValue dataValue = result.get("gt0010");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(8.0, 0));
    }

    @Test
    public void should_return_correct_sqrt_function_result() {
        DataValue dataValue = result.get("gt0011");
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(4.0, 0));
    }
}

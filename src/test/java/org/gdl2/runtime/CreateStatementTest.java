package org.gdl2.runtime;

import org.gdl2.datatypes.CodePhrase;
import org.gdl2.datatypes.DvCodedText;
import org.gdl2.datatypes.DvQuantity;
import org.gdl2.expression.AssignmentExpression;
import org.gdl2.expression.CodedTextConstant;
import org.gdl2.expression.CreateInstanceExpression;
import org.gdl2.expression.Variable;
import org.gdl2.model.Guideline;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class CreateStatementTest extends TestCommon {
    private Interpreter interpreter;
    private HashMap<String, List<Object>> inputMap;
    private HashMap<String, Object> resultMap;
    private Object dataValue;

    @BeforeMethod
    public void setUp() {
        interpreter = new Interpreter();
        inputMap = new HashMap<>();
        resultMap = new HashMap<>();
        dataValue = null;
    }

    @Test
    public void can_performance_create_statement_for_single_variable_with_coded_text() {
        // "$gt0004.create($gt0005=local::at0004|Present|)"
        CreateInstanceExpression createInstanceExpression = new CreateInstanceExpression(
                new Variable("gt0004", null, null, "create"),
                Collections.singletonList(new AssignmentExpression(Variable.createByCode("gt0005"),
                        new CodedTextConstant("Present", new CodePhrase("local", "at0004")))));
        interpreter.performAssignmentStatements(createInstanceExpression, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get("gt0005");
        assertThat(dataValue, instanceOf(DvCodedText.class));
        DvCodedText dvCodedText = (DvCodedText) dataValue;
        assertThat(dvCodedText.toString(), is("local::at0004|Present|"));
    }

    @Test
    public void can_perform_create_statement_twice_verified_by_variable_count() {
        // "$gt0004.create($gt0005=local::at0004|Present|)", "$gt0005.count>=1"
        CreateInstanceExpression createInstanceExpression = new CreateInstanceExpression(
                new Variable("gt0004", null, null, "create"),
                Collections.singletonList(new AssignmentExpression(Variable.createByCode("gt0005"),
                        new CodedTextConstant("Present", new CodePhrase("local", "at0004")))));
        interpreter.performAssignmentStatements(createInstanceExpression, inputMap, new HashMap<>(), resultMap);
        interpreter.performAssignmentStatements(createInstanceExpression, inputMap, new HashMap<>(), resultMap);
        Variable variable = new Variable("gt0005", null, null, "count");
        Object value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(value, is(2));
    }

    @Test
    public void can_create_single_instance() throws Exception {
        Guideline guideline = loadGuideline(BSA_CALCULATION_USING_CREATE);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(toWeight("72.0,kg"));
        dataInstances.add(toHeight("180.0,cm"));
        List<DataInstance> result = interpreter.executeSingleGuideline(guideline, dataInstances);
        assertThat(result.size(), is(1));
        assertExpectedBodyMassIndexValue(result.get(0));
    }

    @Test(enabled = false)
    public void can_create_two_instances() throws Exception {
        Guideline guideline = loadGuideline(BSA_CALCULATION_USING_CREATE_2);
        ArrayList<DataInstance> dataInstances = new ArrayList<>();
        dataInstances.add(toWeight("72.0,kg"));
        dataInstances.add(toHeight("180.0,cm"));
        List<DataInstance> result = interpreter.executeSingleGuideline(guideline, dataInstances);
        assertThat(result.size(), is(2));
        assertExpectedBodyMassIndexValue(result.get(0));
        assertExpectedBodyMassIndexValue(result.get(1));
    }

    private void assertExpectedBodyMassIndexValue(DataInstance dataInstance) {
        DvQuantity dvQuantity = dataInstance.getDvQuantity("/data[at0001]/events[at0002]/data[at0003]/items[at0004]");
        assertThat(dvQuantity.getMagnitude(), closeTo(1.90, 0.1));
        assertThat(dvQuantity.getPrecision(), is(2));
        assertThat(dvQuantity.getUnits(), is("m2"));
    }
}

package org.gdl2.runtime;

import org.gdl2.datatypes.*;
import org.gdl2.expression.*;
import org.gdl2.model.Guideline;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gdl2.runtime.Interpreter.CURRENT_DATETIME;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

/**
 * Group of test cases related to assignment statement of the rules
 */
public class AssignmentStatementTest extends TestCommon {
    private Interpreter interpreter;
    private HashMap<String, List<DataValue>> inputMap;
    private HashMap<String, DataValue> resultMap;
    private DataValue dataValue;
    private Map<String, DataValue> systemParameters = new HashMap<>();

    @BeforeMethod
    public void setUp() {
        interpreter = new Interpreter(systemParameters);
        inputMap = new HashMap<>();
        resultMap = new HashMap<>();
        dataValue = null;
    }

    @Test
    public void can_assign_DvQuantity_with_complex_arithmetic_expression() throws Exception {
        String expression = "$gt0013.magnitude=((($gt0005.magnitude*$gt0006.magnitude)/3600)^0.5)";
        AssignmentExpression assignmentExpression = parseAssignmentExpression(expression);
        inputMap.put("gt0005", asList(new DvQuantity("kg", 72.0, 1)));
        inputMap.put("gt0006", asList(new DvQuantity("cm", 180, 1)));
        Map<String, Class> typeMap = new HashMap<>();
        typeMap.put("gt0013", DvQuantity.class);
        interpreter.performAssignmentStatements(assignmentExpression, inputMap, typeMap, resultMap);
        assertThat(resultMap.size(), is(1));
        DataValue dataValue = resultMap.get("gt0013");
        assertThat(dataValue, Matchers.instanceOf(DvQuantity.class));
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(1.90, 0.1));
    }

    @Test
    public void can_assign_dv_quantity_with_string_value() throws Exception {
        Guideline guideline = loadGuideline("Set_dv_quantity_value_test.v1.gdl2");
        Map<String, DataValue> result = interpreter.execute(guideline, new ArrayList<>());
        DataValue dataValue = result.get("gt0013");
        assertThat(dataValue, Matchers.instanceOf(DvQuantity.class));
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), closeTo(4.5, 0.01));
    }

    @Test
    public void can_assign_DvCount_with_addition_of_two_DvOrdinals() {
        // "$gt0016.magnitude=$gt0009.value+$gt0010.value"
        Variable first = new Variable("gt0009", null, null, "value");
        Variable second = new Variable("gt0010", null, null, "value");
        Variable resultVariable = new Variable("gt0016", null, null, "magnitude");
        BinaryExpression binaryExpression = new BinaryExpression(first, second, OperatorKind.ADDITION);
        AssignmentExpression assignmentExpression = new AssignmentExpression(resultVariable, binaryExpression);
        inputMap.put(first.getCode(), asList(new DvOrdinal(1, "one", "terminology", "code")));
        inputMap.put(second.getCode(), asList(new DvOrdinal(2, "two", "terminology", "code")));
        interpreter.performAssignmentStatements(assignmentExpression, inputMap, new HashMap<>(), resultMap);
        assertThat(resultMap.size(), is(1));
        DataValue dataValue = resultMap.get(resultVariable.getCode());
        assertThat(dataValue, Matchers.instanceOf(DvCount.class));
        assertThat(((DvCount) dataValue).getMagnitude(), is(3));
    }

    @Test
    public void can_assign_DvCount_with_additions_of_several_DvOrdinals() throws Exception {
        String expression = "$gt0016.magnitude=(((((($gt0009.value+$gt0010.value)+$gt0011.value)+$gt0015.value)+$gt0012.value)+$gt0013.value)+$gt0014.value)";
        AssignmentExpression assignmentExpression = parseAssignmentExpression(expression);
        inputMap.put("gt0009", asList(new DvOrdinal(0, "one", "terminology", "code")));
        inputMap.put("gt0010", asList(new DvOrdinal(1, "one", "terminology", "code")));
        inputMap.put("gt0011", asList(new DvOrdinal(1, "one", "terminology", "code")));
        inputMap.put("gt0012", asList(new DvOrdinal(0, "one", "terminology", "code")));
        inputMap.put("gt0013", asList(new DvOrdinal(0, "one", "terminology", "code")));
        inputMap.put("gt0014", asList(new DvOrdinal(0, "one", "terminology", "code")));
        inputMap.put("gt0015", asList(new DvOrdinal(1, "one", "terminology", "code")));
        interpreter.performAssignmentStatements(assignmentExpression, inputMap, new HashMap<>(), resultMap);
        assertThat(resultMap.size(), is(1));
        DataValue dataValue = resultMap.get("gt0016");
        assertThat(dataValue, Matchers.instanceOf(DvCount.class));
        assertThat(((DvCount) dataValue).getMagnitude(), is(3));
    }

    @Test
    public void can_assign_DvCount_with_quantity_constant_expression() throws Exception {
        // $gt0006=0,d
        QuantityConstant quantityConstant = new QuantityConstant(new DvQuantity("d", 0, 0));
        Variable variable = Variable.createByCode("gt0006");
        AssignmentExpression assignmentExpression = new AssignmentExpression(variable, quantityConstant);
        interpreter.performAssignmentStatements(assignmentExpression, inputMap, new HashMap<>(), resultMap);
        assertThat(resultMap.size(), is(1));
        DataValue dataValue = resultMap.get(variable.getCode());
        assertThat(dataValue, Matchers.instanceOf(DvQuantity.class));
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), is(0.0));
        assertThat(dvQuantity.getUnits(), is("d"));
    }

    @Test
    public void can_assign_null_flavor_value() throws Exception {
        // "$gt0117.null_flavor=openehr::271|no information|"
        String code = "gt0117.null_flavor";
        Variable variable = new Variable("gt0117", null, null, "null_flavor");
        AssignmentExpression assignment = new AssignmentExpression(variable,
                new CodedTextConstant("no information", new CodePhrase("openehr", "271")));
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get(code);
        assertThat(dataValue, instanceOf(DvCodedText.class));
        assertThat(((DvCodedText) dataValue).getValue(), is("no information"));
    }

    @Test
    public void can_assign_dv_quantity_with_precision() {
        // "$gt0013.precision=2"
        String code = "gt0013";
        AssignmentExpression assignment = new AssignmentExpression(new Variable(code, "name", "path", "precision"),
                ConstantExpression.create("2"));
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get("gt0013");
        assertThat(dataValue, is(instanceOf(DvQuantity.class)));
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getPrecision(), is(2));
    }

    @Test(enabled = false)
    public void can_assign_dv_ordinal_magnitude_with_double_constant() {
        // "$gt0004.magnitude=22.2"
        String code = "gt0004";
        AssignmentExpression assignment = new AssignmentExpression(
                new Variable(code, "name", "path", "magnitude"), ConstantExpression.create("22.2"));
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get(code);
        assertThat(dataValue, is(instanceOf(DvQuantity.class)));
        DvQuantity dvQuantity = (DvQuantity) dataValue;
        assertThat(dvQuantity.getMagnitude(), is(22.2));
    }

    @Test
    public void can_assign_dv_ordinal() {
        // "$gt0010=1|local::at0028|Present|"
        String code = "gt0010";
        AssignmentExpression assignment = new AssignmentExpression(Variable.createByCode(code),
                new OrdinalConstant(new DvOrdinal(1, new DvCodedText("Present", "local", "at0028"))));
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get("gt0010");
        assertThat(dataValue, instanceOf(DvOrdinal.class));
        DvOrdinal dvOrdinal = (DvOrdinal) dataValue;
        assertThat(dvOrdinal.getValue(), is(1));
        assertThat(dvOrdinal.getTerminologyId(), is("local"));
    }


    @Test
    public void can_assign_dv_datxetime_with_current_datetime() {
        // "$gt0124.value=$currentDateTime.value"
        String code = "gt0124";
        AssignmentExpression assignment = new AssignmentExpression(new Variable(code, "datetime", "path", "value"),
                new Variable("currentDateTime"));
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get(code);
        assertThat(dataValue, instanceOf(DvDateTime.class));
    }

    @Test
    public void can_assign_dv_text_value_with_string() throws Exception {
        AssignmentExpression assignment = parseAssignmentExpression("$gt0008.value='Radiotherapy'");
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get("gt0008");
        assertThat(dataValue, instanceOf(DvText.class));
        assertThat(((DvText) dataValue).getValue(), is("Radiotherapy"));
    }

    @Test
    public void can_assign_dv_boolean() {
        // then = <"$gt0004=true",...>
        String code = "gt0004";
        AssignmentExpression assignment = new AssignmentExpression(Variable.createByCode(code),
                new StringConstant("true"));
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get(code);
        assertThat(dataValue, instanceOf(DvBoolean.class));
    }

    @Test
    public void can_calculate_years_using_datetime_value() throws Exception {
        AssignmentExpression assignment = parseAssignmentExpression("$gt0005.magnitude=(($currentDateTime.value-$gt0003.value)/1,a)");
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2017-03-17T10:52:10"));
        inputMap.put("gt0003", asList(DvDateTime.valueOf("1972-10-20T00:00:00")));
        interpreter.performAssignmentStatements(assignment, inputMap, new HashMap<>(), resultMap);
        dataValue = resultMap.get("gt0005");
        DvCount dvQuantity = (DvCount) dataValue;
        assertThat(dvQuantity.getMagnitude(), is(44));
    }
}

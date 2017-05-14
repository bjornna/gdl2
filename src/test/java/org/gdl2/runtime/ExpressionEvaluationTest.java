package org.gdl2.runtime;

import org.gdl2.datatypes.*;
import org.gdl2.expression.*;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Group of test cases related to evaluation of expressions in rules
 */
public class ExpressionEvaluationTest extends TestCommon {
    private Interpreter interpreter;
    private ExpressionItem expressionItem;
    private HashMap<String, List<Object>> inputMap;
    private Object value;


    @BeforeMethod
    public void setUp() {
        interpreter = new Interpreter();
        inputMap = new HashMap<>();
        value = null;
    }

    @Test
    public void can_evaluate_complex_arithmetic_expression() throws Exception {
        String expression = "(($gt0005.magnitude*$gt0006.magnitude)/3600)^0.5";
        expressionItem = parseExpression(expression);
        inputMap.put("gt0005", asList(new DvQuantity("kg", 72.0, 1)));
        inputMap.put("gt0006", asList(new DvQuantity("cm", 180, 1)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat((Double) value, closeTo(1.90, 0.1));
    }

    @Test
    public void can_evaluate_addition_of_two_DvOrdinals() {
        expressionItem = parseExpression("$gt0009.value+$gt0010.value");
        inputMap.put("gt0009", asList(DvOrdinal.valueOf("1|local::at0001|Text one|")));
        inputMap.put("gt0010", asList(DvOrdinal.valueOf("2|local::at0002|Text two|")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(((Double) value).intValue(), is(3));
    }

    @Test
    public void can_evaluate_equality_of_two_dv_quantity_in_number_of_days() {
        expressionItem = parseExpression("$gt0025==30,d");
        inputMap.put("gt0025", asList(new DvQuantity("d", 30, 0)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_equality_of_dv_count_with_addition() {
        expressionItem = parseExpression("$gt0015.magnitude==(7-$gt0016.magnitude)");
        inputMap.put("gt0015", asList(new DvCount(4)));
        inputMap.put("gt0016", asList(new DvCount(3)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_variable_of_last_value_in_the_list() {
        Variable variable = new Variable("gt0001");
        ArrayList<Object> dataValues = new ArrayList<>();
        dataValues.add(new DvCount(1));
        dataValues.add(new DvCount(2));
        inputMap.put(variable.getCode(), dataValues);
        value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(((DvCount) value).getMagnitude(), is(2));
    }

    @Test
    public void can_evaluate_variable_of_uninitialized_value() {
        Variable variable = new Variable("name", "gt0006", "path", "magnitude");
        value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(value, notNullValue());
    }

    @Test
    public void can_evaluate_single_dv_coded_text_expected_false() {
        expressionItem = parseExpression("$gt0013==local::at0009|Mosteller|");
        inputMap.put("gt0013", asList(DvCodedText.valueOf("local::at0008|Dubois and Dubois|")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_single_dv_coded_text_equality_expected_true() {
        expressionItem = parseExpression("$gt0013==local::at0009|Mosteller|");
        inputMap.put("gt0013", asList(new DvCodedText("Mosteller", "local", "at0009")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_single_dv_ordinal_equality_expected_true() {
        expressionItem = parseExpression("$gt0013==2|local::at0018|M1|");
        inputMap.put("gt0013", asList(DvOrdinal.valueOf("2|local::at0018|M1|")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_null_equality_check_expected_true() {
        Variable variable = new Variable("gt0099");
        BinaryExpression binaryExpression = new BinaryExpression(variable, ConstantExpression.create("null"),
                OperatorKind.EQUALITY);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_null_equality_check_expected_false() {
        Variable variable = new Variable("gt0011");
        BinaryExpression binaryExpression = new BinaryExpression(variable, ConstantExpression.create("null"),
                OperatorKind.EQUALITY);
        inputMap.put(variable.getCode(), asList(new DvCount(1)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_null_negated_equality_check_expected_false() {
        Variable variable = new Variable("gt0020");
        BinaryExpression binaryExpression = new BinaryExpression(variable, ConstantExpression.create("null"),
                OperatorKind.INEQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_null_negated_equality_check_expected_true() {
        Variable variable = new Variable("gt0011");
        BinaryExpression binaryExpression = new BinaryExpression(variable, ConstantExpression.create("null"),
                OperatorKind.INEQUAL);
        inputMap.put(variable.getCode(), asList(new DvCount(1)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_negated_equality_check_against_constant_expected_true() {
        Variable variable = new Variable("gt0011");
        expressionItem = parseExpression("$gt0011!=ICD10::E10|Diabetes type-1|");
        inputMap.put(variable.getCode(), asList(new DvCount(1)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_constant_expression_with_int() {
        value = interpreter.evaluateExpressionItem(ConstantExpression.create("2"), inputMap);
        assertThat(value, is(instanceOf(String.class)));
        assertThat(value, equalTo("2"));
    }

    @Test
    public void can_evaluate_binary_expression_with_and_operator_expected_false() {
        expressionItem = parseExpression("($gt0039!=null)&&($gt0032>$gt0040)");
        inputMap.put("gt0039", asList(DvQuantity.valueOf("9.0,d")));
        inputMap.put("gt0032", asList(DvCount.valueOf("4")));
        inputMap.put("gt0040", asList(DvCount.valueOf("6")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_binary_expression_with_negative_number() {
        expressionItem = parseExpression("$gt0001>(-0.329)");
        inputMap.put("gt0001", asList(new DvCount(0)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_DvQuantity_with_quantity_constant() {
        expressionItem = parseExpression("$gt0006<=2.0,mg");
        inputMap.put("gt0006", asList(DvQuantity.valueOf("9.0,mg")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, instanceOf(Boolean.class));
        assertThat(value, equalTo(false));
    }

    @Test
    public void can_evaluate_binary_multiplication_expression_two_constants() {
        expressionItem = parseExpression("25*4");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat(value, equalTo(100.0));
    }

    @Test
    public void can_evaluate_binary_multiplication_expression_two_variables() {
        expressionItem = parseExpression("$gt0005*$gt0006");
        inputMap.put("gt0005", asList(DvQuantity.valueOf("72.0,kg")));
        inputMap.put("gt0006", asList(DvQuantity.valueOf("180,cm")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat(value, equalTo(12960.0));
    }

    @Test
    public void can_evaluate_binary_multiplication_expression_with_one_variable_one_constant() {
        expressionItem = parseExpression("$gt0015*180");
        inputMap.put("gt0015", asList(DvQuantity.valueOf("72.0,kg")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat(value, equalTo(12960.0));
    }

    @Test
    public void can_evaluate_greater_than_expected_true() {
        expressionItem = parseExpression("10>8");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_equality_DvBoolean_and_boolean_constant_expected_true() {
        expressionItem = parseExpression("$gt0004==true");
        inputMap.put("gt0004", asList(DvBoolean.valueOf(true)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_greater_than_expected_false() {
        expressionItem = parseExpression("10>18");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_greater_than_equal_expected_true() {
        expressionItem = parseExpression("10>=7");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_greater_than_equal_expected_false() {
        expressionItem = parseExpression("10>=12");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_greater_than_equal_expected_true_equal() {
        expressionItem = parseExpression("10>=10");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_less_than_expected_true() {
        expressionItem = parseExpression("7<8");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_less_than_expected_false() {
        expressionItem = parseExpression("9<8");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_less_than_equal_expected_true() {
        expressionItem = parseExpression("7<=8");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_less_than_equal_expected_true_equal() {
        expressionItem = parseExpression("7<=7");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_less_than_equal_expected_false() {
        expressionItem = parseExpression("9<=8");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_variable_count_expect_3() {
        Variable variable = new Variable("gt0005", null, null, "count");
        inputMap.put("gt0005", Arrays.asList(new DvCount(1), new DvCount(3), new DvCount(3)));
        value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(value, is(3));
    }

    @Test
    public void can_evaluate_variable_count_expect_0() {
        Variable variable = new Variable("gt0005", null, null, "count");
        value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(value, is(0));
    }

    @Test
    public void can_evaluate_less_than_equal_expected_true_with_variable() {
        inputMap.put("gt0005", asList(DvQuantity.valueOf("72.0,kg")));
        expressionItem = parseExpression("70<=$gt0005.magnitude");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_greater_than_expected_false_with_variable() {
        expressionItem = parseExpression("$gt0005.magnitude>=80");
        inputMap.put("gt0005", asList(DvQuantity.valueOf("72.0,kg")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_variable_with_set_currentDateTime_value() {
        Variable variable = Variable.createByCode(Interpreter.CURRENT_DATETIME);
        HashMap<String, Object> systemParameters = new HashMap<>();
        systemParameters.put(Interpreter.CURRENT_DATETIME, DvDateTime.valueOf("2000-01-01T00:00:00"));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(((DvDateTime) value).getDateTime().getYear(), is(2000));
    }

    @Test
    public void can_evaluate_binary_expression_with_datetime_constant_expected_true() {
        expressionItem = parseExpression("$gt0006>=(2016-01-01T00:00:00)");
        inputMap.put("gt0006", asList(DvDateTime.valueOf("2016-01-03T00:00:00")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_DvCount_equals_integer_shorthand_syntax() {
        expressionItem = parseExpression("$gt0005==3");
        inputMap.put("gt0005", asList(new DvCount(3)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_logic_operator_with_null_value() {
        expressionItem = parseExpression("$gt0001.value>1");
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(false));
    }
}
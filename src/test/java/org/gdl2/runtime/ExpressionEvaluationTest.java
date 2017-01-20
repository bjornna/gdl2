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
    private final String CURRENT_DATETIME = "currentDateTime";
    private Interpreter interpreter;
    private HashMap<String, List<DataValue>> inputMap;
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
        ExpressionItem expressionItem = parseExpression(expression);
        inputMap.put("gt0005", asList(new DvQuantity("kg", 72.0, 1)));
        inputMap.put("gt0006", asList(new DvQuantity("cm", 180, 1)));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat((Double) value, closeTo(1.90, 0.1));
    }

    @Test
    public void can_evaluate_addition_of_two_DvOrdinals() {
        // $gt0009.value+$gt0010.value"
        Variable first = new Variable("gt0009", null, null, "value");
        Variable second = new Variable("gt0010", null, null, "value");
        BinaryExpression binaryExpression = new BinaryExpression(first, second, OperatorKind.ADDITION);
        inputMap.put(first.getCode(), asList(new DvOrdinal(1, "one", "terminology", "code")));
        inputMap.put(second.getCode(), asList(new DvOrdinal(2, "two", "terminology", "code")));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(((Double) value).intValue(), is(3));
    }

    @Test
    public void can_evaluate_equality_of_two_dv_quantity_in_number_of_days() {
        // "$gt0025==30,d"
        Variable variable = Variable.createByCode("gt0025");
        QuantityConstant quantityConstant = new QuantityConstant(new DvQuantity("d", 30, 0));
        BinaryExpression binaryExpression = new BinaryExpression(variable, quantityConstant, OperatorKind.EQUALITY);
        inputMap.put(variable.getCode(), asList(new DvQuantity("d", 30, 0)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_variable_of_last_value_in_the_list() {
        Variable variable = new Variable("gt0001");
        ArrayList<DataValue> dataValues = new ArrayList<>();
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
        DvCodedText dvCodedText = new DvCodedText("Mosteller", "local", "at0009");
        Variable variable = new Variable("gt0011");
        ExpressionItem when = new BinaryExpression(variable,
                new CodedTextConstant(dvCodedText.getValue(), dvCodedText.getDefiningCode()), OperatorKind.EQUALITY);
        inputMap.put(variable.getCode(), asList(new DvCodedText("Dubois and Dubois", "local", "at0008")));
        value = interpreter.evaluateExpressionItem(when, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_single_dv_coded_text_equality_expected_true() {
        DvCodedText dvCodedText = new DvCodedText("Mosteller", "local", "at0009");
        Variable variable = new Variable("gt0105");
        ExpressionItem when = new BinaryExpression(variable,
                new CodedTextConstant(dvCodedText.getValue(), dvCodedText.getDefiningCode()), OperatorKind.EQUALITY);

        inputMap.put(variable.getCode(), asList(dvCodedText));
        value = interpreter.evaluateExpressionItem(when, inputMap);
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
    public void can_evaluate_null_negated_equality_check_against_constant_expected_true() {
        Variable variable = new Variable("gt0011");
        BinaryExpression binaryExpression = new BinaryExpression(variable,
                new CodedTextConstant("Diabetes type-1", new CodePhrase("ICD10", "E10")),
                OperatorKind.INEQUAL);
        inputMap.put(variable.getCode(), asList(new DvCount(1)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
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
        // ($gt0039!=null)&&($gt0032>$gt0040)
        BinaryExpression leftBinary = new BinaryExpression(Variable.createByCode("gt0039"),
                StringConstant.create("null"), OperatorKind.INEQUAL);
        BinaryExpression rightBinary = new BinaryExpression(Variable.createByCode("gt0032"),
                Variable.createByCode("gt0040"), OperatorKind.GREATER_THAN);
        BinaryExpression binaryExpression = new BinaryExpression(leftBinary, rightBinary, OperatorKind.AND);

        inputMap.put("gt0039", asList(new DvQuantity("d", 9.0, 0)));
        inputMap.put("gt0032", asList(new DvCount(4)));
        inputMap.put("gt0040", asList(new DvCount(6)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_binary_expression_with_negative_number() {
        // $gt0001>(-0.329)
        BinaryExpression binaryExpression = new BinaryExpression(new Variable("gt0001"), new ConstantExpression("(-0.329)"),
                OperatorKind.GREATER_THAN);
        inputMap.put("gt0001", asList(new DvCount(0)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_DvQuantity_with_quantity_constant() {
        // "$gt0006<=2,d"
        Variable variable = Variable.createByCode("gt0006");
        inputMap.put(variable.getCode(), asList(new DvQuantity("mg", 9.0, 0)));
        BinaryExpression binaryExpression = BinaryExpression.create(variable,
                new QuantityConstant(new DvQuantity("mg", 2.0, 0)), OperatorKind.LESS_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, instanceOf(Boolean.class));
        assertThat(value, equalTo(false));
    }

    @Test
    public void can_evaluate_binary_multiplication_expression__two_constants() {
        BinaryExpression binaryExpression = BinaryExpression.create(ConstantExpression.create("25"),
                ConstantExpression.create("4"), OperatorKind.MULTIPLICATION);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat(value, equalTo(100.0));
    }

    @Test
    public void can_evaluate_binary_multiplication_expression__two_variables() {
        String leftCode = "gt0005";
        String rightCode = "gt0006";
        Variable leftVariable = new Variable(leftCode, "name", "path", "magnitude");
        Variable rightVariable = new Variable(rightCode, "name", "path", "magnitude");
        BinaryExpression binaryExpression = BinaryExpression.create(leftVariable, rightVariable, OperatorKind.MULTIPLICATION);
        inputMap.put(leftCode, asList(new DvQuantity("kg", 72.0, 1)));
        inputMap.put(rightCode, asList(new DvQuantity("cm", 180, 1)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat(value, equalTo(12960.0));
    }

    @Test
    public void can_evaluate_binary_multiplication_expression_with_one_variable_one_constant() {
        String leftCode = "gt0005";
        Variable leftVariable = new Variable(leftCode, "name", "path", "magnitude");
        ConstantExpression constantExpression = ConstantExpression.create("180");
        BinaryExpression binaryExpression = BinaryExpression.create(leftVariable, constantExpression, OperatorKind.MULTIPLICATION);
        inputMap.put(leftCode, asList(new DvQuantity("kg", 72.0, 1)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, instanceOf(Double.class));
        assertThat(value, equalTo(12960.0));
    }

    @Test
    public void can_evaluate_greater_than_expected_true() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("10"),
                ConstantExpression.create("8"), OperatorKind.GREATER_THAN);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_equality_DvBoolean_and_boolean_constant_expected_true() {
        // $gt0004==true"
        Variable variable = Variable.createByCode("gt0004");
        inputMap.put(variable.getCode(), asList(DvBoolean.valueOf(true)));
        BinaryExpression binaryExpression = new BinaryExpression(variable, StringConstant.create("true"), OperatorKind.EQUALITY);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_greater_than_expected_false() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("10"),
                ConstantExpression.create("18"), OperatorKind.GREATER_THAN);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_greater_than_equal_expected_true() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("10"),
                ConstantExpression.create("7"), OperatorKind.GREATER_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_greater_than_equal_expected_false() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("10"),
                ConstantExpression.create("12"), OperatorKind.GREATER_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_greater_than_equal_expected_true_equal() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("10"),
                ConstantExpression.create("10"), OperatorKind.GREATER_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_less_than_expected_true() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("7"),
                ConstantExpression.create("8"), OperatorKind.LESS_THAN);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_less_than_expected_false() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("9"),
                ConstantExpression.create("8"), OperatorKind.LESS_THAN);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_less_than_equal_expected_true() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("7"),
                ConstantExpression.create("8"), OperatorKind.LESS_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
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
    public void can_evaluate_less_than_equal_expected_true_equal() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("7"),
                ConstantExpression.create("7"), OperatorKind.LESS_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_less_than_equal_expected_false() {
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("9"),
                ConstantExpression.create("8"), OperatorKind.LESS_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_less_than_equal_expected_true_with_variable() {
        inputMap.put("gt0005", asList(new DvQuantity("kg", 72.0, 1)));
        BinaryExpression binaryExpression = new BinaryExpression(ConstantExpression.create("70"),
                new Variable("gt0005", "name", "path", "magnitude"), OperatorKind.LESS_THAN_OR_EQUAL);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_greater_than_expected_false_with_variable() {
        inputMap.put("gt0005", asList(new DvQuantity("kg", 72.0, 1)));
        BinaryExpression binaryExpression = new BinaryExpression(new Variable("gt0005", "name", "path", "magnitude"),
                ConstantExpression.create("80"), OperatorKind.GREATER_THAN);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_evaluate_variable_with_datetime_value() {
        // $gt0113.value
        Variable variable = new Variable("gt0113", null, null, "value");
        inputMap.put("gt0113", asList(DvDateTime.valueOf("1940-01-01T00:00:00")));
        value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(value, is(-946774800000L));
    }

    @Test
    public void can_evaluate_variable_with_set_currentDateTime_value() {
        // $currentDateTime
        Variable variable = Variable.createByCode(CURRENT_DATETIME);
        HashMap<String, DataValue> systemParameters = new HashMap<>();
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2000-01-01T00:00:00"));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(((DvDateTime) value).getDateTime().getYear(), is(2000));
    }

    @Test
    public void can_evaluate_binary_expression_with_datetime_constant_expected_true() {
        // "$gt0006>=(2016-01-01T00:00:00)"
        Variable variable = Variable.createByCode("gt0006");
        BinaryExpression binaryExpression = new BinaryExpression(variable, new DateTimeConstant("2016-01-01T00:00:00"),
                OperatorKind.GREATER_THAN_OR_EQUAL);
        inputMap.put(variable.getCode(), asList(DvDateTime.valueOf("2016-01-03T00:00:00")));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_DvCount_equals_integer_shorthand_syntax() {
        // "$gt0005==3"
        Variable variable = new Variable("gt0005");
        BinaryExpression binaryExpression = new BinaryExpression(variable, ConstantExpression.create("3"),
                OperatorKind.EQUALITY);
        inputMap.put(variable.getCode(), asList(new DvCount(3)));
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_logic_operator_with_null_value() {
        // $gt0052.value>1
        BinaryExpression binaryExpression = new BinaryExpression(new Variable("gt0001"), new ConstantExpression("1"),
                OperatorKind.GREATER_THAN);
        value = interpreter.evaluateExpressionItem(binaryExpression, inputMap);
        assertThat(value, is(false));
    }

 /*   @Test
    public void can_evaluate_isa_relationship_with_subcode() throws Exception {
        String bindingCode = "gt0015";
        DvCodedText codedInput = new DvCodedText("Simvastatin", "ATC", "C10AA01");
        DvCodedText localBinding = new DvCodedText("Statins", "local", bindingCode);
        List<CodePhrase> bindingList = new ArrayList<>();
        bindingList.add(new CodePhrase("ATC", "C10AA"));
        bindingList.add(new CodePhrase("ATC", "C10BA"));
        Binding binding = new Binding(bindingCode, bindingList);
        HashMap<String, Binding> bindings = new HashMap<>();
        bindings.put(bindingCode, binding);
        TermBinding termBinding = new TermBinding("ATC", bindings);
        Map<String, TermBinding> termBindings = new HashMap<>();
        termBindings.put("ATC", termBinding);
        GuideOntology guideOntology = new GuideOntology();
        //guideOntology.setTermBindings(termBindings);
        assertThat(interpreter.evaluateIsARelationship(codedInput, localBinding, guideOntology), is(true));
    }*/
}

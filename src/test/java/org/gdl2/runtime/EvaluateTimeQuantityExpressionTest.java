package org.gdl2.runtime;

import org.gdl2.datatypes.DataValue;
import org.gdl2.datatypes.DvDateTime;
import org.gdl2.expression.ExpressionItem;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.gdl2.runtime.Interpreter.CURRENT_DATETIME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EvaluateTimeQuantityExpressionTest extends TestCommon {
    private Interpreter interpreter;
    private Map<String, List<DataValue>> inputMap = new HashMap<>();
    private Object value;

    @BeforeMethod
    public void setUp() {
        interpreter = new Interpreter();
        inputMap.clear();
    }

    @Test
    public void can_compare_datetime_and_years_expect_false() {
        ExpressionItem expression = parseExpression("$gt0113.value<=($currentDateTime.value-65,a)");
        Map<String, DataValue> systemParameters = new HashMap<>();
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2017-01-09T23:59:59"));
        inputMap.put("gt0113", asList(DvDateTime.valueOf("1952-01-10T00:00:00")));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(expression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_compare_datetime_and_years_expect_true() {
        ExpressionItem expression = parseExpression("$gt0113.value<=($currentDateTime.value-65,a)");
        Map<String, DataValue> systemParameters = new HashMap<>();
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2017-01-10T00:00:01"));
        inputMap.put("gt0113", asList(DvDateTime.valueOf("1952-01-10T00:00:00")));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(expression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_compare_datetime_and_months_expect_false() {
        ExpressionItem expression = parseExpression("$gt0113.value<=($currentDateTime.value-3,mo)");
        Map<String, DataValue> systemParameters = new HashMap<>();
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2017-04-09T23:59:59"));
        inputMap.put("gt0013", asList(DvDateTime.valueOf("2017-01-10T00:00:00")));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(expression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_compare_datetime_and_months_expect_true() {
        ExpressionItem expression = parseExpression("$gt0113.value<=($currentDateTime.value-3,mo)");
        Map<String, DataValue> systemParameters = new HashMap<>();
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2017-04-01T00:00:01"));
        inputMap.put("gt0113", asList(DvDateTime.valueOf("2017-01-01T00:00:00")));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(expression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_compare_datetime_and_days_expect_false() {
        ExpressionItem expression = parseExpression("$gt0113.value<=($currentDateTime.value-14,d)");
        Map<String, DataValue> systemParameters = new HashMap<>();
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2017-01-29T23:59:59"));
        inputMap.put("gt0113", asList(DvDateTime.valueOf("2017-01-16T00:00:00")));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(expression, inputMap);
        assertThat(value, is(false));
    }

    @Test
    public void can_compare_datetime_and_days_expect_true() {
        ExpressionItem expression = parseExpression("$gt0113.value<=($currentDateTime.value-14,d)");
        Map<String, DataValue> systemParameters = new HashMap<>();
        systemParameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2017-01-30T00:00:01"));
        inputMap.put("gt0113", asList(DvDateTime.valueOf("2017-01-16T00:00:00")));
        interpreter = new Interpreter(systemParameters);
        value = interpreter.evaluateExpressionItem(expression, inputMap);
        assertThat(value, is(true));
    }

    @Test
    public void can_evaluate_condition_with_two_dates_within_90_days_apart_expect_true() {
        ExpressionItem expressionItem = parseExpression("$gt0023.value<($gt0004.value+90,d)");
        inputMap.put("gt0023", asList(DvDateTime.valueOf("2014-04-10T18:18:00")));
        inputMap.put("gt0004", asList(DvDateTime.valueOf("2014-01-11T13:14:11")));
        value = interpreter.evaluateExpressionItem(expressionItem, inputMap);
        assertThat(value, is(true));
    }
}

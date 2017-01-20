package org.gdl2.runtime;

import org.gdl2.datatypes.CodePhrase;
import org.gdl2.datatypes.DataValue;
import org.gdl2.datatypes.DvCodedText;
import org.gdl2.expression.AssignmentExpression;
import org.gdl2.expression.CodedTextConstant;
import org.gdl2.expression.CreateInstanceExpression;
import org.gdl2.expression.Variable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CreateStatementTest extends TestCommon {
    private Interpreter interpreter;
    private HashMap<String, List<DataValue>> inputMap;
    private HashMap<String, DataValue> resultMap;
    private DataValue dataValue;

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
        interpreter.mergeValueMapIntoListValueMap(resultMap, inputMap);
        interpreter.performAssignmentStatements(createInstanceExpression, inputMap, new HashMap<>(), resultMap);
        interpreter.mergeValueMapIntoListValueMap(resultMap, inputMap);
        Variable variable = new Variable("gt0005", null, null, "count");
        Object value = interpreter.evaluateExpressionItem(variable, inputMap);
        assertThat(value, is(2));
    }
}

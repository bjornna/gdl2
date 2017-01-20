package org.gdl2.runtime;

import org.gdl2.datatypes.DvCount;
import org.gdl2.datatypes.DvQuantity;
import org.gdl2.expression.AssignmentExpression;
import org.gdl2.expression.ConstantExpression;
import org.gdl2.expression.ExpressionItem;
import org.gdl2.expression.Variable;
import org.hamcrest.core.Is;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

public class TypeBindingTest {
    private Interpreter interpreter;
    private List<ExpressionItem> assignmentExpressionList;
    private Map<String, Class> typeMap;
    private String code;

    @BeforeTest
    public void setUp() {
        interpreter = new Interpreter();
        assignmentExpressionList = new ArrayList<>();
    }

    @AfterMethod
    public void after_method() {
        assignmentExpressionList.clear();
    }

    @Test
    public void can_bind_to_dv_quantity_with_units_magnitude() {
        code = "gt0001";
        assignmentExpressionList.add(
                new AssignmentExpression(new Variable(code, null, null, TypeBinding.UNITS),
                        new ConstantExpression("kg")));
        assignmentExpressionList.add(
                new AssignmentExpression(new Variable(code, null, null, TypeBinding.MAGNITUDE),
                        new ConstantExpression("100.0")));
        typeMap = interpreter.typeBindingThroughAssignmentStatements(assignmentExpressionList);
        assertThat(typeMap.get(code), Is.<Class>is(DvQuantity.class));
    }

    @Test
    public void can_bind_to_dv_quantity_with_units() {
        code = "gt0001";
        assignmentExpressionList.add(
                new AssignmentExpression(new Variable(code, null, null, TypeBinding.UNITS),
                        new ConstantExpression("kg")));
        typeMap = interpreter.typeBindingThroughAssignmentStatements(assignmentExpressionList);
        assertThat(typeMap.get(code), Is.<Class>is(DvQuantity.class));
    }

    @Test
    public void can_bind_to_dv_count_with_magnitude() {
        code = "gt0001";
        assignmentExpressionList.add(
                new AssignmentExpression(new Variable(code, null, null, TypeBinding.MAGNITUDE),
                        new ConstantExpression("10")));
        typeMap = interpreter.typeBindingThroughAssignmentStatements(assignmentExpressionList);
        assertThat(typeMap.get(code), Is.<Class>is(DvCount.class));
    }
}

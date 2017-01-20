package org.gdl2.expression;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FunctionalExpressionTest {

    @Test
    public void testCreateSimpleFunctionalExpression() {
        FunctionalExpression fe = FunctionalExpression.create(new Function("log"));
        assertThat(fe.toString(), is("log()"));
    }

    @Test
    public void testCreateSimpleFunctionalExpressionWithSingleVariable() {
        FunctionalExpression fe = FunctionalExpression.create(new Function("log"),
                ConstantExpression.create("180"));
        assertThat(fe.toString(), is("log(180)"));
    }

    @Test
    public void testCreateFunctionalExpressionWithNestedVariables() {
        List<ExpressionItem> items = new ArrayList<>();
        items.add(ConstantExpression.create("180"));
        BinaryExpression be1 = BinaryExpression.create(
                Variable.createByCode("gt0001"),
                ConstantExpression.create("2"),
                OperatorKind.MULTIPLICATION);
        items.add(be1);
        FunctionalExpression fe = FunctionalExpression.create(new Function("max"),
                items);
        assertThat(fe.toString(), is("max(180,($gt0001*2))"));
    }
}
package org.gdl2.expression;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UnaryExpressionTest {

    @Test
    public void testToStringWithBooleanExpression() {
        BinaryExpression be = new BinaryExpression(new Variable("gt0001"),
                new ConstantExpression("null"), OperatorKind.INEQUAL);
        UnaryExpression ue = new UnaryExpression(be, OperatorKind.FOR_ALL);
        assertThat(ue.toString(), is("for_all($gt0001!=null)"));
    }

    @Test
    public void testToStringWithVariable() {
        UnaryExpression ue = new UnaryExpression(new Variable("gt0001"),
                OperatorKind.FOR_ALL);
        assertThat(ue.toString(), is("for_all($gt0001)"));
    }
}
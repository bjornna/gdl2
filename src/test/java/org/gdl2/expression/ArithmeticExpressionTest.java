package org.gdl2.expression;

import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ArithmeticExpressionTest {

    @Test
    public void testPrintNestedExpressions() throws Exception {
        BinaryExpression exp1 = new BinaryExpression(new Variable("gt0007"),
                new ConstantExpression("70"), OperatorKind.DIVISION);
        BinaryExpression exp2 = new BinaryExpression(exp1,
                new ConstantExpression("0.7"), OperatorKind.EXPONENT);
        BinaryExpression exp3 = new BinaryExpression(new ConstantExpression("42.5"),
                new Variable("gt0020"), OperatorKind.MULTIPLICATION);
        BinaryExpression exp4 = new BinaryExpression(exp3, new Variable("gt0009"),
                OperatorKind.DIVISION);
        BinaryExpression expression = new BinaryExpression(exp4, exp2, OperatorKind.DIVISION);

        assertThat(expression.toString(), is("((42.5*$gt0020)/$gt0009)/(($gt0007/70)^0.7)"));
    }

    @Test
    public void testPrintNestedExpressionsWithVariableName() throws Exception {
        BinaryExpression exp1 = new BinaryExpression(new Variable("gt0007", "weight"),
                new ConstantExpression("70"), OperatorKind.DIVISION);
        BinaryExpression exp2 = new BinaryExpression(exp1,
                new ConstantExpression("0.7"), OperatorKind.EXPONENT);
        BinaryExpression exp3 = new BinaryExpression(new ConstantExpression("42.5"),
                new Variable("gt0020", "height"), OperatorKind.MULTIPLICATION);
        BinaryExpression exp4 = new BinaryExpression(exp3, new Variable("gt0009", "creatine"),
                OperatorKind.DIVISION);
        BinaryExpression expression = new BinaryExpression(exp4, exp2, OperatorKind.DIVISION);

        String expected = "((42.5*$gt0020|height|)/$gt0009|creatine|)/(($gt0007|weight|/70)^0.7)";
        assertThat(expression.toString(), is(expected));
    }
}
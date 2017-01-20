package org.gdl2.expression;

import org.gdl2.datatypes.CodePhrase;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LogicalExpressionTest {
    private BinaryExpression binaryExpression;

    @Test
    public void testCreateSimpleGreaterThanExpression() {
        binaryExpression = BinaryExpression
                .create(Variable.createByCode("gt0001"),
                        ConstantExpression.create("100"),
                        OperatorKind.GREATER_THAN);

        assertThat(binaryExpression.toString(), is("$gt0001>100"));
    }

    @Test
    public void testCreateISAExpression() {
        binaryExpression = BinaryExpression
                .create(Variable.createByCode("gt0001"),
                        new CodePhraseConstant(new CodePhrase("ICD10", "1000")),
                        OperatorKind.IS_A);
        assertThat(binaryExpression.toString(), is("$gt0001 is_a ICD10::1000"));
    }

    @Test
    public void testCreateNestedAndLogicalExpression() {
        BinaryExpression binaryExpression1 = BinaryExpression.create(
                Variable.createByCode("gt0001"),
                ConstantExpression.create("100"), OperatorKind.LESS_THAN);

        BinaryExpression binaryExpression2 = BinaryExpression.create(
                Variable.createByCode("gt0001"),
                ConstantExpression.create("10"),
                OperatorKind.GREATER_THAN_OR_EQUAL);

        binaryExpression = BinaryExpression.create(binaryExpression1, binaryExpression2,
                OperatorKind.AND);

        assertThat(binaryExpression.toString(), is("($gt0001<100)&&($gt0001>=10)"));
    }

    @Test
    public void testCreateDoubleNestedORLogicalExpression() {
        BinaryExpression binaryExpression1 = BinaryExpression.create(
                Variable.createByCode("gt0001"),
                ConstantExpression.create("100"), OperatorKind.LESS_THAN);

        BinaryExpression binaryExpression2 = BinaryExpression.create(
                Variable.createByCode("gt0001"),
                ConstantExpression.create("10"),
                OperatorKind.GREATER_THAN_OR_EQUAL);

        BinaryExpression binaryExpression3 = BinaryExpression.create(binaryExpression1, binaryExpression2,
                OperatorKind.AND);

        BinaryExpression binaryExpression4 = BinaryExpression.create(
                Variable.createByCode("gt0002"),
                ConstantExpression.create("30"),
                OperatorKind.LESS_THAN_OR_EQUAL);

        binaryExpression = BinaryExpression.create(binaryExpression4, binaryExpression3,
                OperatorKind.OR);
        assertThat(binaryExpression.toString(), is("($gt0002<=30)||(($gt0001<100)&&($gt0001>=10))"));
    }

    @Test
    public void testStringEqualityExpression() {
        binaryExpression = BinaryExpression
                .create(Variable.createByCode("gt0001"),
                        new StringConstant("string value"),
                        OperatorKind.EQUALITY);

        assertThat(binaryExpression.toString(), is("$gt0001=='string value'"));
    }
}
package org.gdl2.expression;

import org.gdl2.datatypes.CodePhrase;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AssignmentExpressionTest {

    @Test
    public void testToStringWithIntegerConstant() {
        AssignmentExpression ae = new AssignmentExpression(
                new Variable("gt0001"), new ConstantExpression("10"));
        assertThat(ae.toString(), is("$gt0001=10"));
    }

    @Test
    public void testToStringWithCodedTextConstant() {
        AssignmentExpression ae = new AssignmentExpression(
                new Variable("gt0001"), new CodedTextConstant(
                "Warfarin", new CodePhrase("ATC", "B01AA03")));
        assertThat(ae.toString(), is("$gt0001=ATC::B01AA03|Warfarin|"));
    }

    @Test
    public void testToStringWithStringConstant() {
        AssignmentExpression ae = new AssignmentExpression(
                new Variable("gt0001"), new StringConstant("sitting"));
        assertThat(ae.toString(), is("$gt0001='sitting'"));
    }

    @Test
    public void testToStringWithBooleanExpression() {
        Variable var1 = new Variable("gt0010");
        Variable var2 = new Variable("gt0012");
        BinaryExpression be = new BinaryExpression(var1, var2, OperatorKind.ADDITION);
        AssignmentExpression ae = new AssignmentExpression(
                new Variable("gt0001"), be);
        assertThat(ae.toString(), is("$gt0001=($gt0010+$gt0012)"));
    }
}
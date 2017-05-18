package org.gdl2.expression;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DoubleConstantTest {

    @Test
    public void can_create_double_constant_with_brackets() {
        DoubleConstant doubleConstant = new DoubleConstant("(-0.329)");
        assertThat(doubleConstant.getDoubleValue(), is(-0.329));
    }
}

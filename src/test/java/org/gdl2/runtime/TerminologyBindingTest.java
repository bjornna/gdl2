package org.gdl2.runtime;

import org.gdl2.datatypes.DvCodedText;
import org.gdl2.expression.ExpressionItem;
import org.gdl2.model.Guideline;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TerminologyBindingTest extends TestCommon {
    private Interpreter interpreter;
    private ExpressionItem expression;
    private Guideline guide;
    private HashMap<String, List<Object>> inputMap;
    private Object value;

    @BeforeMethod
    public void setUp() throws Exception {
        interpreter = new Interpreter();
        inputMap = new HashMap<>();
        guide = loadGuideline("CHA2DS2VASc_diagnosis_review.v1.0.1.gdl2");
        expression = parseExpression("$gt0040 is_a local::gt0102|Diabetes|");
        value = null;
    }

    @Test
    public void can_check_isa_relationship_using_local_term_bindings_expected_true() throws Exception {
        inputMap.put("gt0040", asList(new DvCodedText("Diabetes Type-1", "ICD10", "E10")));
        value = interpreter.evaluateExpressionItem(expression, inputMap, guide.getOntology(), null);
        assertThat(value, is(true));
    }

    @Test
    public void can_check_isa_relationship_using_local_term_bindings_expected_false() throws Exception {
        inputMap.put("gt0040", asList(new DvCodedText("Heart failure", "ICD10", "I50")));
        value = interpreter.evaluateExpressionItem(expression, inputMap, guide.getOntology(), null);
        assertThat(value, is(false));
    }
}
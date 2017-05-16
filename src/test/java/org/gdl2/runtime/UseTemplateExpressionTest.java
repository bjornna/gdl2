package org.gdl2.runtime;

import org.gdl2.datatypes.DvOrdinal;
import org.gdl2.expression.UseTemplateExpression;
import org.gdl2.model.Guideline;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UseTemplateExpressionTest extends TestCommon {
    private Guideline guideline;
    private Interpreter interpreter;
    private HashMap<String, Object> resultMap;
    private Object dataValue;

    @BeforeMethod
    public void setUp() throws Exception {
        guideline = loadGuideline("create_using_template_single_obj_test.v0.1.gdl2");
        interpreter = new Interpreter();
        resultMap = new HashMap<>();
        dataValue = null;
    }

    @Test
    public void can_perform_use_template_expression_expression() {
        UseTemplateExpression useTemplateExpression = (UseTemplateExpression) guideline.getDefinition().getRules().get("gt0034").getThen().get(0);
        interpreter.performUseTemplateStatement(useTemplateExpression, guideline.getDefinition().getTemplates(), resultMap);
        dataValue = resultMap.get("gt0022");
        assertThat(dataValue, instanceOf(DvOrdinal.class));
        DvOrdinal dvOrdinal = (DvOrdinal) dataValue;
        assertThat(dvOrdinal.toString(), is("3|ATC::C10AA05|atorvastatin|"));
    }

    @Test
    public void can_execute_guideline_with_use_template_expression() {
        List<DataInstance> input = new ArrayList<>();
        List<Guideline> guidelines = Collections.singletonList(guideline);
        List<DataInstance> dataInstanceList = interpreter.executeGuidelines(guidelines, input);
        assertThat(dataInstanceList.size(), is(1));
        dataValue = dataInstanceList.get(0).get("/");
        DvOrdinal dvOrdinal = (DvOrdinal) dataValue;
        assertThat(dvOrdinal.toString(), is("3|ATC::C10AA05|atorvastatin|"));
    }
}

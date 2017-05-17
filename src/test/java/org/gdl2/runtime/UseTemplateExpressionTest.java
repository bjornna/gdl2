package org.gdl2.runtime;

import org.gdl2.datatypes.DvOrdinal;
import org.gdl2.model.Guideline;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Goal;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.Assert.assertTrue;

public class UseTemplateExpressionTest extends TestCommon {
    private Guideline guideline;
    private Interpreter interpreter;
    private List<DataInstance> input;
    private List<DataInstance> output;


    @BeforeMethod
    public void setUp() throws Exception {
        input = new ArrayList<>();
    }

    @Test
    public void can_use_template_create_gdl2_ordinal() throws Exception {
        interpreter = new Interpreter();
        guideline = loadGuideline("create_using_template_with_ordinal_test.v0.1.gdl2");
        List<Guideline> guidelines = Collections.singletonList(guideline);
        output = interpreter.executeGuidelines(guidelines, input);
        assertThat(output.size(), is(1));
        assertThat(output.get(0).get("/"), instanceOf(DvOrdinal.class));
        DvOrdinal dvOrdinal = (DvOrdinal) output.get(0).get("/");
        assertThat(dvOrdinal.toString(), is("3|ATC::C10AA05|atorvastatin|"));
    }

    @Test
    public void can_use_template_create_fhir_resources_expect_3_resources() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(Interpreter.OBJECT_CREATOR, new FhirDstu3ResourceCreator());
        interpreter = new Interpreter(params);
        guideline = loadGuideline("create_using_template_with_fhir_resources_test.v0.1.gdl2");
        List<Guideline> guidelines = Collections.singletonList(guideline);
        output = interpreter.executeGuidelines(guidelines, input);
        assertThat(output.size(), is(3));
        assertTrue(output.get(0).get("/") instanceof MedicationRequest);
        assertTrue(output.get(1).get("/") instanceof Goal);
        assertTrue(output.get(2).get("/") instanceof Appointment);
    }
}

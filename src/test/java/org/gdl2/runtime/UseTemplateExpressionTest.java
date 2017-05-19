package org.gdl2.runtime;

import org.gdl2.datatypes.DvDateTime;
import org.gdl2.datatypes.DvOrdinal;
import org.gdl2.datatypes.DvQuantity;
import org.gdl2.model.Guideline;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Goal;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public void can_use_template_create_ordinal() throws Exception {
        interpreter = new Interpreter();
        guideline = loadGuideline("use_template_with_ordinal_test.v0.1.gdl2");
        List<Guideline> guidelines = Collections.singletonList(guideline);
        output = interpreter.executeGuidelines(guidelines, input);
        assertThat(output.get(0).getRoot(), instanceOf(DvOrdinal.class));
        DvOrdinal dvOrdinal = (DvOrdinal) output.get(0).get("/");
        assertThat(dvOrdinal.toString(), is("3|ATC::C10AA05|atorvastatin|"));
    }

    @Test
    public void can_use_template_create_quantity() throws Exception {
        interpreter = new Interpreter();
        guideline = loadGuideline("use_template_with_quantity_test.v0.1.gdl2");
        List<Guideline> guidelines = Collections.singletonList(guideline);
        output = interpreter.executeGuidelines(guidelines, input);
        assertThat(output.get(0).getRoot(), instanceOf(DvQuantity.class));
        DvQuantity dvQuantity = (DvQuantity) output.get(0).getRoot();
        assertThat(dvQuantity.toString(), is("7.5,mg"));
    }

    @Test
    public void can_use_template_create_quantity_with_double_variable() throws Exception {
        interpreter = new Interpreter();
        guideline = loadGuideline("use_template_with_quantity_set_value_test.v0.1.gdl2");
        List<Guideline> guidelines = Collections.singletonList(guideline);
        output = interpreter.executeGuidelines(guidelines, input);
        assertThat(output.get(0).getRoot(), instanceOf(DvQuantity.class));
        DvQuantity dvQuantity = (DvQuantity) output.get(0).getRoot();
        assertThat(dvQuantity.toString(), is("8.5,mg"));
    }

    @Test
    public void can_use_template_create_3_fhir_domain_resources() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(Interpreter.OBJECT_CREATOR, new FhirDstu3ResourceCreator());
        interpreter = new Interpreter(params);
        guideline = loadGuideline("use_template_with_fhir_resources_test.v0.1.gdl2");
        List<Guideline> guidelines = Collections.singletonList(guideline);
        output = interpreter.executeGuidelines(guidelines, input);
        assertTrue(output.get(0).getRoot() instanceof MedicationRequest);
        assertTrue(output.get(1).getRoot() instanceof Goal);
        assertTrue(output.get(2).getRoot() instanceof Appointment);
    }

    @Test
    public void can_use_template_create_fhir_appointment_with_datetime_variable() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(Interpreter.CURRENT_DATETIME, DvDateTime.valueOf("2013-04-20T14:00:00"));
        params.put(Interpreter.OBJECT_CREATOR, new FhirDstu3ResourceCreator());
        interpreter = new Interpreter(params);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        guideline = loadGuideline("use_template_fhir_appointment_set_datetime_test.v0.1.gdl2");
        List<Guideline> guidelines = Collections.singletonList(guideline);
        output = interpreter.executeGuidelines(guidelines, input);
        assertThat(output.get(0).getRoot(), instanceOf(Appointment.class));
        Appointment appointment = (Appointment) output.get(0).getRoot();
        assertThat(appointment.getRequestedPeriod().get(0).getStart(), is(dateFormat.parse("2013-04-20")));
        assertThat(appointment.getRequestedPeriod().get(0).getEnd(), is(dateFormat.parse("2013-04-25")));
    }
}

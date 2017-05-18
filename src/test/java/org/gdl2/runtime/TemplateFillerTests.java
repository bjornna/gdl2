package org.gdl2.runtime;

import com.google.gson.Gson;
import org.gdl2.datatypes.DvDateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.testng.Assert.assertEquals;

public class TemplateFillerTests extends TestCommon {
    private TemplateFiller templateFiller = new TemplateFiller();
    private String source;
    private Map<String, Object> values;

    @BeforeMethod
    public void before_test() {
        values = new HashMap<>();
    }

    @Test
    public void can_fill_single_integer_value_whole_string() {
        source = "{$gt2000}";
        values.put("gt2000", 80);
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("80"));
    }

    @Test
    public void can_fill_single_datetime_value_whole_string() {
        source = "{$gt2000}";
        values.put("gt2000", DvDateTime.valueOf("2017-04-14T13:55:55"));
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("2017-04-14T13:55:55"));
    }

    @Test
    public void can_fill_single_string_value_whole_string() {
        source = "{$gt2000}";
        values.put("gt2000", "mg");
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("mg"));
    }

    @Test
    public void can_fill_single_double_value_whole_string() {
        source = "{$gt2000}";
        values.put("gt2000", 8.5);
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("8.5"));
    }

    @Test
    public void can_fill_single_integer_value_in_part_string() {
        source = "{$gt2000}mg daily";
        values.put("gt2000", 80);
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("80mg daily"));
    }

    @Test
    public void can_fill_single_double_value_in_part_string() {
        source = "{$gt2000}mg daily";
        values.put("gt2000", 8.5);
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("8.5mg daily"));
    }

    @Test
    public void can_fill_single_string_value_in_part_string() {
        source = "80{$gt2000} daily";
        values.put("gt2000", "mg");
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("80mg daily"));
    }

    @Test
    public void can_fill_double_and_string_values() {
        source = "{$gt1000}{$gt2000} daily";
        values.put("gt1000", 80);
        values.put("gt2000", "mg");
        assertThat(templateFiller.replaceVariablesWithValues(source, values), is("80mg daily"));
    }

    @Test
    public void can_traverse_map_fill_values() throws Exception {
        Map map = new Gson().fromJson(loadJson("medication_request_test"), Map.class);
        values.put("gt0000", "plan");
        values.put("gt0001", "http://www.whocc.no/atc");
        values.put("gt0002", "C10AA05");
        values.put("gt0003", "atorvastatin");
        values.put("gt0004", 1.0);
        values.put("gt0005", 7.0);
        values.put("gt0006", "mg");
        values.put("gt0007", 1.0);
        values.put("gt0008", 1.0);
        values.put("gt0009", "d");
        values.put("gt0010", "http://unitsofmeasure.org");
        templateFiller.traverseMapAndReplaceAllVariablesWithValues(map, values);
        Map expected = new Gson().fromJson(loadJson("medication_request_test_expected"), Map.class);
        assertEquals(map, expected);
    }

    @Test
    public void can_traverse_map_fill_datetime_values() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Map map = new Gson().fromJson(loadJson("appointment_test"), Map.class);
        values.put("gt2000", dateFormat.parse("2013-04-20"));
        values.put("gt2001", dateFormat.parse("2013-04-25"));
        templateFiller.traverseMapAndReplaceAllVariablesWithValues(map, values);
        Map expected = new Gson().fromJson(loadJson("appointment_test_expected"), Map.class);
        assertEquals(map, expected);
    }
}

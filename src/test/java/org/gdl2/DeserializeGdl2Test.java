package org.gdl2;

import org.apache.commons.io.IOUtils;
import org.gdl2.model.Guideline;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DeserializeGdl2Test {
    private Guideline guideline;

    @BeforeMethod
    public void setUp() throws Exception {
        guideline = Gdl2.fromGdl2(loadJson("BSA_Calculation.v1.gdl2"));
    }

    @Test
    public void can_parse_guideline_id_and_description() throws Exception {
        assertThat(guideline.getId(), is("BSA_Calculation.v1"));
        assertThat(guideline.getConcept(), is("gt0015"));
        assertThat(guideline.getGdlVersion(), is("2.0"));
        assertThat(guideline.getLanguage().getOriginalLanguage().getCode(), is("en"));

        assertThat(guideline.getDescription().getDetails().get("en").getCopyright(), is("cds"));
        assertThat(guideline.getDescription().getDetails().get("en").getKeywords().contains("body surface area"), is(true));
        assertThat(guideline.getDescription().getOriginalAuthor().get("date"), is("2013-03-11"));
        assertThat(guideline.getDescription().getOriginalAuthor().get("organisation"), is("CDS"));
    }

    @Test
    public void can_parse_guideline_pre_conditions() throws Exception {
        assertThat(guideline.getDefinition().getPreConditions().size(), is(2));
        assertThat(guideline.getDefinition().getPreConditions().get(0).toString(),
                is("$gt0005.units=='kg'"));
    }

    @Test
    public void can_parse_guideline_default_actions() throws Exception {
        assertThat(guideline.getDefinition().getDefaultActions().size(), is(1));
        assertThat(guideline.getDefinition().getDefaultActions().get(0).toString(),
                is("$gt0004.units='kg/m2'"));
    }

    @Test
    public void can_parse_guideline_data_bindings() throws Exception {
        assertThat(guideline.getDefinition().getDataBindings().get("gt0016").getElements().get("gt0005").getPath(),
                is("/data[at0002]/events[at0003]/data[at0001]/items[at0004]"));
        assertThat(guideline.getDefinition().getDataBindings().get("gt0016").getPredicates().get(0).toString(),
                is("max(/data/events/time)"));
    }

    @Test
    public void can_parse_guideline_rules() throws Exception {
        assertThat(guideline.getDefinition().getRules().size(), is(3));
        assertThat(guideline.getDefinition().getRules().get("gt0009").getPriority(), is(3));
        assertThat(guideline.getDefinition().getRules().get("gt0009").getWhen().size(), is(2));
        assertThat(guideline.getDefinition().getRules().get("gt0009").getWhen().get(0).toString(),
                is("$gt0005.units=='kg'"));
        assertThat(guideline.getDefinition().getRules().get("gt0009").getThen().size(), is(3));
        assertThat(guideline.getDefinition().getRules().get("gt0009").getThen().get(0).toString(),
                is("$gt0013.magnitude=((($gt0005.magnitude*$gt0006.magnitude)/3600)^0.5)"));
    }

    @Test
    public void can_parse_guideline_term_definitions() throws Exception {
        assertThat(guideline.getOntology().getTermDefinitions().size(), is(1));
        assertThat(guideline.getOntology().getTermDefinitions().get("en").getTerms().size(), is(8));
        assertThat(guideline.getOntology().getTermDefinitions().get("en").getTermText("gt0005"),
                is("Weight"));
    }

    private String loadJson(String name) throws Exception {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream((name + ".json")), "UTF-8");
    }
}

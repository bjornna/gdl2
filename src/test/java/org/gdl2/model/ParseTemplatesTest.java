package org.gdl2.model;

import org.gdl2.runtime.TestCommon;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

public class ParseTemplatesTest extends TestCommon {

    @Test
    public void can_parse_object_template_definition() throws Exception {
        Guideline guideline = loadGuideline("create_using_template_single_obj_test.v0.1.gdl2");
        Template template = guideline.getDefinition().getTemplates().get("gt0022");
        assertNotNull(template);
        assertEquals("org.gdl2.datatypes.DvOrdinal", template.getModelId());
    }
}

package org.gdl2.runtime;

import org.apache.commons.io.IOUtils;
import org.gdl2.Gdl2;
import org.gdl2.datatypes.DataValue;
import org.gdl2.datatypes.DvDateTime;
import org.gdl2.datatypes.DvQuantity;
import org.gdl2.expression.AssignmentExpression;
import org.gdl2.expression.ExpressionItem;
import org.gdl2.expression.parser.ExpressionParser;
import org.gdl2.model.Guideline;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TestCommon {
    private static final String WEIGHT_ARCHETYPE = "openEHR-EHR-OBSERVATION.body_weight.v1";
    private static final String WEIGHT_VALUE = "/data[at0002]/events[at0003]/data[at0001]/items[at0004]";
    private static final String WEIGHT_EVENT_TIME = "/data/events/time";
    private static final String HEIGHT_ARCHETYPE = "openEHR-EHR-OBSERVATION.height.v1";
    private static final String HEIGHT_VALUE = "/data[at0001]/events[at0002]/data[at0003]/items[at0004]";
    private static final String HEIGHT_EVENT_TIME = "/data/events/time";

    static final String BSA_CALCULATION = "BSA_Calculation_test.v1.gdl2";
    static final String BSA_CALCULATION_WITHOUT_WHEN = "BSA_Calculation_without_when_test.v1.gdl2";

    DataInstance toWeight(String value) {
        return new DataInstance.Builder().modelId(WEIGHT_ARCHETYPE)
                .addValue(WEIGHT_VALUE, DvQuantity.valueOf(value))
                .addValue(WEIGHT_EVENT_TIME, DvDateTime.valueOf("2012-01-01T00:00:00"))
                .build();
    }

    DataInstance toHeight(String value) {
        return new DataInstance.Builder().modelId(HEIGHT_ARCHETYPE)
                .addValue(HEIGHT_VALUE, DvQuantity.valueOf(value))
                .addValue(HEIGHT_EVENT_TIME, DvDateTime.valueOf("2012-01-01T00:00:00"))
                .build();
    }

    public Guideline loadGuideline(String name) throws Exception {
        return Gdl2.fromGdl2(loadJson(name));
    }

    ExpressionItem parseExpression(String expression) {
        try {
            return new ExpressionParser(new StringReader(expression)).parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    AssignmentExpression parseAssignmentExpression(String expression) throws Exception {
        return (AssignmentExpression) parseExpression(expression);
    }

    private String loadJson(String name) throws Exception {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream((name + ".json")), "UTF-8");
    }

    List<DataValue> asList(DataValue dataValue) {
        List<DataValue> list = new ArrayList<>();
        list.add(dataValue);
        return list;
    }
}

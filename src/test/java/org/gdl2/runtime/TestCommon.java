package org.gdl2.runtime;

import org.apache.commons.io.IOUtils;
import org.gdl2.Gdl2;
import org.gdl2.datatypes.DataValue;
import org.gdl2.datatypes.DvCodedText;
import org.gdl2.datatypes.DvDateTime;
import org.gdl2.datatypes.DvQuantity;
import org.gdl2.expression.AssignmentExpression;
import org.gdl2.expression.ExpressionItem;
import org.gdl2.expression.ExpressionParser;
import org.gdl2.model.Guideline;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.fail;

public class TestCommon {
    static final String NULL_STRING = "NULL";
    static final String WEIGHT_ARCHETYPE = "openEHR-EHR-OBSERVATION.body_weight.v1";
    static final String WEIGHT_VALUE = "/data[at0002]/events[at0003]/data[at0001]/items[at0004]";
    static final String WEIGHT_EVENT_TIME = "/data/events/time";

    static final String HEIGHT_ARCHETYPE = "openEHR-EHR-OBSERVATION.height.v1";
    static final String HEIGHT_VALUE = "/data[at0001]/events[at0002]/data[at0003]/items[at0004]";
    static final String HEIGHT_EVENT_TIME = "/data/events/time";

    static final String MEDICATION_ARCHETYPE = "openEHR-EHR-INSTRUCTION.medication.v1";
    static final String MEDICATION_GENERIC_NAME = "/activities[at0001]/description[openEHR-EHR-ITEM_TREE.medication.v1]/items[at0012]";
    static final String MEDICATION_FIRST_ADMINISTRATION_DATE = "/activities[at0001]/description[openEHR-EHR-ITEM_TREE.medication.v1]/items[at0018]/items[at0019]";
    static final String MEDICATION_LAST_ADMINISTRATION_DATE = "/activities[at0001]/description[openEHR-EHR-ITEM_TREE.medication.v1]/items[at0018]/items[at0032]";

    static final String BSA_CALCULATION = "BSA_Calculation_test.v1.gdl2";
    static final String BSA_CALCULATION_WITHOUT_WHEN = "BSA_Calculation_without_when_test.v1.gdl2";

    DataInstance toWeight(String value, String eventTime) {
        return new DataInstance.Builder().archetypeId(WEIGHT_ARCHETYPE)
                .addValue(WEIGHT_VALUE, DvQuantity.valueOf(value))
                .addValue(WEIGHT_EVENT_TIME, DvDateTime.valueOf(eventTime))
                .build();
    }

    DataInstance toHeight(String value, String eventTime) {
        return new DataInstance.Builder().archetypeId(HEIGHT_ARCHETYPE)
                .addValue(HEIGHT_VALUE, DvQuantity.valueOf(value))
                .addValue(HEIGHT_EVENT_TIME, DvDateTime.valueOf(eventTime))
                .build();
    }

    DataInstance toMedicationWithoutEndDate(String startDatetime, String genericName) {
        return toMedication(startDatetime, null, genericName);
    }

    DataInstance toMedication(String startDatetime, String endDatetime, String genericName) {
        return new DataInstance.Builder()
                .archetypeId(MEDICATION_ARCHETYPE)
                .addValue(MEDICATION_FIRST_ADMINISTRATION_DATE, toDvDateTime(startDatetime))
                .addValue(MEDICATION_LAST_ADMINISTRATION_DATE, toDvDateTime(endDatetime))
                .addValue(MEDICATION_GENERIC_NAME, DvCodedText.valueOf(genericName))
                .build();
    }

    private DvDateTime toDvDateTime(String value) {
        if (value == null || value.isEmpty() || NULL_STRING.equals(value)) {
            return null;
        }
        return DvDateTime.valueOf(value);
    }

    Guideline loadGuideline(String name) throws Exception {
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

    String loadJson(String name) throws Exception {
        return IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream((name + ".json")), "UTF-8");
    }

    List<DataValue> asList(DataValue dataValue) {
        List<DataValue> list = new ArrayList<>();
        list.add(dataValue);
        return list;
    }
}

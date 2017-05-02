package org.gdl2.runtime;

import org.gdl2.datatypes.*;
import org.gdl2.expression.*;
import org.gdl2.model.Guideline;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PredicateTest extends TestCommon {
    private String archetypeId;
    private Interpreter interpreter;
    private Guideline guideline;
    private final String CURRENT_DATETIME = "currentDateTime";

    @BeforeMethod
    public void setUp() {
        archetypeId = "archetype";
        interpreter = new Interpreter();
    }

    @Test
    public void can_evaluate_predicate_with_max_function_expected_one_result() throws Exception {
        // predicates = <"max(/data[at0001]/items[at0004])",...>
        String path = "/data[at0001]/items[at0004]";
        ExpressionItem predicate = new UnaryExpression(
                new Variable(null, "name", path, null), OperatorKind.MAX);
        DataInstance[] dataInstances = new DataInstance[4];
        dataInstances[0] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(1))
                .addValue(("/data[at0001]/items[at0004]"), DvDateTime.valueOf("2010-01-01T00:00:00"))
                .build();
        dataInstances[1] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(2))
                .addValue(("/data[at0001]/items[at0004]"), DvDateTime.valueOf("2012-01-01T00:00:00"))
                .build();
        dataInstances[2] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(8))
                .addValue(("/data[at0001]/items[at0004]"), DvDateTime.valueOf("2015-10-01T00:00:00"))
                .build();
        dataInstances[3] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(5))
                .addValue(("/data[at0001]/items[at0004]"), DvDateTime.valueOf("2013-01-01T00:00:00"))
                .build();
        List<DataInstance> result = interpreter.evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances),
                predicate, null, null);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getDvCount("gt0011").getMagnitude(), is(8));
    }

    @Test(enabled = false)
    public void can_evaluate_predicate_to_get_second_max() throws Exception {
        String predicateExpression = "/data/events/time/value/value<=(max(/data/events/time))";
        String path = "/data/events/time";
        String key = "";
        ExpressionItem predicate = parseExpression(predicateExpression);
        List<DataInstance> dataInstanceList = new ArrayList<>();
        dataInstanceList.add(new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(key, DvCount.valueOf(1))
                .addValue((path), DvDateTime.valueOf("2010-01-01T00:00:00"))
                .build());
        dataInstanceList.add(new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(key, DvCount.valueOf(2))
                .addValue((path), DvDateTime.valueOf("2012-01-01T00:00:00"))
                .build());
        dataInstanceList.add(new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(key, DvCount.valueOf(8))
                .addValue((path), DvDateTime.valueOf("2015-10-01T00:00:00"))
                .build());
        dataInstanceList.add(new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(key, DvCount.valueOf(5))
                .addValue((path), DvDateTime.valueOf("2013-01-01T00:00:00"))
                .build());
        List<DataInstance> result = interpreter.evaluateDataInstancesWithPredicate(dataInstanceList, predicate, null, null);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getDvCount("gt0011").getMagnitude(), is(5));
    }

    @Test
    public void can_evaluate_predicate_with_min_function_expected_one_result() throws Exception {
        // predicates = <"max(/data[at0001]/items[at0004])",...>
        String path = "/data[at0001]/items[at0004]";
        ExpressionItem predicate = new UnaryExpression(
                new Variable(null, "name", path, null), OperatorKind.MIN);
        DataInstance[] dataInstances = new DataInstance[3];
        dataInstances[0] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(1))
                .addValue(path, DvDateTime.valueOf("2010-01-01T00:00:00"))
                .build();
        dataInstances[1] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(2))
                .addValue(path, DvDateTime.valueOf("2012-01-01T00:00:00"))
                .build();
        dataInstances[2] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(8))
                .addValue(path, DvDateTime.valueOf("2015-10-01T00:00:00"))
                .build();
        List<DataInstance> result = interpreter.evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances),
                predicate, null, null);
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getDvCount("gt0011").getMagnitude(), is(1));
    }

    @Test
    public void can_evaluate_predicate_with_is_a_local_term_bindings() throws Exception {
        // predicates = <"/data[at0001]/items[at0002.1] is_a local::gt0101|Hypertension|",...>
        String path = "/data[at0001]/items[at0002.1]";
        guideline = loadGuideline("CHA2DS2VASc_diagnosis_review.v1.0.1.gdl2");
        ExpressionItem predicate = new BinaryExpression(new Variable("code", "name", path, "attribute"),
                new CodedTextConstant("Hypertension", new CodePhrase("local", "gt0101")), OperatorKind.IS_A);
        DataInstance[] dataInstances = new DataInstance[5];
        dataInstances[0] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(1))
                .addValue(path, new DvCodedText("Hypertension", "ICD10", "I10"))
                .build();
        dataInstances[1] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(3))
                .addValue(path, new DvCodedText("Diabetes Type-1", "ICD10", "E10"))
                .build();
        dataInstances[2] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(5))
                .addValue(path, new DvCodedText("Hypertension", "ICD10", "I11"))
                .build();
        dataInstances[3] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(7))
                .build();
        dataInstances[4] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue("gt0011", new DvCount(9))
                .addValue(path, new DvCodedText("Hypertension", "ICD10", "I11"))
                .build();
        List<DataInstance> result = interpreter.evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances),
                predicate, guideline.getOntology(), null);
        assertThat(result.size(), is(3));
        assertThat(result.get(0).getDvCount("gt0011").getMagnitude(), is(1));
        assertThat(result.get(1).getDvCount("gt0011").getMagnitude(), is(5));
        assertThat(result.get(2).getDvCount("gt0011").getMagnitude(), is(9));
        assertThat(result.get(0).getDvCodedText(path).getDefiningCode().getCode(), is("I10"));
        assertThat(result.get(1).getDvCodedText(path).getDefiningCode().getCode(), is("I11"));
        assertThat(result.get(2).getDvCodedText(path).getDefiningCode().getCode(), is("I11"));
    }

    @Test
    public void can_evaluate_predicate_with_is_a_local_term_bindings_and_max_timestamp() throws Exception {
        // "/data[at0001]/items[at0002.1] is_a local::gt0043|Vascular disease diagnosis code|", "max(/data[at0001]/items[at0003])"
        String countCode = "gt0011";
        String codePath = "/data[at0001]/items[at0002.1]";
        String timestampPath = "/data[at0001]/items[at0003]";
        guideline = loadGuideline("Stroke_prevention_dashboard_case.v1.gdl2");
        List<ExpressionItem> predicates = guideline.getDefinition().getDataBindings().get("gt0059").getPredicates();

        DataInstance[] dataInstances = new DataInstance[5];
        dataInstances[0] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(countCode, new DvCount(1))
                .addValue(codePath, new DvCodedText("Hypertension", "ICD10", "I10"))
                .addValue(timestampPath, DvDateTime.valueOf("2012-01-01T00:00:00"))
                .build();
        dataInstances[1] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(countCode, new DvCount(3))
                .addValue(codePath, new DvCodedText("Other aneurysm", "ICD10", "I72"))        // right code
                .addValue(timestampPath, DvDateTime.valueOf("2011-01-01T00:00:00"))
                .build();
        dataInstances[2] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(countCode, new DvCount(5))
                .addValue(codePath, new DvCodedText("Hypertension", "ICD10", "I11"))
                .addValue(timestampPath, DvDateTime.valueOf("2010-01-01T00:00:00"))
                .build();
        dataInstances[3] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(countCode, new DvCount(7))
                .addValue(codePath, new DvCodedText("Hypertension", "ICD10", "I11"))             // wrong code
                .addValue(timestampPath, DvDateTime.valueOf("2014-01-01T00:00:00"))             // max date
                .build();
        dataInstances[4] = new DataInstance.Builder()
                .modelId(archetypeId)
                .addValue(countCode, new DvCount(9))
                .addValue(codePath, new DvCodedText("myocardial infarction", "ICD10", "I21"))    // right code
                .addValue(timestampPath, DvDateTime.valueOf("2013-01-01T00:00:00"))
                .build();
        List<DataInstance> result = interpreter.evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances),
                predicates, guideline.getOntology());
        assertThat(result.size(), is(1));
        assertThat(result.get(0).getDvCount(countCode).getMagnitude(), is(9));
        assertThat(result.get(0).getDvCodedText(codePath).getDefiningCode().getCode(), is("I21"));
    }

    @Test
    public void can_run_empty_list_without_right_input_on_max_predicate() throws Exception {
        DataInstance[] dataInstances = new DataInstance[1];
        dataInstances[0] = new DataInstance.Builder()
                .modelId("archetype")
                .addValue("count", new DvCount(1))
                .build();
        String path = "/data[at0001]/items[at0004]";
        ExpressionItem predicate = new UnaryExpression(
                new Variable(null, "name", path, null), OperatorKind.MAX);
        List<DataInstance> result = interpreter
                .evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances), predicate, null, null);
        assertThat(result.size(), Matchers.is(0));
    }

    @Test
    public void can_run_empty_list_without_right_input_on_min_predicate() throws Exception {
        DataInstance[] dataInstances = new DataInstance[1];
        dataInstances[0] = new DataInstance.Builder()
                .modelId("archetype")
                .addValue("count", new DvCount(1))
                .build();
        String path = "/data[at0001]/items[at0004]";
        ExpressionItem predicate = new UnaryExpression(
                new Variable(null, "name", path, null), OperatorKind.MIN);
        List<DataInstance> result = interpreter
                .evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances), predicate, null, null);
        assertThat(result.size(), Matchers.is(0));
    }

    @Test
    public void can_evaluate_observation_datetime_against_current_datetime_minus_12_month() throws Exception {
        // /data/events/time/value/value>=($currentDateTime.value-12,mo)
        DataInstance[] dataInstances = new DataInstance[1];
        dataInstances[0] = new DataInstance.Builder()
                .modelId("weight")
                .addValue("/data/events/time", DvDateTime.valueOf("2014-02-15T18:18:00"))
                .build();
        Map<String, DataValue> parameters = new HashMap<>();
        parameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2015-01-10T00:00:00"));
        interpreter = new Interpreter(parameters);
        BinaryExpression binaryExpression = new BinaryExpression(
                new Variable(CURRENT_DATETIME, null, null, "value"),
                new QuantityConstant(new DvQuantity("mo", 12.0, 0)), OperatorKind.SUBTRACTION);
        BinaryExpression predicate = new BinaryExpression(Variable.createByPath("/data/events/time/value/value"),
                binaryExpression, OperatorKind.GREATER_THAN_OR_EQUAL);
        List<DataInstance> result = interpreter
                .evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances), predicate, null, null);
        assertThat(result.size(), Matchers.is(1));
        assertThat(result.get(0).modelId(), is("weight"));
    }

    @Test
    public void can_evaluate_pathed_datetime_against_current_datetime_minus_12_month() throws Exception {
        // /data[at0001]/items[at0003]/value/value>=($currentDateTime.value-12,mo)
        DataInstance[] dataInstances = new DataInstance[1];
        dataInstances[0] = new DataInstance.Builder()
                .modelId("weight")
                .addValue("/data[at0001]/items[at0003]", DvDateTime.valueOf("2014-02-15T18:18:00"))
                .build();
        Map<String, DataValue> parameters = new HashMap<>();
        parameters.put(CURRENT_DATETIME, DvDateTime.valueOf("2015-01-10T00:00:00"));
        interpreter = new Interpreter(parameters);
        BinaryExpression binaryExpression = new BinaryExpression(
                new Variable(CURRENT_DATETIME, null, null, "value"),
                new QuantityConstant(new DvQuantity("mo", 12.0, 0)), OperatorKind.SUBTRACTION);
        BinaryExpression predicate = new BinaryExpression(Variable.createByPath("/data[at0001]/items[at0003]/value/value"),
                binaryExpression, OperatorKind.GREATER_THAN_OR_EQUAL);
        List<DataInstance> result = interpreter
                .evaluateDataInstancesWithPredicate(Arrays.asList(dataInstances), predicate, null, null);
        assertThat(result.size(), Matchers.is(1));
        assertThat(result.get(0).modelId(), is("weight"));
    }
}
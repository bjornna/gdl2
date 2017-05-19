package org.gdl2.runtime;

import lombok.NonNull;
import org.gdl2.datatypes.*;
import org.gdl2.expression.*;
import org.gdl2.model.*;
import org.gdl2.terminology.Binding;
import org.gdl2.terminology.TermBinding;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.gdl2.expression.OperatorKind.*;

/**
 * Java interpreter of GDL.
 */
public class Interpreter {
    public static final String CURRENT_DATETIME = "currentDateTime";
    public static final String OBJECT_CREATOR = "objectCreator";
    private static final String COUNT = "count";
    private static final String SUM = "sum";

    private static final long HOUR_IN_MILLISECONDS = 3600 * 1000L;
    private Map<String, Object> systemParameters;
    private ObjectCreatorPlugin objectCreatorPlugin;
    private TemplateFiller templateFiller;

    public Interpreter() {
        this.systemParameters = new HashMap<>();
        this.systemParameters.put(CURRENT_DATETIME, new DvDateTime());
        this.objectCreatorPlugin = new DefaultObjectCreator();
        this.templateFiller = new TemplateFiller();
    }

    public Interpreter(Map<String, Object> systemParameters) {
        this();
        assertNotNull(systemParameters, "systemParameters can not be null");
        this.systemParameters.putAll(systemParameters);
        Object objectCreator = systemParameters.get(OBJECT_CREATOR);
        if (objectCreator instanceof ObjectCreatorPlugin) {
            this.objectCreatorPlugin = (ObjectCreatorPlugin) systemParameters.get(OBJECT_CREATOR);
        } else {
            this.objectCreatorPlugin = new DefaultObjectCreator();
        }
    }

    private static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    // TODO sort guides according to dependency
    public List<DataInstance> executeGuidelines(List<Guideline> guidelines, List<DataInstance> inputDataInstances) {
        assertNotNull(guidelines, "List<Guideline> cannot be null.");
        assertNotNull(inputDataInstances, "List<DataInstance> cannot be null.");
        Map<String, DataInstance> allResults = new HashMap<>();
        List<DataInstance> input = new ArrayList<>(inputDataInstances);
        List<DataInstance> totalResult = new ArrayList<>();
        for (Guideline guide : guidelines) {
            List<DataInstance> resultPerExecution = executeSingleGuideline(guide, input);
            for (DataInstance dataInstance : resultPerExecution) {
                DataInstance existing = allResults.get(dataInstance.modelId());
                if (existing == null) {
                    allResults.put(dataInstance.modelId(), dataInstance);
                } else {
                    existing.merge(dataInstance);
                }
            }
            input = new ArrayList<>(inputDataInstances);
            input.addAll(allResults.values());
            totalResult.addAll(resultPerExecution);
        }
        return totalResult;
    }

    public List<DataInstance> executeSingleGuideline(Guideline guide, List<DataInstance> dataInstances) {
        Map<String, Object> resultMap = execute(guide, dataInstances);
        return collectDataInstancesFromValueMap(resultMap, guide.getDefinition());
    }

    private Set<String> getCodesForAssignableVariables(GuideDefinition guideDefinition) {
        Set<String> codesFromAssignments = guideDefinition.getRules().entrySet().stream()
                .flatMap(entry -> entry.getValue().getThen().stream())
                .filter(s -> !(s instanceof CreateInstanceExpression))
                .filter(s -> !(s instanceof UseTemplateExpression))
                .map(assignmentExpression -> ((AssignmentExpression) assignmentExpression).getVariable().getCode())
                .collect(Collectors.toSet());
        Set<String> codesFromCreateStatements = guideDefinition.getRules().entrySet().stream()
                .flatMap(entry -> entry.getValue().getThen().stream())
                .filter(s -> s instanceof CreateInstanceExpression)
                .flatMap(createInstanceExpression -> ((CreateInstanceExpression) createInstanceExpression).getAssignmentExpressions().stream())
                .map(assignmentExpression -> assignmentExpression.getVariable().getCode())
                .collect(Collectors.toSet());
        codesFromAssignments.addAll(codesFromCreateStatements);
        return codesFromAssignments;
    }

    Map<String, Object> execute(Guideline guide, List<DataInstance> dataInstances) {
        assertNotNull(guide, "Guideline cannot not be null.");
        assertNotNull(dataInstances, "List<DataInstance> cannot be null.");

        Map<String, List<Object>> inputValues = selectDataInstancesUsingPredicatesAndSortWithElementBindingCode(
                dataInstances, guide);
        Map<String, Object> resultDefaultRuleExecution = new HashMap<>();
        Map<String, Class> typeMap = new HashMap<>();
        Set<String> firedRules = new HashSet<>();
        boolean allPreconditionsAreTrue = true;
        if (guide.getDefinition().getPreConditions() != null) {
            allPreconditionsAreTrue = guide.getDefinition().getPreConditions().stream()
                    .allMatch(expressionItem -> evaluateBooleanExpression(expressionItem, inputValues, guide.getOntology(), null));
        }
        if (!allPreconditionsAreTrue) {
            return collectValueListMap(inputValues);
        }
        if (guide.getDefinition().getDefaultActions() != null) {
            for (ExpressionItem assignmentExpression : guide.getDefinition().getDefaultActions()) {
                performAssignmentStatements((AssignmentExpression) assignmentExpression, inputValues, typeMap, resultDefaultRuleExecution);
                mergeValueMapIntoListValueMap(resultDefaultRuleExecution, inputValues);
            }
        }
        List<Rule> sortedRules = sortRulesByPriority(guide.getDefinition().getRules().values());

        Map<String, List<Object>> inputAndResult = new HashMap<>(inputValues);
        for (Rule rule : sortedRules) {
            Map<String, Object> resultPerRuleExecution = evaluateRuleWithPossibleMultipleValues(
                    rule,
                    inputValues,
                    inputAndResult,
                    guide.getDefinition().getTemplates(),
                    guide.getOntology(),
                    firedRules);
            mergeValueMapIntoListValueMap(resultPerRuleExecution, inputAndResult);
        }
        return collectValueListMap(inputAndResult);
    }

    private Map<String, Object> collectValueListMap(Map<String, List<Object>> valueListMap) {
        return valueListMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, s -> s.getValue().get(s.getValue().size() - 1)));
    }

    void mergeValueMapIntoListValueMap(Map<String, Object> valueMap, Map<String, List<Object>> valueListMap) {
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            valueListMap.computeIfAbsent(entry.getKey(), s -> new ArrayList<>()).add(entry.getValue());
        }
    }

    private List<DataInstance> collectDataInstancesFromValueMap(Map<String, Object> valueMap, GuideDefinition guideDefinition) {
        List<DataInstance> dataInstances = new ArrayList<>();
        Set<String> assignableCodes = getCodesForAssignableVariables(guideDefinition);
        for (DataBinding dataBinding : guideDefinition.getDataBindings().values()) {
            if (DataBinding.Type.INPUT.equals(dataBinding.getType())) {
                continue;
            }
            DataInstance dataInstance = new DataInstance.Builder().modelId(dataBinding.getModelId()).build();
            for (Map.Entry<String, Element> elementBindingEntry : dataBinding.getElements().entrySet()) {
                String elementId = elementBindingEntry.getValue().getId();
                String elementPath = elementBindingEntry.getValue().getPath();
                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                    String valueKey = entry.getKey();
                    if (assignableCodes.contains(valueKey) && elementId.equals(valueKey)) {
                        dataInstance.setValue(elementPath, entry.getValue());
                    }
                }
            }
            if (dataInstance.values().size() != 0) {
                dataInstances.add(dataInstance);
            }
        }
        if (guideDefinition.getTemplates() != null) {
            for (Map.Entry<String, Template> entry : guideDefinition.getTemplates().entrySet()) {
                Template template = entry.getValue();
                if (valueMap.containsKey(template.getId())) {
                    dataInstances.add(new DataInstance.Builder()
                            .modelId(template.getModelId())
                            .addValue("/", valueMap.get(template.getId()))
                            .build());
                }
            }
        }
        return dataInstances;
    }

    private Map<String, List<Object>> selectDataInstancesUsingPredicatesAndSortWithElementBindingCode(
            List<DataInstance> dataInstances, Guideline guide) {
        Map<String, List<Object>> valueListMap = new HashMap<>();
        for (Map.Entry<String, DataBinding> entry : guide.getDefinition().getDataBindings().entrySet()) {
            DataBinding dataBinding = entry.getValue();
            List<DataInstance> selectedDataInstances =
                    evaluateDataInstancesWithPredicate(
                            filterDataInstancesWithModelId(dataInstances, dataBinding.getModelId()),
                            dataBinding.getPredicates(),
                            guide.getOntology());
            convertDataInstancesToCodeBasedValueMap(dataBinding, selectedDataInstances, valueListMap);
        }
        return valueListMap;
    }

    private void convertDataInstancesToCodeBasedValueMap(DataBinding dataBinding,
                                                         List<DataInstance> dataInstances,
                                                         Map<String, List<Object>> valueListMap) {
        Map<String, String> pathToCode = pathToCode(dataBinding);
        dataInstances.stream()
                .flatMap(s -> s.values().entrySet().stream())
                .filter(s -> pathToCode.containsKey(s.getKey()))
                .forEach(s -> valueListMap
                        .computeIfAbsent(pathToCode.get(s.getKey()), key -> new ArrayList<>())
                        .add(s.getValue()));
    }

    private Map<String, String> pathToCode(DataBinding dataBinding) {
        return dataBinding.getElements().entrySet().stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toMap(Element::getPath, Element::getId));
    }

    private List<Rule> sortRulesByPriority(Collection<Rule> rules) {
        return rules.stream().sorted(new RuleComparator()).collect(Collectors.toList());
    }

    private Map<String, Object> evaluateRuleWithPossibleMultipleValues(Rule rule, Map<String, List<Object>> originalInput,
                                                                       Map<String, List<Object>> inputAndResult,
                                                                       Map<String, Template> templateMap,
                                                                       GuideOntology guideOntology, Set<String> firedRules) {
        for (Map.Entry<String, List<Object>> entry : originalInput.entrySet()) {
            List<Object> list = entry.getValue();
            List<Object> listForLoop = new ArrayList<>(list);
            if (list.size() == 1) {
                continue;
            }
            for (Object dataValue : listForLoop) {
                entry.setValue(Collections.singletonList(dataValue));
                Map<String, Object> result = evaluateRuleWithPossibleMultipleValues(rule, originalInput, inputAndResult,
                        templateMap, guideOntology, firedRules);
                mergeValueMapIntoListValueMap(result, inputAndResult);
            }
        }
        return evaluateRuleWithSingleValueForEachInputVariable(rule, inputAndResult, templateMap, guideOntology, firedRules);
    }

    private Map<String, Object> evaluateRuleWithSingleValueForEachInputVariable(Rule rule, Map<String, List<Object>> input,
                                                                                Map<String, Template> templateMap,
                                                                                GuideOntology guideOntology, Set<String> firedRules) {
        Map<String, Object> result = new HashMap<>();
        boolean allWhenStatementsAreTrue = rule.getWhen() == null || rule.getWhen().stream()
                .allMatch(whenStatement -> evaluateBooleanExpression(whenStatement, input, guideOntology, firedRules));
        if (!allWhenStatementsAreTrue) {
            return result;
        }
        Map<String, Class> typeMap = typeBindingThroughAssignmentStatements(rule.getThen());
        for (ExpressionItem thenStatement : rule.getThen()) {
            if (thenStatement instanceof AssignmentExpression) {
                performAssignmentStatements((AssignmentExpression) thenStatement, input, typeMap, result);
            } else if (thenStatement instanceof UseTemplateExpression) {
                performUseTemplateStatement((UseTemplateExpression) thenStatement, templateMap, input, result);
            }
            mergeValueMapIntoListValueMap(result, input);
        }
        firedRules.add(rule.getId());
        return result;
    }

    // mainly to resolve ambiguity between DvCount and DvQuantity
    Map<String, Class> typeBindingThroughAssignmentStatements(List<ExpressionItem> assignmentExpressions) {
        Map<String, Set<String>> attributesMap = new HashMap<>();
        for (ExpressionItem expressionItem : assignmentExpressions) {
            if (expressionItem instanceof AssignmentExpression) {
                Variable variable = ((AssignmentExpression) expressionItem).getVariable();
                attributesMap
                        .computeIfAbsent(variable.getCode(), key -> new HashSet<>())
                        .add(variable.getAttribute());
            }
        }

        TypeBinding typeBinding = new TypeBinding();
        return attributesMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, s -> typeBinding.possibleType(s.getValue())));
    }

    void performAssignmentStatements(AssignmentExpression assignmentExpression, Map<String, List<Object>> input,
                                     Map<String, Class> typeMap, Map<String, Object> result) {
        Variable variable = assignmentExpression.getVariable();
        String attribute = variable.getAttribute();
        if (assignmentExpression instanceof CreateInstanceExpression) {
            evaluateCreateInstanceExpression(assignmentExpression, input, typeMap, result);
            return;
        }
        Object value;
        if (assignmentExpression.getAssignment() instanceof QuantityConstant) {
            value = ((QuantityConstant) assignmentExpression.getAssignment()).getQuantity();
        } else {
            value = evaluateExpressionItem(assignmentExpression.getAssignment(), input);
        }
        if (TypeBinding.PRECISION.equals(attribute)) {
            DvQuantity dvQuantity = retrieveDvQuantityFromResultMapOrCreateNew(variable.getCode(), result);
            try {
                if (value instanceof String) {
                    DvQuantity newQuantity = new DvQuantity(dvQuantity.getUnits(), dvQuantity.getMagnitude(), Integer.parseInt((String) value));
                    result.put(variable.getCode(), newQuantity);
                } else if (value instanceof Integer) {
                    DvQuantity newQuantity = new DvQuantity(dvQuantity.getUnits(), dvQuantity.getMagnitude(), (Integer) value);
                    result.put(variable.getCode(), newQuantity);
                } else {
                    throw new IllegalArgumentException("Unexpected integer value: " + value + ", in assignmentExpression: " + assignmentExpression);
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Unexpected integer string value: " + value + ", in assignmentExpression: " + assignmentExpression);
            }
        } else if (TypeBinding.UNITS.equals(attribute)) {
            DvQuantity dvQuantity = retrieveDvQuantityFromResultMapOrCreateNew(variable.getCode(), result);
            if (value instanceof String) {
                DvQuantity newQuantity = new DvQuantity((String) value, dvQuantity.getMagnitude(), dvQuantity.getPrecision());
                result.put(variable.getCode(), newQuantity);
            } else {
                throw new IllegalArgumentException("Unexpected value for units: " + value + ", in assignmentExpression: " + assignmentExpression);
            }
        } else if (TypeBinding.MAGNITUDE.equals(attribute)) {
            performAssignMagnitudeAttribute(value, variable, assignmentExpression, input, typeMap, result);
        } else if (TypeBinding.NULL_FLAVOR.equals(attribute)) {
            if (value instanceof DvCodedText) {
                result.put(variable.getCode() + "." + TypeBinding.NULL_FLAVOR, (DvCodedText) value);
            } else {
                throw new IllegalArgumentException("Unexpected value: " + value + ", in null_flavor assignmentExpression: " + assignmentExpression);
            }
        } else if (TypeBinding.VALUE.equals(attribute) && value instanceof String) {
            result.put(variable.getCode(), DvText.valueOf((String) value));
        } else if ("true".equalsIgnoreCase(value.toString()) || "false".equalsIgnoreCase(value.toString())) {
            result.put(variable.getCode(), DvBoolean.valueOf(value.toString()));
        } else {
            result.put(assignmentExpression.getVariable().getCode(), value);
        }
    }

    void performUseTemplateStatement(UseTemplateExpression useTemplateExpression, Map<String, Template> templateMap,
                                     Map<String, List<Object>> input, Map<String, Object> result) {
        Variable variable = useTemplateExpression.getVariable();
        String attribute = variable.getCode();
        Template template = templateMap.get(attribute);
        if (template == null) {
            return;
        }
        Map<String, Object> useTemplateLocalResult = new HashMap<>();
        for (AssignmentExpression assignmentExpression : useTemplateExpression.getAssignmentExpressions()) {
            Object value = evaluateExpressionItem(assignmentExpression.getAssignment(), input);
            useTemplateLocalResult.put(assignmentExpression.getVariable().getCode(), value);
        }
        Map<String, Object> localMapCopy = new HashMap<>(template.getObject());
        this.templateFiller.traverseMapAndReplaceAllVariablesWithValues(localMapCopy, useTemplateLocalResult);
        try {
            Object object = this.objectCreatorPlugin.create(template.getModelId(), localMapCopy);
            result.put(variable.getCode(), object);
        } catch (ClassNotFoundException cnf) {
            System.out.println("failed to create object using template(" + template.getModelId() + "), class not found..");
            cnf.printStackTrace();
        }
    }

    private void performAssignMagnitudeAttribute(Object value, Variable variable, AssignmentExpression assignmentExpression,
                                                 Map<String, List<Object>> input, Map<String, Class> typeMap, Map<String, Object> result) {
        Class type = typeMap.get(variable.getCode());
        DvQuantity dvQuantity = null;
        List<Object> valueList = input.get(variable.getCode());
        if (valueList != null && !valueList.isEmpty() && (valueList.get(valueList.size() - 1) instanceof DvQuantity)) {
            dvQuantity = (DvQuantity) valueList.get(valueList.size() - 1);
        }
        if (dvQuantity != null || DvQuantity.class.equals(type)) {
            if (dvQuantity == null) {
                dvQuantity = retrieveDvQuantityFromResultMapOrCreateNew(variable.getCode(), result);
            }
            try {
                Double magnitude;
                if (value instanceof Double) {
                    magnitude = (Double) value;
                } else if (value instanceof Long) {
                    magnitude = ((Long) value).doubleValue();
                    result.put(variable.getCode(), new DvQuantity(dvQuantity.getUnits(), ((Long) value).doubleValue(), dvQuantity.getPrecision()));
                } else if (value instanceof Integer) {
                    magnitude = ((Integer) value).doubleValue();
                    result.put(variable.getCode(), new DvQuantity(dvQuantity.getUnits(), ((Integer) value).doubleValue(), dvQuantity.getPrecision()));
                } else if (value instanceof String) {
                    magnitude = Double.parseDouble((String) value);
                } else {
                    throw new IllegalArgumentException("Unexpected double value: " + value + ", in assignmentExpression: " + assignmentExpression);
                }
                result.put(variable.getCode(), new DvQuantity(dvQuantity.getUnits(), magnitude, dvQuantity.getPrecision()));
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Unexpected integer string value: " + value + ", in assignmentExpression: " + assignmentExpression);
            }
        } else {
            try {
                if (value instanceof Double) {
                    int intValue = ((Double) value).intValue();
                    result.put(variable.getCode(), new DvCount(intValue));
                } else if (value instanceof Integer) {
                    int intValue = (Integer) value;
                    result.put(variable.getCode(), new DvCount(intValue));
                } else {
                    throw new IllegalArgumentException("Unexpected double value: " + value + ", in assignmentExpression: " + assignmentExpression);
                }
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Unexpected integer string value: " + value + ", in assignmentExpression: " + assignmentExpression);
            }
        }
    }

    private void evaluateCreateInstanceExpression(AssignmentExpression assignmentExpression, Map<String, List<Object>> input,
                                                  Map<String, Class> typeMap, Map<String, Object> result) {
        CreateInstanceExpression createInstanceExpression = (CreateInstanceExpression) assignmentExpression;
        List<AssignmentExpression> assignmentExpressions = createInstanceExpression.getAssignmentExpressions();
        for (AssignmentExpression expression : assignmentExpressions) {
            performAssignmentStatements(expression, input, typeMap, result);
            mergeValueMapIntoListValueMap(result, input);
        }
        result.putAll(result);
    }

    private DvQuantity retrieveDvQuantityFromResultMapOrCreateNew(String code, Map<String, Object> resultMap) {
        Object dataValue = resultMap.computeIfAbsent(code, k -> new DvQuantity(0));
        if (!(dataValue instanceof DvQuantity)) {
            throw new IllegalArgumentException("Expected DvQuantity but instead got: " + resultMap.get(code).getClass());
        }
        return (DvQuantity) dataValue;
    }

    Object evaluateExpressionItem(ExpressionItem expressionItem, Map<String, List<Object>> input) {
        return evaluateExpressionItem(expressionItem, input, null, null);
    }

    Object evaluateExpressionItem(ExpressionItem expressionItem, Map<String, List<Object>> input,
                                  GuideOntology guideOntology, Set<String> firedRules) {
        if (expressionItem instanceof ConstantExpression) {
            return evaluateConstantExpression(expressionItem);
        } else if (expressionItem instanceof Variable) {
            Variable variable = (Variable) expressionItem;
            return retrieveValueFromValueMap(variable, input);
        } else if (expressionItem instanceof BinaryExpression) {
            return processBinaryExpression(expressionItem, input, guideOntology, firedRules);
        } else if (expressionItem instanceof UnaryExpression) {
            return processUnaryExpression((UnaryExpression) expressionItem, firedRules);
        } else if (expressionItem instanceof FunctionalExpression) {
            return processFunctionalExpression((FunctionalExpression) expressionItem, input, guideOntology, firedRules);
        } else {
            throw new IllegalArgumentException("Unsupported expressionItem: " + expressionItem);
        }
    }

    private Object evaluateConstantExpression(ExpressionItem expressionItem) {
        if (expressionItem instanceof DoubleConstant) {
            return ((DoubleConstant) expressionItem).getDoubleValue();
        } else if (expressionItem instanceof IntegerConstant) {
            return ((IntegerConstant) expressionItem).getIntegerValue();
        } else if (expressionItem instanceof CodedTextConstant) {
            return ((CodedTextConstant) expressionItem).getCodedText();
        } else if (expressionItem instanceof OrdinalConstant) {
            return ((OrdinalConstant) expressionItem).getOrdinal();
        } else if (expressionItem instanceof QuantityConstant) {
            return evaluateQuantityValue(((QuantityConstant) expressionItem).getQuantity());
        } else if (expressionItem instanceof DateTimeConstant) {
            return DvDateTime.valueOf(((DateTimeConstant) expressionItem).getValue());
        }
        ConstantExpression constantExpression = (ConstantExpression) expressionItem;
        String value = constantExpression.getValue();
        if ("null".equalsIgnoreCase(value)) {
            return null;
        } else if (value.startsWith("(-") && value.endsWith(")")) {
            int length = value.length();
            return value.substring(1, length - 1);
        } else {
            return value;
        }
    }

    private Object evaluateQuantityValue(@NonNull DvQuantity dvQuantity) {
        if (isTimePeriodUnits(dvQuantity.getUnits())) {
            return convertTimeQuantityToPeriodOrMilliSeconds(dvQuantity);
        }
        return dvQuantity.getMagnitude();
    }

    private boolean isTimePeriodUnits(String unit) {
        return "a".equals(unit) || "mo".equals(unit) || "d".equals(unit) || "h".equals(unit);
    }

    private Object convertTimeQuantityToPeriodOrMilliSeconds(DvQuantity dvQuantity) {
        int magnitude = Double.valueOf(dvQuantity.getMagnitude()).intValue();
        if ("a".equals(dvQuantity.getUnits())) {
            return Period.ofYears(magnitude);
        } else if ("mo".equals(dvQuantity.getUnits())) {
            return Period.ofMonths(magnitude);
        } else if ("d".equals(dvQuantity.getUnits())) {
            return Period.ofDays(magnitude);
        } else if ("h".equals(dvQuantity.getUnits())) {
            return HOUR_IN_MILLISECONDS * magnitude;
        }
        throw new UnsupportedOperationException("Unsupported time period unit: " + dvQuantity.getUnits());
    }

    private boolean evaluateBooleanExpression(ExpressionItem whenStatement, Map<String, List<Object>> input,
                                              GuideOntology guideOntology, Set<String> firedRules) {
        Object value = evaluateExpressionItem(whenStatement, input, guideOntology, firedRules);
        return value instanceof Boolean && ((Boolean) value);
    }

    private Object processFunctionalExpression(FunctionalExpression functionalExpression, Map<String,
            List<Object>> input, GuideOntology ontology, Set<String> firedRules) {
        String function = functionalExpression.getFunction().toString();
        Double value = Double.valueOf(evaluateExpressionItem(functionalExpression.getItems().get(0), input, ontology, firedRules).toString());
        if ("abs".equalsIgnoreCase(function)) {
            return Math.abs(value);
        } else if ("ceil".equalsIgnoreCase(function)) {
            return Math.ceil(value);
        } else if ("exp".equalsIgnoreCase(function)) {
            return Math.exp(value);
        } else if ("floor".equalsIgnoreCase(function)) {
            return Math.floor(value);
        } else if ("log".equalsIgnoreCase(function)) {
            return Math.log(value);
        } else if ("log10".equalsIgnoreCase(function)) {
            return Math.log10(value);
        } else if ("log1p".equalsIgnoreCase(function)) {
            return Math.log1p(value);
        } else if ("round".equalsIgnoreCase(function)) {
            return Math.round(value);
        } else if ("sqrt".equalsIgnoreCase(function)) {
            return Math.sqrt(value);
        } else {
            throw new UnsupportedOperationException("Unsupported function: " + function);
        }
    }

    private Object processUnaryExpression(UnaryExpression unaryExpression, Set<String> firedRules) {
        if (OperatorKind.FIRED.equals(unaryExpression.getOperator())) {
            return firedRules.contains(((Variable) unaryExpression.getOperand()).getCode());
        } else if (OperatorKind.NOT_FIRED.equals(unaryExpression.getOperator())) {
            return !firedRules.contains(((Variable) unaryExpression.getOperand()).getCode());
        } else {
            throw new UnsupportedOperationException("Unsupported unary operation: " + unaryExpression);
        }
    }

    private Object processBinaryExpression(ExpressionItem expressionItem, Map<String, List<Object>> input,
                                           GuideOntology ontology, Set<String> firedRules) {
        BinaryExpression binaryExpression = (BinaryExpression) expressionItem;
        OperatorKind operator = binaryExpression.getOperator();
        ExpressionItem leftExpression = binaryExpression.getLeft();
        ExpressionItem rightExpression = binaryExpression.getRight();
        if (operator == OR) {
            if (leftExpression == null) {
                throw new IllegalArgumentException("Null value in left expression item with OR operator: " + expressionItem);
            }
            return evaluateBooleanExpression(leftExpression, input, ontology, firedRules)
                    || evaluateBooleanExpression(rightExpression, input, ontology, firedRules);
        }
        Object leftValue = leftExpression == null ? null : evaluateExpressionItem(leftExpression, input, ontology, firedRules);
        Object rightValue = rightExpression == null ? null : evaluateExpressionItem(rightExpression, input, ontology, firedRules);
        if (leftValue instanceof Period || rightValue instanceof Period) {
            return evaluateDateTimeExpression(operator, leftValue, rightValue);
        } else if (isArithmeticOperator(operator)) {
            return evaluateArithmeticExpression(operator, leftValue, rightValue, expressionItem);
        } else if (operator == EQUALITY) {
            return evaluateEqualityExpression(leftValue, rightValue);
        } else if (operator == INEQUAL) {
            return !(leftValue == null && rightValue == null) && (leftValue == null || !leftValue.equals(rightValue));
        } else if (operator == IS_A) {     // todo IS_A_NOT
            return evaluateIsARelationship(leftValue, rightValue, ontology);
        } else if (operator == AND && leftValue != null && rightValue != null) {
            return (Boolean) leftValue && (Boolean) rightValue;
        } else {
            throw new IllegalArgumentException("Unsupported operator in expressionItem: " + expressionItem + ", leftValue: " + leftValue + ", rightValue: " + rightValue);
        }
    }

    private LocalDateTime systemCurrentDateTime() {
        return ((DvDateTime) systemParameters.get(CURRENT_DATETIME)).getDateTime();
    }

    private Object evaluateDateTimeExpression(OperatorKind operator, Object leftValue, Object rightValue) {
        if (leftValue instanceof Period && rightValue instanceof Period) {
            Period periodLeft = (Period) leftValue;
            Period periodRight = (Period) rightValue;
            LocalDateTime localDateTime = systemCurrentDateTime();
            LocalDateTime localDateTimeLeft = localDateTime.plus(periodLeft);
            LocalDateTime localDateTimeRight = localDateTime.plus(periodRight);
            if (operator == GREATER_THAN) {
                return localDateTimeLeft.isAfter(localDateTimeRight);
            } else if (operator == GREATER_THAN_OR_EQUAL) {
                return localDateTimeLeft.isAfter(localDateTimeRight) || localDateTimeLeft.equals(localDateTimeRight);
            } else if (operator == LESS_THAN) {
                return localDateTimeLeft.isBefore(localDateTimeRight);
            } else if (operator == LESS_THAN_OR_EQUAL) {
                return localDateTimeLeft.isBefore(localDateTimeRight) || localDateTimeLeft.equals(localDateTimeRight);
            } else if (operator == EQUALITY) {
                return localDateTimeLeft.equals(localDateTimeRight);
            } else {
                throw new UnsupportedOperationException("Unsupported combination of operator for two periods: " + operator);
            }
        } else if (operator == ADDITION || operator == SUBTRACTION) {
            Period period;
            Long longValue;
            if (rightValue instanceof Period) {
                period = (Period) rightValue;
                longValue = (Long) leftValue;
            } else {
                period = (Period) leftValue;
                longValue = (Long) rightValue;
            }
            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(longValue), ZoneId.systemDefault());
            return operator == ADDITION ? localDateTime.plus(period) : localDateTime.minus(period);
        } else if (rightValue == null) {
            return operator == NOT;
        } else if (operator == DIVISION && rightValue instanceof Period && leftValue instanceof Double) {
            // special case when datetime.value is divided by period (1,a)
            LocalDateTime localDateTime = systemCurrentDateTime();
            LocalDateTime localDateTimeWithPeriod = localDateTime.plus((Period) rightValue);
            double rightValueDouble = Long.valueOf(ChronoUnit.MILLIS.between(localDateTime, localDateTimeWithPeriod)).doubleValue();
            return ((Double) leftValue) / rightValueDouble;
        }
        throw new UnsupportedOperationException("Unsupported combination of left: "
                + leftValue + ", right: " + rightValue + ", operator: " + operator);
    }

    private boolean evaluateEqualityExpression(Object leftValue, Object rightValue) {
        if (leftValue == null && rightValue == null) {
            return true;
        } else if (leftValue != null) {
            if (leftValue instanceof DvCount) {
                if (rightValue instanceof String) {
                    return ((DvCount) leftValue).getMagnitude() == Integer.parseInt((String) rightValue);
                } else if (rightValue instanceof Integer) {
                    return ((DvCount) leftValue).getMagnitude() == (Integer) rightValue;
                }
            } else if ((leftValue instanceof DvBoolean && rightValue != null)) {
                boolean rightValueBoolean = Boolean.valueOf(rightValue.toString());
                return ((DvBoolean) leftValue).getValue() == rightValueBoolean;
            } else if (leftValue instanceof DvQuantity) {
                leftValue = evaluateQuantityValue((DvQuantity) leftValue);
            } else if (rightValue instanceof Double) {
                Double leftValueDouble = Double.valueOf(leftValue.toString());
                return leftValueDouble.equals(rightValue);
            }
            return leftValue.equals(rightValue);
        } else {
            return false;
        }
    }

    private Object evaluateArithmeticExpression(OperatorKind operator, Object leftValue, Object rightValue, ExpressionItem expressionItem) {
        if ((leftValue == null || rightValue == null)) {
            if (isLogicalOperator(operator)) {
                return false;
            } else {
                throw new IllegalArgumentException("Null value in expression item: " + expressionItem + ", leftValue: " + leftValue + ", rightValue: " + rightValue);
            }
        }
        try {
            double left = convertObjectValueToDouble(leftValue);
            double right = convertObjectValueToDouble(rightValue);
            switch (operator) {
                case ADDITION:
                    return left + right;
                case SUBTRACTION:
                    return left - right;
                case MULTIPLICATION:
                    return left * right;
                case DIVISION:
                    return left / right;
                case EXPONENT:
                    return Math.pow(left, right);
                case GREATER_THAN:
                    return left > right;
                case GREATER_THAN_OR_EQUAL:
                    return left >= right;
                case LESS_THAN:
                    return left < right;
                case LESS_THAN_OR_EQUAL:
                    return left <= right;
                default:
                    throw new IllegalArgumentException("Unexpected operator type in expressionItem: " + operator);
            }
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Unexpected double value in expressionItem: " + expressionItem + ". leftValue: " + leftValue + ", rightValue: " + rightValue);
        }
    }

    private boolean isArithmeticOperator(OperatorKind operator) {
        return operator == ADDITION
                || operator == SUBTRACTION
                || operator == MULTIPLICATION
                || operator == DIVISION
                || operator == EXPONENT
                || operator == GREATER_THAN
                || operator == GREATER_THAN_OR_EQUAL
                || operator == LESS_THAN
                || operator == LESS_THAN_OR_EQUAL;
    }

    private boolean isLogicalOperator(OperatorKind operatorKind) {
        return operatorKind == GREATER_THAN
                || operatorKind == GREATER_THAN_OR_EQUAL
                || operatorKind == LESS_THAN
                || operatorKind == LESS_THAN_OR_EQUAL;
    }

    private double convertObjectValueToDouble(Object dataValue) {
        if (dataValue instanceof DvQuantity) { // TODO handle units
            return Double.valueOf(evaluateQuantityValue((DvQuantity) dataValue).toString());
        } else if (dataValue instanceof DvDateTime) {
            return ((DvDateTime) dataValue).getDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (dataValue instanceof LocalDateTime) {
            return ((LocalDateTime) dataValue).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        } else if (dataValue.toString().startsWith("(-") && dataValue.toString().endsWith(")")) {
            int length = dataValue.toString().length();
            return Double.valueOf(dataValue.toString().substring(1, length - 1));
        } else {
            return Double.valueOf(dataValue.toString());
        }
    }

    private boolean evaluateIsARelationship(Object leftValue, Object rightValue, GuideOntology ontology) {
        if (!(leftValue instanceof DvCodedText) || !(rightValue instanceof DvCodedText)) {
            return false;
        }
        CodePhrase codedTextDefiningCode = ((DvCodedText) leftValue).getDefiningCode();
        CodePhrase bindingDefiningCode = ((DvCodedText) rightValue).getDefiningCode();
        String terminology = codedTextDefiningCode.getTerminology();
        TermBinding termBinding = ontology.getTermBindings().get(terminology);
        if (termBinding == null) {
            return false;
        }
        Binding binding = termBinding.getBindings().get(bindingDefiningCode.getCode());
        return binding != null && binding.getCodes().stream()
                .map(CodePhrase::getCode)
                .anyMatch(bindingCode -> codedTextDefiningCode.getCode().startsWith(bindingCode));
    }

    private Object evaluateAggregationSum(Variable variable, Map<String, List<Object>> valueMap) {
        String key = variable.getCode();
        List<Object> valueList = valueMap.get(key);
        if (valueList == null || valueList.isEmpty()) {
            return 0;
        }
        Object first = valueList.get(0);
        if (first instanceof DvCount) {
            int sum = 0;
            for (Object dataValue : valueList) {
                sum += ((DvCount) dataValue).getMagnitude();
            }
            return sum;
        } else if (first instanceof DvQuantity) {
            double sum = 0;
            for (Object dataValue : valueList) {
                sum += ((DvQuantity) dataValue).getMagnitude();
            }
            return sum;
        } else if (first instanceof DvOrdinal) {
            int sum = 0;
            for (Object dataValue : valueList) {
                sum += ((DvOrdinal) dataValue).getValue();
            }
            return sum;
        } else {
            throw new IllegalArgumentException("Supported data type for sum(): " + first.getClass());
        }
    }

    private Object retrieveValueFromValueMap(Variable variable, Map<String, List<Object>> valueMap) {
        String key = variable.getCode() != null ? variable.getCode() : variable.getPath();

        if (COUNT.equals(variable.getAttribute())) {
            return valueMap.getOrDefault(key, Collections.emptyList()).size();
        } else if (SUM.equals(variable.getAttribute())) {
            return evaluateAggregationSum(variable, valueMap);
        }
        if (key.endsWith("/value/value")) {
            key = key.substring(0, key.length() - 12);
        }
        Object dataValue;
        if (CURRENT_DATETIME.equals(variable.getCode())) {
            dataValue = systemParameters.get(CURRENT_DATETIME);
        } else {
            List<Object> valueList = valueMap.get(key);
            if (valueList == null) {
                return TypeBinding.MAGNITUDE.equals(variable.getAttribute()) ? 0.0 : null; // backwards compatibility
            }
            dataValue = valueList.get(valueList.size() - 1);
        }
        String attribute = variable.getAttribute();
        if (attribute == null) {
            if (dataValue instanceof DvQuantity) {
                DvQuantity dvQuantity = (DvQuantity) dataValue;
                if (isTimePeriodUnits(dvQuantity.getUnits())) {
                    return convertTimeQuantityToPeriodOrMilliSeconds(dvQuantity);
                }
            }
            return dataValue;
        }
        if (TypeBinding.VALUE.equals(attribute) && dataValue instanceof DvDateTime) {
            return ((DvDateTime) dataValue).getDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        try {
            String getterName = "get" + attribute.substring(0, 1).toUpperCase() + attribute.substring(1);
            Method method = dataValue.getClass().getMethod(getterName);
            return method.invoke(dataValue);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalArgumentException("Failed to retrieve attribute [" + attribute + "] value for variable: " + variable);
        }
    }

    private List<DataInstance> filterDataInstancesWithModelId(List<DataInstance> dataInstances, String modelId) {
        return dataInstances.stream()
                .filter(s -> modelId.equals(s.modelId()))
                .collect(Collectors.toList());
    }

    private List<DataInstance> evaluateMinOrMaxFunction(List<DataInstance> dataInstances, UnaryExpression unaryExpression, boolean minFunction) {
        DataInstance found = null;
        long milliseconds = 0;
        for (DataInstance dataInstance : dataInstances) {
            Object value = evaluateExpressionItem(unaryExpression.getOperand(), dataInstance.valueListMap());
            if (value instanceof DvDateTime) {
                DvDateTime dvDateTime = (DvDateTime) value;
                long convertedDateTime = dvDateTime.getDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                if (minFunction) {
                    if (found == null || convertedDateTime < milliseconds) {
                        found = dataInstance;
                        milliseconds = convertedDateTime;
                    }
                } else { // maxFunction
                    if (found == null || convertedDateTime > milliseconds) {
                        found = dataInstance;
                        milliseconds = convertedDateTime;
                    }
                }
            }
        }
        return found == null ? Collections.emptyList() : Collections.singletonList(found);
    }

    List<DataInstance> evaluateDataInstancesWithPredicate(List<DataInstance> dataInstances, List<ExpressionItem> predicateStatements,
                                                          GuideOntology guideOntology) {
        if (predicateStatements == null) {
            return dataInstances;
        }
        List<DataInstance> dataInstanceList = dataInstances;
        for (ExpressionItem expressionItem : predicateStatements) {
            dataInstanceList = evaluateDataInstancesWithPredicate(dataInstanceList, expressionItem, guideOntology, null);
        }
        return dataInstanceList;
    }

    List<DataInstance> evaluateDataInstancesWithPredicate(
            List<DataInstance> dataInstances,
            ExpressionItem predicateStatement,
            GuideOntology guideOntology,
            Set<String> firedRules) {
        if (predicateStatement instanceof UnaryExpression) {
            UnaryExpression unaryExpression = (UnaryExpression) predicateStatement;
            if (OperatorKind.MAX == unaryExpression.getOperator()) {
                return evaluateMinOrMaxFunction(dataInstances, unaryExpression, false);
            } else if (OperatorKind.MIN == unaryExpression.getOperator()) {
                return evaluateMinOrMaxFunction(dataInstances, unaryExpression, true);
            }
        } else if (predicateStatement instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) predicateStatement;
            String path = ((Variable) binaryExpression.getLeft()).getPath();
            if (IS_A == binaryExpression.getOperator()) {
                return dataInstances.stream()
                        .filter(dataInstance -> evaluateIsARelationship(
                                dataInstance.get(path),
                                evaluateExpressionItem(binaryExpression.getRight(), dataInstance.valueListMap(), guideOntology, firedRules), guideOntology))
                        .collect(Collectors.toList());
            } else {
                return dataInstances.stream()
                        .filter(s -> evaluateBooleanExpression(binaryExpression, s.valueListMap(), guideOntology, firedRules))
                        .collect(Collectors.toList());
            }
        }
        throw new IllegalArgumentException("Unsupported operator in predicateStatement: " + predicateStatement);
    }
}

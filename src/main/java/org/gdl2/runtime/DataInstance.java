package org.gdl2.runtime;

import org.gdl2.datatypes.*;

import java.util.*;

/**
 * A simple data structure that matches the ArchetypeBinding in the instance space both as input
 * and output of GDL guideline executions.
 */
public class DataInstance {
    private String modelId;
    private Map<String, Object> values; // values indexed by path

    private DataInstance() {
        this.values = new HashMap<>();
    }

    DataInstance(String modelId) {
        this();
        this.modelId = modelId;
    }

    private static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void assertTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public boolean contains(String key) {
        assertNotNull(key, "Null key");
        return this.values.containsKey(key);
    }

    public Object get(String key) {
        assertNotNull(key, "Null key");
        return this.values.get(key);
    }

    public Map<String, Object> values() {
        return Collections.unmodifiableMap(this.values);
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public void merge(DataInstance dataInstance) {
        if (dataInstance.modelId.equals(this.modelId)) {
            this.values.putAll(dataInstance.values);
        }
    }

    public int size() {
        return this.values.size();
    }

    public Map<String, List<Object>> valueListMap() {
        Map<String, List<Object>> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : this.values.entrySet()) {
            List<Object> list = new ArrayList<>();
            list.add(entry.getValue());
            result.put(entry.getKey(), list);
        }
        return result;
    }

    public DvCount getDvCount(String key) {
        check(key);
        return (DvCount) this.values.get(key);
    }

    public DvBoolean getDvBoolean(String key) {
        check(key);
        return (DvBoolean) this.values.get(key);
    }

    public DvCodedText getDvCodedText(String key) {
        check(key);
        return (DvCodedText) this.values.get(key);
    }

    public DvText getDvText(String key) {
        check(key);
        return (DvText) this.values.get(key);
    }

    public DvQuantity getDvQuantity(String key) {
        check(key);
        return (DvQuantity) this.values.get(key);
    }

    public DvOrdinal getDvOrdinal(String key) {
        check(key);
        return (DvOrdinal) this.values.get(key);
    }

    public DvDateTime getDvDateTime(String key) {
        check(key);
        return (DvDateTime) this.values.get(key);
    }

    /**
     * Set keyed value of dataInstance.
     *
     * @param key       not null
     * @param dataValue null value if ignored
     */
    public void setValue(String key, Object dataValue) {
        assertNotNull(key, "Null key");
        assertNotNull(dataValue, "Null dataValue");
        this.values.put(key, dataValue);
    }

    private void check(String key) {
        assertNotNull(key, "Null key");
        assertTrue(this.values.containsKey(key), "Missing value for key: " + key);
    }

    public String modelId() {
        return this.modelId;
    }

    /**
     * Get the object at root path.
     *
     * @return the object or null if not found
     */
    public Object getRoot() {
        return get("/");
    }

    public static class Builder {
        private DataInstance dataInstance = new DataInstance();

        public Builder modelId(String modelId) {
            assertNotNull(modelId, "Null modelId");
            dataInstance.modelId = modelId;
            return this;
        }

        /**
         * Add keyed value of dataInstance.
         *
         * @param key       not null
         * @param dataValue ignored if null value
         */
        public Builder addValue(String key, Object dataValue) {
            assertNotNull(key, "Null key");
            if (dataValue != null) {
                dataInstance.values.put(key, dataValue);
            }
            return this;
        }

        public DataInstance build() {
            assertNotNull(dataInstance.modelId, "modelId");
            return dataInstance;
        }
    }
}

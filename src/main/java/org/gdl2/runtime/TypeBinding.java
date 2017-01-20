package org.gdl2.runtime;

import org.gdl2.datatypes.DvCount;
import org.gdl2.datatypes.DvQuantity;

import java.util.Set;

/**
 * Try to find correct type by inspecting attributes.
 */
class TypeBinding {
    static final String VALUE = "value";
    static final String PRECISION = "precision";
    static final String MAGNITUDE = "magnitude";
    static final String UNITS = "units";
    static final String NULL_FLAVOR = "null_flavor";

    Class possibleType(Set<String> attributes) {
        if (attributes.contains(PRECISION) || attributes.contains(UNITS)) {
            return DvQuantity.class;
        } else if (attributes.contains(MAGNITUDE)) {
            return DvCount.class;
        }
        return Object.class;
    }
}

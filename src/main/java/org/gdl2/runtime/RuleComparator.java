package org.gdl2.runtime;

import org.gdl2.model.Rule;

import java.io.Serializable;
import java.util.Comparator;

class RuleComparator implements Comparator<Rule>, Serializable {
    @Override
    public int compare(Rule o1, Rule o2) {
        return o2.getPriority() - o1.getPriority();
    }
}

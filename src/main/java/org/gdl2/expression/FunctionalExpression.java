package org.gdl2.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class FunctionalExpression extends ExpressionItem {
    private Function function;
    private List<ExpressionItem> items;

    public FunctionalExpression(Function function) {
        this(function, null);
    }

    public FunctionalExpression(Function function, List<ExpressionItem> items) {
        if (function == null) {
            throw new IllegalArgumentException("null function");
        }
        this.function = function;
        if (items != null) {
            this.items = new ArrayList<>(items);
        }
    }

    public static FunctionalExpression create(Function function) {
        return new FunctionalExpression(function);
    }

    public static FunctionalExpression create(Function function, ExpressionItem item) {
        List<ExpressionItem> items = new ArrayList<>();
        items.add(item);
        return new FunctionalExpression(function, items);
    }

    public static FunctionalExpression create(Function function, List<ExpressionItem> items) {
        return new FunctionalExpression(function, items);
    }

    public Function getFunction() {
        return function;
    }


    /**
     * String representation of this expression using the following format:
     *
     * <p>function() without any variables
     *
     * <p>or
     *
     * <p>function(var1, var2..)
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(function);
        buf.append("(");
        if (items != null) {
            for (int i = 0, j = items.size(); i < j; i++) {
                ExpressionItem item = items.get(i);
                if (item instanceof BinaryExpression) {
                    buf.append("(");
                    buf.append(item.toString());
                    buf.append(")");
                } else {
                    buf.append(item.toString());
                }
                if (i != j - 1) {
                    buf.append(",");
                }
            }
        }
        buf.append(")");
        return buf.toString();
    }

    public List<ExpressionItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        FunctionalExpression that = (FunctionalExpression) other;
        return Objects.equals(function, that.function)
                && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, items);
    }
}
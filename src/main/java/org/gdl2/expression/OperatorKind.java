package org.gdl2.expression;

public enum OperatorKind {

    /**
     * Arithmetic operators.
     */
    ADDITION("addition", "+"),
    SUBSTRATION("substraction", "-"),
    MULTIPLICATION("multiplication", "*"),
    DIVISION("division", "/"),
    EXPONENT("exponent", "^"),

    /**
     * Logical operators.
     */
    AND("and", "&&"),
    OR("or", "||"),
    NOT("not", "!"),

    /**
     * Relational operators.
     */
    EQUALITY("equal", "=="),
    INEQUAL("unequal", "!="),
    LESS_THAN("less than", "<"),
    LESS_THAN_OR_EQUAL("less than or equals", "<="),
    GREATER_THAN("greater than", ">"),
    GREATER_THAN_OR_EQUAL("greater than or equals", ">="),

    /**
     * Assignment operator.
     */
    ASSIGNMENT("assignment", "="),

    /**
     * Terminological reasoning.
     */
    IS_A("is_a", "is_a"),
    IS_NOT_A("is_not_a", "!is_a"),

    /**
     * Conditional operators.
     */
    FOR_ALL("for all", "for_all"),
    MAX("max", "max"),
    MIN("min", "min"),
    FIRED("fired", "fired"),
    NOT_FIRED("!fired", "!fired");


    private String name;
    private String symbol;

    OperatorKind(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public String toString() {
        return name;
    }
}
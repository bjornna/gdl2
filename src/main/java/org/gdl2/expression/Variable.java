package org.gdl2.expression;

import java.util.Objects;

public class Variable extends ExpressionItem {
    private String name;
    private String code;
    private String path;
    private String attribute;

    public Variable(String code, String name, String path, String attribute) {
        this.name = name;
        this.code = code;
        this.path = path;
        this.attribute = attribute;
    }

    public Variable(String code, String name) {
        this(code, name, null);
    }

    public Variable(String code, String name, String path) {
        this(code, name, path, null);
    }

    public Variable(String code) {
        this.code = code;
    }

    public static Variable createByCode(String code) {
        return new Variable(code);
    }

    public static Variable createByPath(String path) {
        return new Variable(null, null, path);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getPath() {
        return path;
    }

    /**
     * String representation of a variable as one of the following 1. code 2.
     * code|name| 3. code.attribute 4. code|name|.attribute 5. path
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (code != null) {
            buf.append("$");
            buf.append(code);
            if (name != null) {
                buf.append("|");
                buf.append(name);
                buf.append("|");
            }
            if (attribute != null) {
                buf.append(".");
                buf.append(attribute);
            }

        } else {
            buf.append(path);
        }
        return buf.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Variable variable = (Variable) other;
        return Objects.equals(name, variable.name)
                && Objects.equals(code, variable.code)
                && Objects.equals(path, variable.path)
                && Objects.equals(attribute, variable.attribute);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, code, path, attribute);
    }

    public String getAttribute() {
        return attribute;
    }

}

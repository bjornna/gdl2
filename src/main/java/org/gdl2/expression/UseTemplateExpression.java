package org.gdl2.expression;

import lombok.Value;

@Value
public class UseTemplateExpression extends ExpressionItem {
    private Variable variable;
}

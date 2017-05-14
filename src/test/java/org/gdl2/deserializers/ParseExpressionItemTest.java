package org.gdl2.deserializers;


import org.gdl2.expression.ExpressionItem;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;

public class ParseExpressionItemTest {

    @Test
    public void can_parse_expression_item() throws Exception {
        ExpressionItemDeserializer expressionItemDeserializer = new ExpressionItemDeserializer();
        ExpressionItem expressionItem = expressionItemDeserializer.parse("max(/data/events/time)");
        assertNotNull(expressionItem);
    }
}
